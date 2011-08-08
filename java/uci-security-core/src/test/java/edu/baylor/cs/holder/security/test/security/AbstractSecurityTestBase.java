package edu.baylor.cs.holder.security.test.security;

import java.util.List;

import org.junit.Before;

import edu.baylor.cs.holder.security.service.AccessService;
import edu.baylor.cs.holder.security.service.SecurityRepository;
import edu.baylor.cs.holder.security.service.accessobjects.AccessRule;
import edu.baylor.cs.holder.security.service.accessobjects.RoleMapping;

public abstract class AbstractSecurityTestBase {
    
    protected SecurityRepository securityRepository;
    protected AccessService accessService;
    
    @Before
    public void setup() throws Exception {
        securityRepository = getSecurityRepository();
        accessService = getAccessService();
    }
    
    protected RoleMapping createRoleMapping(Long userId, Class<?> contextType, Long contextId) {
        RoleMapping roleMapping = new RoleMapping();
        roleMapping.setUserId(userId);
        roleMapping.setContextType(contextType);
        roleMapping.setContextId(contextId);
        
        return roleMapping;
    }
    
    protected AccessRule createAccessRule(String category, String action, Boolean active, List<Class<?>> contexts, List<String> roles) {
        AccessRule accessRule = new AccessRule();
        accessRule.setCategory(category);
        accessRule.setAction(action);
        accessRule.setActive(active);
        
        for(Class<?> context : contexts) {
            accessRule.addContext(context);
        }
        
        for(String role : roles) {
            accessRule.addRole(role);
        }
        
        return accessRule;
    }
    
    protected abstract SecurityRepository getSecurityRepository() throws Exception;
    protected abstract AccessService getAccessService() throws Exception;
}
