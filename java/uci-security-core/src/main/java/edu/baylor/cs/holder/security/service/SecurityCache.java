package edu.baylor.cs.holder.security.service;

import java.util.Map;
import java.util.Set;

import edu.baylor.cs.holder.security.service.accessobjects.AccessRule;
import edu.baylor.cs.holder.security.service.accessobjects.AccessRuleKey;
import edu.baylor.cs.holder.security.service.accessobjects.RoleMappingKey;
import edu.baylor.cs.holder.security.util.Pair;


/**
 * This class provides various security related caches to improve the
 * performance of security access checks.
 * 
 * @author holder
 * 
 */
public interface SecurityCache {
    
    /**
     * Return the user role cache, useful for AccessService.getUserRoles().
     * 
     * @return
     */
    public Map<RoleMappingKey, Set<String>> getUserRoleCache();

    /**
     * Return the veto role cache, useful for AccessService.getVetoRoles().
     * 
     * @return
     */
    public Map<Pair<AccessRuleKey, RoleMappingKey>, Set<String>> getVetoRoleCache();

    /**
     * Return the access rule role cache, useful for
     * AccessService.getAccessRuleRoles().
     * 
     * @return
     */
    public Map<AccessRule, Set<String>> getAccessRuleCache();

    /**
     * Clear all caches held by this instance.
     */
    public void clear();
}
