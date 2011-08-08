package edu.baylor.cs.holder.security.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.baylor.cs.holder.security.service.AccessService;
import edu.baylor.cs.holder.security.service.SecurityRepository;
import edu.baylor.cs.holder.security.service.accessobjects.AccessRule;
import edu.baylor.cs.holder.security.service.accessobjects.AccessRuleKey;
import edu.baylor.cs.holder.security.service.accessobjects.RoleMapping;
import edu.baylor.cs.holder.security.service.accessobjects.RoleMappingKey;

/**
 * This class populates the access rules and explicit role mappings from a
 * {@link SecurityRepository}.
 * 
 * Role lookups handled by this class include those that match the following
 * RoleMappingKey forms:<br>
 * <br>
 * *, *, * <br>
 * UserId, *, * <br>
 * UserId, ContextType, * <br>
 * UserId, ContextType, ContextId<br>
 * 
 * This class also returns those AccessRule roles stored for the given
 * AccessRoleKey.
 * 
 * @author holder
 */
public class RepositoryAccessServiceImpl implements AccessService {

    /**
     * UID
     */
    private static final long serialVersionUID = 1190769758693170119L;
    
    private Log log = LogFactory.getLog(getClass());

    /**
     * This map is filled in and keyed with all of the access rules defined in
     * the system. Its purpose is to allow quick lookup of an access rule given
     * the category and the action. We can build an access rule key and then
     * fill in the necessary elements to look up the real access rule that holds
     * the roles.
     */
    protected Map<AccessRuleKey, Set<String>> accessRuleMap = new HashMap<AccessRuleKey, Set<String>>();

    /**
     * This map is filled in and keyed with all of the explicit role mappings.
     * Its purpose is to allow quick lookup of a role mapping given a UserId and
     * Context. We can build a role mapping key and then fill in the necessary
     * elements to look up the real role mapping that holds the roles.
     */
    protected Map<RoleMappingKey, Set<String>> roleMap = new HashMap<RoleMappingKey, Set<String>>();
    
    /**
     * This set contains all of the available roles.
     */
    protected Set<String> allAvailableRoles = Collections.emptySet();
    
    /**
     * This set contains all of the context packages.
     */
    protected Set<String> allContextPackages = Collections.emptySet();

    /**
     * Used to only allow a single initialization call to the update() method,
     * such as at startup to bootstrap this class.
     */
    private AtomicBoolean updated = new AtomicBoolean(false);

    /**
     * Use the given {@link SecurityRepository} to update this service with new
     * access rules, role mappings, and available roles, replacing all existing
     * content.
     * 
     * NOTE: Subsequent calls to this method will not result in further updates.
     * Future versions will include better thread safety to support dynamic
     * updates, etc.
     */
    public void update(SecurityRepository securityRepository) {
       
        // only allow one update call, for thread safety right now
        boolean alreadyUpdated = updated.getAndSet(true);
        if(!alreadyUpdated) {
            
            // clear previous data load up the repository data
            accessRuleMap.clear();
            for(AccessRule rule : securityRepository.getAccessRules()) {
                addAccessRule(rule);
            }
            
            roleMap.clear();
            for(RoleMapping roleMapping : securityRepository.getRoleMappings()) {
                addRoleMapping(roleMapping);
            }
            
            allAvailableRoles = Collections.unmodifiableSet(securityRepository.getAllRoles());
            allContextPackages = Collections.unmodifiableSet(securityRepository.getContextPackages());
        }
    }

    public Set<String> getAccessRuleRoles(AccessRuleKey key) {
        Set<String> roles = accessRuleMap.get(key);
        if (roles == null) {
            if(log.isErrorEnabled()) {
                log.error("No rule found for: " + key.getCategory() + "."
                        + key.getAction() + " " + key.getContexts());
            }
            roles = Collections.emptySet();
        }
        return roles;
    }

    public Set<String> getUserRoles(RoleMappingKey key) {

        // Roles to return
        Set<String> roles = new HashSet<String>();

        // Using naive implementation, this won't scale well if the XML
        // role mappings grow too large but it's cached anyway so...
        for (RoleMappingKey existingKey : roleMap.keySet()) {

            // Check if the existing key matches the current key
            if (existingKey.matches(key)) {
                Set<String> newRoles = roleMap.get(existingKey);
                roles.addAll(newRoles);
            }
        }

        return roles;
    }
    
    public Set<String> getVetoRoles(AccessRuleKey accessRuleKey, RoleMappingKey roleMappingKey) {
        return Collections.emptySet();
    }
    
    public Set<String> getAllAvailableRoles() {
        return allAvailableRoles;
    }
    
    public Set<String> getContextPackages() {
        return allContextPackages;
    }

    /**
     * Add an access rule to this repository checking for duplicate rules and
     * throwing an {@link IllegalArgumentException} if they occur.
     * 
     * @param rule
     *            rule to be added
     * @throws IllegalArgumentException
     *             thrown when a duplicate rule is added to the repository
     */
    private void addAccessRule(AccessRule rule) throws IllegalArgumentException {
        
        if (accessRuleMap.containsKey(rule.getKey())) {
            // we have a duplicate rule
            throw new IllegalArgumentException("Duplicate access rule detected: "
                    + rule.getKey().getCategory() + "."
                    + rule.getKey().getAction());
        }

        // treat inactive rules as non-existent rules
        if (rule.getActive()) {
            accessRuleMap.put(rule.getKey(), rule.getRoles());
        }
    }

    /**
     * Add a role mapping to the repository checking for duplicate role mappings
     * and throwing an {@link IllegalArgumentException} if they occur.
     * 
     * @param mapping
     *            mapping to be added
     * @throws IllegalArgumentException
     *             thrown when a duplicate rule is added to the repository
     */
    private void addRoleMapping(RoleMapping mapping) {

        // add the rule to the explicit role map
        if (roleMap.containsKey(mapping.getKey())) {
            // we have a duplicate mapping
            throw new IllegalArgumentException("Duplicate mapping detected: "
                    + mapping.getKey().getUserId() + ":"
                    + mapping.getKey().getContextType() + ":"
                    + mapping.getKey().getContextId());
        }
        
        roleMap.put(mapping.getKey(), mapping.getRoles());
    }
}