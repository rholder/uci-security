package edu.baylor.cs.holder.security.service.accessobjects;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Defines a set of roles any of which, if held by the user, allow execution of
 * a particular action in a particular category. Note that instances of this
 * class are not persistent as the intent is to store a small Collection of
 * these in memory for the most efficient rule lookups.
 * 
 * @author holder
 */
public class AccessRule implements Serializable {

    /**
     * UID
     */
    private static final long serialVersionUID = 1230921430294722783L;

    // Key for this AccessRule, defaults to null, null, empty list
    private AccessRuleKey key = new AccessRuleKey();

    // True when the rule should be used
    private Boolean active = true;

    // Roles a user must have before invoking the action
    private Set<String> roles = new HashSet<String>();

    public void setCategory(String category) {
        this.key.setCategory(category);
    }

    public void setAction(String action) {
        this.key.setAction(action);
    }

    public void setContexts(List<Class<?>> contexts) {
        this.key.setContexts(contexts);
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public AccessRuleKey getKey() {
        return key;
    }

    public void setKey(AccessRuleKey key) {
        this.key = key;
    }

    /**
     * Adds the given role to the list of roles with permission to execute the
     * associated action. This method is used when deserializing rules from XML.
     * 
     * @param role
     *            role to add
     */
    public void addRole(String role) {
        this.roles.add(role);
    }

    /**
     * Adds the given context to the list of contexts such that this rule will
     * now expect to have the given context. This method is used when
     * deserializing rules from XML.
     * 
     * @param context
     *            Class name to add to the contexts list
     */
    public void addContext(Class<?> context) {
        this.key.getContexts().add(context);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof AccessRule))
            return false;
        final AccessRule other = (AccessRule) obj;
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
    
    @Override
    public String toString() {
        return this.key.toString() + ": " + this.roles.toString();
    }
}
