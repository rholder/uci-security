package edu.baylor.cs.holder.security.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.baylor.cs.holder.security.service.AccessService;
import edu.baylor.cs.holder.security.service.DomainService;
import edu.baylor.cs.holder.security.service.SecurityService;
import edu.baylor.cs.holder.security.service.accessobjects.AccessRuleKey;
import edu.baylor.cs.holder.security.service.accessobjects.RoleMappingKey;
import edu.baylor.cs.holder.security.service.accessobjects.User;

public class SecurityServiceImpl implements SecurityService {
    
    // maximum length of a context array to prevent denial of service attacks
    private static final int MAXCONTEXTLENGTH = 5;
    
    // empty object array
    private static final Object[] EMTPYARRAY = new Object[0];
    
    private Log log = LogFactory.getLog(getClass());
    
    // service used to retrieve role mappings and access rule roles
    protected AccessService accessService;
    
    // service used to retrieve domain objects
    protected DomainService domainService;
    
    public boolean hasRole(User user, String roleName) {
        try {
            return getAllRolesForUser(user).contains(roleName);
        } catch (IllegalArgumentException e) {
            if(log.isErrorEnabled()) {
                log.error("Invalid role name: " + roleName, e);
            }
            return false;
        }
    }
    
    public Set<String> getAllRolesForUser(User user) {
        
        if (user == null) {
            // no roles if you're not logged in
            return Collections.emptySet();
        }
        
        // get roles UID, *, *
        return accessService.getUserRoles(new RoleMappingKey(user.getId(), null, null));
    }
    
    public boolean hasAccess(String category, String action, User user, Object... context) {
        
        // treat a null context array as an empty Object array
        if (context == null) {
            context = EMTPYARRAY;
        }
        
        // process the context array and return the types
        List<Class<?>> contextTypes = processContext(context);
        
        // roles the user must have according to the access rules
        AccessRuleKey accessRuleKey = new AccessRuleKey(category, action, contextTypes);
        Set<String> accessRuleRoles = accessService.getAccessRuleRoles(accessRuleKey);
        
        // If there are no valid roles then don't process any further
        if (accessRuleRoles.size() == 0) {
            return false;
        }
        
        // roles the user has
        Set<String> personRoles = new HashSet<String>();
        
        // roles that may be vetoed by the access service
        Set<String> vetoRoles = new HashSet<String>();
        
        if (context.length == 0) {
            // no context
            
            // add roles UID, *, *
            RoleMappingKey roleMappingKey = new RoleMappingKey(user.getId(), null, null);
            personRoles.addAll(accessService.getUserRoles(roleMappingKey));
            
            // Add roles that may be vetoed
            vetoRoles.addAll(accessService.getVetoRoles(accessRuleKey,
                    roleMappingKey));
        } else {
            // Don't process a context array greater than this to lighten a
            // possible denial of service attack via web services integration in
            // the future
            if (context.length > MAXCONTEXTLENGTH) {
                throw new IllegalArgumentException("Context array has exceeded maximum length.");
            }
            
            // Get all available roles, for an intersect operation
            Set<String> contextRoles = new HashSet<String>(accessService.getAllAvailableRoles());
            
            // You must have the required allowed role for each
            // object in the context in order for the role to be valid for the
            // current action to be permitted.
            
            Iterator<Class<?>> types = contextTypes.iterator();
            for (Object c : context) {
                Long entityId = domainService.getId(c);
                Class<?> entityType = types.next();
                
                // Add roles PID, EntityType, EntityID
                
                // Compute intersection of roles derived from each object in
                // the context.
                RoleMappingKey roleMappingKey = new RoleMappingKey(user.getId(), entityType, entityId);
                contextRoles.retainAll(accessService.getUserRoles(roleMappingKey));
                
                // Add roles that may be vetoed
                vetoRoles.addAll(accessService.getVetoRoles(accessRuleKey, roleMappingKey));
            }
            
            // Add roles derived from the context to the Person's roles
            personRoles.addAll(contextRoles);
        }
        
        // Remove the roles that have been vetoed
        personRoles.removeAll(vetoRoles);
        
        // Intersect with the allowed roles
        personRoles.retainAll(accessRuleRoles);
        
        // An empty set at this point means the current Person had no allowed
        // roles according to the access rule
        return !personRoles.isEmpty();
    }
    
    /**
     * Extensions of this method can be used to modify the list of returned
     * types or the contents of the object array in place.
     * 
     * @param context
     * @return
     */
    protected List<Class<?>> processContext(Object... context) {
        return getContextTypes(context);
    }
    
    /**
     * Return a list of expected class types derived from the array of context.
     * 
     * @param context
     *            Object array to derive types from
     * @return
     */
    private List<Class<?>> getContextTypes(Object... context) {
        List<Class<?>> contextTypes = new ArrayList<Class<?>>();
        for (Object o : context) {
            if (o != null) {
                contextTypes.add(o.getClass());
            } else {
                // Null context variable detected
                contextTypes.add(null);
            }
        }
        return contextTypes;
    }

    public void setAccessService(AccessService accessService) {
        this.accessService = accessService;
    }

    public void setDomainService(DomainService domainService) {
        this.domainService = domainService;
    }
}
