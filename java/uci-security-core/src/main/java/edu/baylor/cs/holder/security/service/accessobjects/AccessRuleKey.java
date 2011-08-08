package edu.baylor.cs.holder.security.service.accessobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Defines the key for a set of roles any of which, if held by the user, allow
 * execution of a particular action in a particular category. Note that
 * instances of this class are not persistent as the intent is to store a small
 * Collection of these in memory for the most efficient rule lookups.
 * 
 * @author holder
 */
public class AccessRuleKey implements Serializable {

    /**
     * UID
     */
    private static final long serialVersionUID = 1230921430294722783L;

    // Name of the category this rule applies to
    private String category;

    // Name of the action that this rule applies to
    private String action;

    // Classes expected to be matched for determining this rule, order does
    // matter
    private List<Class<?>> contexts = new ArrayList<Class<?>>();

    public AccessRuleKey() {
    }

    public AccessRuleKey(String category, String action, List<Class<?>> contexts) {
        super();
        this.category = category;
        this.action = action;
        this.contexts = contexts;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<Class<?>> getContexts() {
        return contexts;
    }

    public void setContexts(List<Class<?>> contexts) {
        this.contexts = contexts;
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
    public boolean matches(AccessRuleKey otherKey) {
        // null is a wildcard

        // test category
        if (this.getCategory() == null
                || this.getCategory().equals(otherKey.getCategory())) {

            // test action
            if (this.getAction() == null
                    || this.getAction().equals(otherKey.getAction())) {

                // test contexts
                if (this.getContexts() == null
                        || this.getContexts().equals(otherKey.getContexts())) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean wildMatches(AccessRuleKey otherKey) {
    	// null is a wildcard
    	
    	// test category
    	if (this.getCategory() == null
    			|| this.getCategory().equals(otherKey.getCategory())) {
    		
    		// test action
    		if (this.getAction() == null
    				|| otherKey.getAction().matches(this.getAction())) {
    			
    			// test contexts
    			if (this.getContexts() == null
    					|| this.getContexts().equals(otherKey.getContexts())) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((getAction() == null) ? 0 : getAction().hashCode());
        result = prime * result
                + ((getContexts() == null) ? 0 : getContexts().hashCode());
        result = prime
                * result
                + ((getCategory() == null) ? 0 : getCategory()
                        .hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof AccessRuleKey))
            return false;
        final AccessRuleKey other = (AccessRuleKey) obj;
        if (getAction() == null) {
            if (other.getAction() != null)
                return false;
        } else if (!getAction().equals(other.getAction()))
            return false;
        if (getContexts() == null) {
            if (other.getContexts() != null)
                return false;
        } else if (!getContexts().equals(other.getContexts()))
            return false;
        if (getCategory() == null) {
            if (other.getCategory() != null)
                return false;
        } else if (!getCategory().equals(other.getCategory()))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        String id = category + ", " + action;
        for(Class<?> context : contexts) {
            id +=  ", " + context.getName();
        }
        
        return id;
    }
}
