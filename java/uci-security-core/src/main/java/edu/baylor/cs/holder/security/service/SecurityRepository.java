package edu.baylor.cs.holder.security.service;

import java.util.Set;

import edu.baylor.cs.holder.security.service.accessobjects.AccessRule;
import edu.baylor.cs.holder.security.service.accessobjects.RoleMapping;

/**
 * Implementations of this interface provide access to {@link AccessRule}'s,
 * {@link RoleMapping}'s, and available roles.
 * 
 * @author rholder
 */
public interface SecurityRepository {

    /**
     * Return a {@link Set} of {@link AccessRule}'s.
     * 
     * @return
     */
    public Set<AccessRule> getAccessRules();

    /**
     * Return a {@link Set} of {@link RoleMapping}'s.
     * 
     * @return
     */
    public Set<RoleMapping> getRoleMappings();

    /**
     * Return a {@link Set} of all available roles.
     * 
     * @return
     */
    public Set<String> getAllRoles();

    /**
     * Return a {@link Set} of all context packages.
     * 
     * @return
     */
    public Set<String> getContextPackages();
}
