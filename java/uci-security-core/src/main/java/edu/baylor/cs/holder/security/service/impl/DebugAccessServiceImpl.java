package edu.baylor.cs.holder.security.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.baylor.cs.holder.security.service.AccessService;
import edu.baylor.cs.holder.security.service.accessobjects.AccessRuleKey;
import edu.baylor.cs.holder.security.service.accessobjects.RoleMappingKey;

/**
 * This service is used as a drop-in replacement for any access service. It
 * provides more detailed logging functionality but decreased performance. It
 * also does not make use of any caching of security lookups, allowing for full
 * debugging of each call to the security service.
 * 
 * @author holder
 */
public class DebugAccessServiceImpl implements AccessService {

    // List of each access service we use when retrieving access rules and role
    // mappings
    private List<AccessService> accessServices = new ArrayList<AccessService>();
    
    private Log log = LogFactory.getLog(getClass());

    public Set<String> getAccessRuleRoles(AccessRuleKey key) {
        Set<String> roles = new HashSet<String>();
        for (AccessService service : accessServices) {
            roles.addAll(service.getAccessRuleRoles(key));
        }
        logAllowedRoles(key, roles);
        return roles;
    }

    public Set<String> getUserRoles(RoleMappingKey key) {
        Set<String> roles = new HashSet<String>();
        for (AccessService service : accessServices) {
            roles.addAll(service.getUserRoles(key));
        }
        logGrantedRoles(key, roles);
        return roles;
    }
    
    public Set<String> getVetoRoles(AccessRuleKey accessRuleKey,
            RoleMappingKey roleMappingKey) {
        Set<String> roles = new HashSet<String>();
        for (AccessService service : accessServices) {
            roles.addAll(service.getVetoRoles(accessRuleKey, roleMappingKey));
        }
        logVetoedRoles(accessRuleKey, roleMappingKey, roles);
        return roles;
    }
    
    public Set<String> getAllAvailableRoles() {
        Set<String> roles = new HashSet<String>();
        for (AccessService service : accessServices) {
            roles.addAll(service.getAllAvailableRoles());
        }
        log.debug("All available roles: " + roles);
        return roles;
    }
    

    public Set<String> getContextPackages() {

        Set<String> packages = new HashSet<String>();
        for (AccessService service : accessServices) {
            packages.addAll(service.getContextPackages());
        }
        log.debug("All context packages: " + packages);
        return packages;
    }

    /**
     * Log output for the given RoleMappingKey and AccessRoles.
     * 
     * @param key
     * @param roles
     */
    private void logGrantedRoles(RoleMappingKey key, Set<String> roles) {
        StringBuilder result = new StringBuilder();
        String contextType = key.getContextType() == null ? null : key.getContextType().getSimpleName();

        result.append("Granted roles for (");
        result.append(key.getUserId() + ", " + contextType + ", " + key.getUserId() + "): ");

        for (String role : roles) {
            result.append(role.toString() + " ");
        }
        log.debug(result);
    }

    /**
     * Log output for the given AccessRuleKey and AccessRoles.
     * 
     * @param key
     * @param roles
     */
    private void logAllowedRoles(AccessRuleKey key, Set<String> roles) {
        StringBuilder result = new StringBuilder();
        result.append("Allowed roles for ");
        result.append(key.getCategory() + "." + key.getAction() + " " + key.getContexts() + ": ");
        for (String role : roles) {
            result.append(role.toString() + " ");
        }
        log.debug(result);
    }

    /**
     * Log output for the vetoed roles.
     *
     * @param accessRuleKey
     * @param roleMappingKey
     * @param roles
     */
    private void logVetoedRoles(AccessRuleKey accessRuleKey,
            RoleMappingKey roleMappingKey, Set<String> roles) {
        String contextType = roleMappingKey.getContextType() == null ? null : roleMappingKey.getContextType()
                .getSimpleName();
        StringBuilder result = new StringBuilder();
        result.append("Vetoed roles for " + accessRuleKey.getCategory() + ".");
        result.append(accessRuleKey.getAction() + " " + accessRuleKey.getContexts() + ": ");
        
        result.append("(" + roleMappingKey.getUserId() + ", " + contextType + ", ");
        result.append(roleMappingKey.getContextId() + "): ");
        
        for (String role : roles) {
            result.append(role.toString() + " ");
        }
        log.debug(result);
    }
}
