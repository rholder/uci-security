package edu.baylor.cs.holder.security.service.accessobjects;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines a set of roles a person has with respect to a particular entity.
 * 
 * @author holder
 */
public class RoleMapping implements Serializable {
    
    /** Generated serial version UID */
    private static final long serialVersionUID = -2004083195208687939L;
    
    // key for this RoleMapping, defaults to *, *, *
    private RoleMappingKey key = new RoleMappingKey();
    
    // roles added for the User, contextType pairing
    private Set<String> roles = new HashSet<String>();
    
    public void setContextType(Class<?> contextType) {
        getKey().setContextType(contextType);
    }
    
    public void setUserId(Long userId) {
        getKey().setUserId(userId);
    }
    
    public void setContextId(Long contextId) {
        getKey().setContextId(contextId);
    }
    
    public RoleMappingKey getKey() {
        return key;
    }
    
    public void setKey(RoleMappingKey key) {
        this.key = key;
    }
    
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    /**
     * Adds the given role to the list of roles associated with the user in the
     * given context. This method is used when deserializing rules from XML.
     * 
     * @param role
     *            the role
     */
    public void addRole(String role) {
        this.roles.add(role);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof RoleMapping))
            return false;
        final RoleMapping other = (RoleMapping) obj;
        if (getKey() == null) {
            if (other.getKey() != null)
                return false;
        } else if (!getKey().equals(other.getKey()))
            return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
}
