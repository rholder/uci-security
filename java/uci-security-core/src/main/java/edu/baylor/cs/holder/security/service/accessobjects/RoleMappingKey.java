package edu.baylor.cs.holder.security.service.accessobjects;

import java.io.Serializable;

/**
 * Defines a key for the set of roles a user has with respect to a particular
 * contextType and contextId.
 * 
 * @author holder
 */
public class RoleMappingKey implements Serializable {

    /**
     * UID
     */
    private static final long serialVersionUID = -2004083195208687939L;

    // id of the User that will receive the roles
    private Long userId;

    // type of context that the user will have the roles added for
    private Class<?> contextType;

    // id of the context
    private Long contextId;

    public RoleMappingKey() {
    }

    public RoleMappingKey(Long userId, Class<?> contextType, Long contextId) {
        super();
        this.userId = userId;
        this.contextType = contextType;
        this.contextId = contextId;
    }

    public Class<?> getContextType() {
        return contextType;
    }

    public void setContextType(Class<?> contextType) {
        this.contextType = contextType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getContextId() {
        return contextId;
    }

    public void setContextId(Long contextId) {
        this.contextId = contextId;
    }

    /**
     * Matching of two keys is non-symmetric and for the sake of clarity, the
     * most general key is "this" key. null values are treated as wildcards in
     * "this" key. The "otherKey" matches this key when either "this" key
     * contains a wildcard for the property or it exactly equals the property of
     * the "otherKey." When all properties "match" then we return true.
     * 
     * @param otherKey
     *            other key to match
     * @return true if this key "matches" the other given key
     */
    public boolean matches(RoleMappingKey otherKey) {
        // null is a wildcard

        // test user id
        if (this.getUserId() == null
                || this.getUserId().equals(otherKey.getUserId())) {

            // test context type
            if (this.getContextType() == null
                    || this.getContextType().equals(otherKey.getContextType())) {

                // test context id
                if (this.getContextId() == null
                        || this.getContextId().equals(otherKey.getContextId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        // check memory address
        if (this == obj)
            return true;

        // check null
        if (obj == null)
            return false;

        if (!(obj instanceof RoleMappingKey))
            return false;

        // check user id
        final RoleMappingKey other = (RoleMappingKey) obj;
        if (getUserId() == null) {
            if (other.getUserId() != null)
                return false;
        } else if (!getUserId().equals(other.getUserId()))
            return false;

        // check context id
        if (getContextId() == null) {
            if (other.getContextId() != null)
                return false;
        } else if (!getContextId().equals(other.getContextId()))
            return false;

        // check context type
        if (getContextType() == null) {
            if (other.getContextType() != null)
                return false;
        } else if (!getContextType().equals(other.getContextType()))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result
                + ((getContextId() == null) ? 0 : getContextId().hashCode());
        result = prime * result
                + ((getContextType() == null) ? 0 : getContextType().hashCode());
        return result;
    }
}
