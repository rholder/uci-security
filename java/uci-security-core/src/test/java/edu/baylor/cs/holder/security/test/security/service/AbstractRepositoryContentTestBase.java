package edu.baylor.cs.holder.security.test.security.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import edu.baylor.cs.holder.security.service.accessobjects.AccessRule;
import edu.baylor.cs.holder.security.service.accessobjects.RoleMapping;
import edu.baylor.cs.holder.security.test.model.Contest;
import edu.baylor.cs.holder.security.test.model.Institution;
import edu.baylor.cs.holder.security.test.security.AbstractSecurityTestBase;

public abstract class AbstractRepositoryContentTestBase extends AbstractSecurityTestBase {

    @Test
    public void testRoleParsing() {
        
        Set<String> allAvailableRoles = securityRepository.getAllRoles();
        Assert.assertTrue(allAvailableRoles.contains("ROLE_ADMIN"));
        Assert.assertTrue(allAvailableRoles.contains("ROLE_CONTEST_MANAGER"));
        Assert.assertTrue(allAvailableRoles.contains("ROLE_TEAM_MANAGER"));
        Assert.assertTrue(allAvailableRoles.contains("ROLE_TEAM_MEMBER"));
        Assert.assertTrue(allAvailableRoles.contains("ROLE_USER"));
        Assert.assertEquals(5, allAvailableRoles.size());
    }
    
    @Test
    public void testAccessRuleParsingCorrectWithRoles() {
        
        List<Class<?>> contexts = new ArrayList<Class<?>>();
        contexts.add(Institution.class);
        
        List<String> roles = new ArrayList<String>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_CONTEST_MANAGER");
        
        AccessRule rule = createAccessRule("institutionManager", "select", true, contexts, roles );
        
        Set<AccessRule> allRules = securityRepository.getAccessRules();
        Assert.assertTrue(allRules.contains(rule));
    }
    
    @Test
    public void testAccessRuleParsingCorrectNoRoles() {
        
        List<Class<?>> contexts = new ArrayList<Class<?>>();
        contexts.add(Institution.class);
        
        // roles aren't matches, only category, action, contexts
        List<String> roles = Collections.emptyList();
        
        AccessRule rule = createAccessRule("institutionManager", "select", true, contexts, roles );
        
        Set<AccessRule> allRules = securityRepository.getAccessRules();
        Assert.assertTrue(allRules.contains(rule));
    }
    
    @Test
    public void testAccessRuleParsingCorrectWithBrokenRoles() {
        
        List<Class<?>> contexts = new ArrayList<Class<?>>();
        contexts.add(Institution.class);
        
        List<String> roles = new ArrayList<String>();
        roles.add("ROLE_ADMIN" + "BROKEN");
        roles.add("ROLE_CONTEST_MANAGER" + "BROKEN");
        
        AccessRule rule = createAccessRule("institutionManager", "select", true, contexts, roles );
        
        Set<AccessRule> allRules = securityRepository.getAccessRules();
        Assert.assertTrue(allRules.contains(rule));
    }
    
    @Test
    public void testAccessRuleParsingBrokenContext() {
        
        List<Class<?>> contexts = new ArrayList<Class<?>>();
        contexts.add(Contest.class);
        
        // roles aren't matches, only category, action, contexts
        List<String> roles = Collections.emptyList();
        
        AccessRule rule = createAccessRule("institutionManager", "select", true, contexts, roles );
        
        Set<AccessRule> allRules = securityRepository.getAccessRules();
        Assert.assertFalse(allRules.contains(rule));
    }
    
    @Test
    public void testAccessRuleParsingNoContext1() {
        
        List<Class<?>> contexts = new ArrayList<Class<?>>();
        
        // roles aren't matches, only category, action, contexts
        List<String> roles = Collections.emptyList();
        
        AccessRule rule = createAccessRule("institutionManager", "select", true, contexts, roles );
        
        Set<AccessRule> allRules = securityRepository.getAccessRules();
        Assert.assertTrue(allRules.contains(rule)); // should match other rule with no context defined
    }
    
    @Test
    public void testAccessRuleParsingNoContext2() {
        
        List<Class<?>> contexts = new ArrayList<Class<?>>();
        
        // roles aren't matches, only category, action, contexts
        List<String> roles = Collections.emptyList();
        
        AccessRule rule = createAccessRule("institutionManager", "save", true, contexts, roles );
        
        Set<AccessRule> allRules = securityRepository.getAccessRules();
        Assert.assertTrue(allRules.contains(rule)); // should match other rule with no context defined
    }
    
    @Test
    public void testAccessRuleParsingBrokenCategory() {
        
        List<Class<?>> contexts = new ArrayList<Class<?>>();
        contexts.add(Institution.class);
        
        // roles aren't matches, only category, action, contexts
        List<String> roles = Collections.emptyList();
        
        AccessRule rule = createAccessRule("institutionManager-BROKEN", "select", true, contexts, roles );
        
        Set<AccessRule> allRules = securityRepository.getAccessRules();
        Assert.assertFalse(allRules.contains(rule));
    }
    
    @Test
    public void testAccessRuleParsingBrokenAction() {
        
        List<Class<?>> contexts = new ArrayList<Class<?>>();
        contexts.add(Institution.class);
        
        // roles aren't matches, only category, action, contexts
        List<String> roles = Collections.emptyList();
        
        AccessRule rule = createAccessRule("institutionManager", "select-BROKEN", true, contexts, roles );
        
        Set<AccessRule> allRules = securityRepository.getAccessRules();
        Assert.assertFalse(allRules.contains(rule));
    }
    
    @Test
    public void testAccessRuleParsingBrokenActive() {
        
        List<Class<?>> contexts = new ArrayList<Class<?>>();
        contexts.add(Institution.class);
        
        // roles aren't matches, only category, action, contexts
        List<String> roles = Collections.emptyList();
        
        // active isn't used to match
        AccessRule rule = createAccessRule("institutionManager", "select", false, contexts, roles );
        
        Set<AccessRule> allRules = securityRepository.getAccessRules();
        Assert.assertTrue(allRules.contains(rule));
    }
    
    @Test
    public void testRoleMappingParsing() {
        
        Set<RoleMapping> allRoleMappings = securityRepository.getRoleMappings();
        
        // *, *, *
        Assert.assertTrue(allRoleMappings.contains(createRoleMapping(null, null, null)));
        
        // 1, *, *
        Assert.assertTrue(allRoleMappings.contains(createRoleMapping(1L, null, null)));
        
        // 2, Contest, *
        Assert.assertTrue(allRoleMappings.contains(createRoleMapping(2L, Contest.class, null)));

        // 3, Contest, 4
        Assert.assertTrue(allRoleMappings.contains(createRoleMapping(3L, Contest.class, 4L)));
    }
}
