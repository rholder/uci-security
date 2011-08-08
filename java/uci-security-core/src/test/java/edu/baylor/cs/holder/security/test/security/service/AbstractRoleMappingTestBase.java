package edu.baylor.cs.holder.security.test.security.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import edu.baylor.cs.holder.security.service.accessobjects.AccessRuleKey;
import edu.baylor.cs.holder.security.service.accessobjects.RoleMappingKey;
import edu.baylor.cs.holder.security.test.model.Contest;
import edu.baylor.cs.holder.security.test.model.Institution;
import edu.baylor.cs.holder.security.test.security.AbstractSecurityTestBase;

public abstract class AbstractRoleMappingTestBase extends AbstractSecurityTestBase {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_CONTEST_MANAGER = "ROLE_CONTEST_MANAGER";
    private static final String ROLE_TEAM_MANAGER = "ROLE_TEAM_MANAGER";
    private static final String ROLE_TEAM_MEMBER = "ROLE_TEAM_MEMBER";
    private static final String ROLE_USER = "ROLE_USER";

    @Test
    public void testAccessRuleMatching() {
        
        checkAccessRuleMatch(createAccessRuleKey("institutionManager", "select"), ROLE_ADMIN, ROLE_CONTEST_MANAGER);
        checkAccessRuleMatch(createAccessRuleKey("institutionManager", "select", Institution.class), ROLE_ADMIN, ROLE_CONTEST_MANAGER, ROLE_TEAM_MEMBER);
        checkAccessRuleMatch(createAccessRuleKey("institutionManager", "save"), ROLE_ADMIN);
        checkAccessRuleMatch(createAccessRuleKey("institutionManager", "remove"), ROLE_ADMIN);
        checkAccessRuleMatch(createAccessRuleKey("institutionManager", "remove", Institution.class), ROLE_ADMIN);
        checkAccessRuleMatch(createAccessRuleKey("contestManager", "select", Contest.class), ROLE_ADMIN, ROLE_CONTEST_MANAGER, ROLE_TEAM_MANAGER, ROLE_TEAM_MEMBER);
    }
    
    @Test
    public void testRoleMappingMatching() {
        // *, *, * TODO all roles can be returned now, does this change semantics?
        //checkRoleMappingMatch(createRoleMappingKey(null, null, null), ROLE_USER);
        
        // 1, *, *
        checkRoleMappingMatch(createRoleMappingKey(1L, null, null), ROLE_ADMIN, ROLE_USER);
        checkRoleMappingMatch(createRoleMappingKey(1L, Contest.class, null), ROLE_ADMIN, ROLE_USER);
        checkRoleMappingMatch(createRoleMappingKey(1L, Contest.class, 8L), ROLE_ADMIN, ROLE_USER);
        checkRoleMappingMatch(createRoleMappingKey(1L, Institution.class, null), ROLE_ADMIN, ROLE_USER);
        checkRoleMappingMatch(createRoleMappingKey(1L, Institution.class, 8L), ROLE_ADMIN, ROLE_USER);
        
        // 2, Contest, *
        checkRoleMappingMatch(createRoleMappingKey(2L, Contest.class, null), ROLE_CONTEST_MANAGER, ROLE_USER);
        checkRoleMappingMatch(createRoleMappingKey(2L, Contest.class, null), ROLE_CONTEST_MANAGER, ROLE_USER);
        checkRoleMappingMatch(createRoleMappingKey(2L, Contest.class, 8L), ROLE_CONTEST_MANAGER, ROLE_USER);
        checkRoleMappingMatch(createRoleMappingKey(2L, Institution.class, null), ROLE_USER);
        checkRoleMappingMatch(createRoleMappingKey(2L, Institution.class, 8L), ROLE_USER);

        // 3, Contest, 4
        checkRoleMappingMatch(createRoleMappingKey(3L, Contest.class, null), ROLE_USER);
        checkRoleMappingMatch(createRoleMappingKey(3L, Contest.class, null), ROLE_USER);
        checkRoleMappingMatch(createRoleMappingKey(3L, Contest.class, 8L), ROLE_USER);
        checkRoleMappingMatch(createRoleMappingKey(3L, Institution.class, null), ROLE_USER);
        checkRoleMappingMatch(createRoleMappingKey(3L, Institution.class, 8L), ROLE_USER);
        checkRoleMappingMatch(createRoleMappingKey(3L, Contest.class, 4L), ROLE_CONTEST_MANAGER, ROLE_USER);
        checkRoleMappingMatch(createRoleMappingKey(4L, Contest.class, 4L), ROLE_USER);
    }


    private void checkAccessRuleMatch(AccessRuleKey accessRuleKey, String... expectedRolesArray) {
        Set<String> expectedRoles = new HashSet<String>(Arrays.asList(expectedRolesArray));
        Set<String> requiredRoles = accessService.getAccessRuleRoles(accessRuleKey);
        
        Assert.assertTrue(expectedRoles.containsAll(requiredRoles));
        Assert.assertTrue(requiredRoles.containsAll(expectedRoles));
    }

    @SuppressWarnings("serial")
    private AccessRuleKey createAccessRuleKey(final String category, final String action, final Class<?>... contextTypes) {
        return new AccessRuleKey() {
            {
                setCategory(category);
                setAction(action);
                for(Class<?> contextType : contextTypes) {
                    getContexts().add(contextType);
                }
            }
        };
    }
    
    private void checkRoleMappingMatch(RoleMappingKey roleMappingKey, String... expectedRolesArray) {
        Set<String> expectedRoles = new HashSet<String>(Arrays.asList(expectedRolesArray));
        Set<String> requiredRoles = accessService.getUserRoles(roleMappingKey);
        
        Assert.assertTrue(expectedRoles.containsAll(requiredRoles));
        Assert.assertTrue(requiredRoles.containsAll(expectedRoles));
    }

    private RoleMappingKey createRoleMappingKey(Long userId, Class<?> contextType, Long contextId) {
        RoleMappingKey roleMappingKey = new RoleMappingKey();
        roleMappingKey.setUserId(userId);
        roleMappingKey.setContextType(contextType);
        roleMappingKey.setContextId(contextId);

        return roleMappingKey;
    }

}
