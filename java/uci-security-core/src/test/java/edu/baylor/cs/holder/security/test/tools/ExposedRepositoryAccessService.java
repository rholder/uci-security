package edu.baylor.cs.holder.security.test.tools;

import java.util.Map;
import java.util.Set;

import edu.baylor.cs.holder.security.service.accessobjects.AccessRuleKey;
import edu.baylor.cs.holder.security.service.accessobjects.RoleMappingKey;
import edu.baylor.cs.holder.security.service.impl.RepositoryAccessServiceImpl;

public class ExposedRepositoryAccessService extends RepositoryAccessServiceImpl {

    public Map<AccessRuleKey, Set<String>> getAccessRuleMap() {
        return accessRuleMap;
    }
    
    public Map<RoleMappingKey, Set<String>> getRoleMap() {
        return roleMap;
    }
    
    public Set<String> getAllAvailableRoles() {
        return allAvailableRoles;
    }
}
