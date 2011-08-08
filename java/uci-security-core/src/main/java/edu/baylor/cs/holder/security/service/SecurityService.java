package edu.baylor.cs.holder.security.service;

import java.util.Set;

import edu.baylor.cs.holder.security.service.accessobjects.User;

/**
 * Implementations of this interface provide the user with a clear and simple
 * mechanism for evaluating permission to execute a certain action in a given
 * category on the user's access roles with respect to a given context.
 * 
 * This interface should handle all security needs necessary to provide context
 * based security. Other services are exposed as a convenience for possible
 * later use but for the most part, this class should be the only interface that
 * needs to be used under normal circumstances.
 * 
 * @author holder
 */
public interface SecurityService {

    /**
     * Determines whether the given user can access the specified action given
     * the context. Note that if no active rule exists for the given action,
     * this method returns false.
     * 
     * If the intersection of (the roles of the access rule associated with the
     * given category/action/context) and (the union of all of the users roles
     * with respect to the context and (the intersection of roles over each
     * given context for the given user)) is not empty, return true; otherwise
     * false.
     * 
     * @param category
     *            name of the category
     * @param action
     *            action in the given category
     * @param user
     *            user being checked for access
     * @param context
     *            objects being checked for access with respect to the user
     * @return true if access if allowed; false otherwise
     */
    public boolean hasAccess(String category, String action, User person, Object... context);

    /**
     * Returns true if the given user has the given role in any context.
     * 
     * @param roleName
     * @return true if the user has this role in any context
     */
    public boolean hasRole(User user, String roleName);
    
    
    /**
     * Returns set of all roles (UID, *, *) assigned to the given user.
     * 
     * @return set of all roles assigned to the given person in all contexts
     */
    public Set<String> getAllRolesForUser(User user);
}
