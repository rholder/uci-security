package edu.baylor.cs.holder.security.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.baylor.cs.holder.security.service.AccessService;
import edu.baylor.cs.holder.security.service.SecurityCache;
import edu.baylor.cs.holder.security.service.accessobjects.AccessRule;
import edu.baylor.cs.holder.security.service.accessobjects.AccessRuleKey;
import edu.baylor.cs.holder.security.service.accessobjects.RoleMappingKey;
import edu.baylor.cs.holder.security.util.Pair;

/**
 * This class merges derived roles. Calls to methods in this class represent the
 * union of the sets of all underlying access services configured here. It also
 * makes use of the security cache to speed up expensive lookups.
 * 
 * TODO Extensions of this class should override getSecurityCache() and provide
 * a caching backend appropriate for their integration, such as an HTTP session
 * based cache that resets on user logout.
 * 
 * @author holder
 */
public class GenericCachingAccessServiceImpl implements AccessService {

    /**
     * UID
     */
    private static final long serialVersionUID = 8334539489269348330L;

    // List of each access service we use when retrieving access rules and role
    // mappings
    private List<AccessService> accessServices = new ArrayList<AccessService>();

    public void setAccessServices(List<AccessService> accessServices) {
        this.accessServices = accessServices;
    }

    public Set<String> getAccessRuleRoles(AccessRuleKey key) {
        Set<String> roles = new HashSet<String>();
        for (AccessService service : accessServices) {
            roles.addAll(service.getAccessRuleRoles(key));
        }
        return roles;
    }

    public Set<String> getUserRoles(RoleMappingKey key) {

        // Use the cached version if it exists
        Map<RoleMappingKey, Set<String>> cache = getSecurityCache().getUserRoleCache();
        Set<String> roles = cache.get(key);
        if (roles == null) {
            roles = new HashSet<String>();
            for (AccessService service : accessServices) {
                roles.addAll(service.getUserRoles(key));
            }
            // Add this lookup to the cache
            cache.put(key, roles);
        }
        return roles;
    }

    public Set<String> getVetoRoles(AccessRuleKey accessRuleKey, RoleMappingKey roleMappingKey) {
        
        // Use the cached version if it exists
        Map<Pair<AccessRuleKey, RoleMappingKey>, Set<String>> cache = getSecurityCache().getVetoRoleCache();

        // Check the cache
        Pair<AccessRuleKey, RoleMappingKey> lookupKey = new Pair<AccessRuleKey, RoleMappingKey>(accessRuleKey,
                roleMappingKey);
        Set<String> roles = cache.get(lookupKey);
        if (roles == null) {
            roles = new HashSet<String>();

            for (AccessService service : accessServices) {
                roles.addAll(service.getVetoRoles(accessRuleKey, roleMappingKey));
            }
            // Add this lookup to the cache
            cache.put(lookupKey, roles);
        }
        return roles;
    }
    
    public Set<String> getAllAvailableRoles() {
        // TODO cache this?
        Set<String> roles = new HashSet<String>();
        for (AccessService service : accessServices) {
            roles.addAll(service.getAllAvailableRoles());
        }
        return roles;
    }
    
    public Set<String> getContextPackages() {
        // TODO cache this?
        Set<String> packages = new HashSet<String>();
        for(AccessService service : accessServices) {
            packages.addAll(service.getContextPackages());
        }
        return packages;
    }
	
    // FIXME decide how to pull out the caching functionality
    // properly, for now just extend the class and replace this call
	protected SecurityCache getSecurityCache() {
	    return EMPTY_CACHE;
	}
    
	private static final SecurityCache EMPTY_CACHE = new SecurityCache() {

        public void clear() {
            // nothing really to clear here...
        }

        public Map<AccessRule, Set<String>> getAccessRuleCache() {
            return Collections.emptyMap();
        }

        public Map<RoleMappingKey, Set<String>> getUserRoleCache() {
            return Collections.emptyMap();
        }

        public Map<Pair<AccessRuleKey, RoleMappingKey>, Set<String>> getVetoRoleCache() {
            return Collections.emptyMap();
        }
    };
}
