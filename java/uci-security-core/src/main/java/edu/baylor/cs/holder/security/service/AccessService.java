package edu.baylor.cs.holder.security.service;

import java.util.Set;

import edu.baylor.cs.holder.security.service.accessobjects.AccessRuleKey;
import edu.baylor.cs.holder.security.service.accessobjects.RoleMappingKey;

/**
 * This interface is used to retrieve access rules and roles for given users for
 * evaluating user access.
 * 
 * @author holder
 */
public interface AccessService {

    /**
     * Return the access rule roles associated with the given category, action,
     * and context list. Return an empty set if no rule exists.
     * 
     * @param key
     *            key that contains name of the category, name of the action,
     *            and contexts list of context types used to identify the rule
     * @return access roles for the given category, action, and context list
     */
    public Set<String> getAccessRuleRoles(AccessRuleKey key);

    /**
     * Returns the roles that a user has with respect to the given context, both
     * included in the key. The key may contain wildcards in the form of null's
     * and will be matched accordingly depending on whether the implemented
     * access service can provide information for such wildcards. Totally
     * unmatched key's still return an empty Set.
     * 
     * @param key
     *            key intended to match the roles that are associated with the
     *            user and the context
     * @return the roles that a user has with respect to the given context
     *         contained in the key
     */
    public Set<String> getUserRoles(RoleMappingKey key);

    /**
     * Given the information provided in the AccessRuleKey and the
     * RoleMappingKey, determine which roles should be "vetoed," or removed from
     * the final set of granted roles. The "vetoed roles" will be returned here
     * based on some condition to be determined by implementations of this
     * method.
     * 
     * An example of a case where roles should be vetoed is when an Entry has
     * been certified for the World Finals. Normally, a person's roles such as
     * ROLE_TEAM_MANAGER or ROLE_CONTEST_MANAGER will be returned with respect
     * to the Entry from the getUserRoles() call, which is desired. However, in
     * the case of the World Finals, no one should be allowed to update a
     * certified Entry, thus roles returned for actions that can update should
     * be vetoed.
     * 
     * @param accessRuleKey
     * @param roleMappingKey
     * @return
     */
    public Set<String> getVetoRoles(AccessRuleKey accessRuleKey, RoleMappingKey roleMappingKey);
    
    /**
     * Return all available roles in the system.
     * 
     * @return
     */
    public Set<String> getAllAvailableRoles();
    
    /**
     * Return the {@link Set} of all context packages.
     * 
     * @return
     */
    public Set<String> getContextPackages();

}
