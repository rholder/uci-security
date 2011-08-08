package edu.baylor.cs.holder.security.service.impl;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import edu.baylor.cs.holder.security.service.SecurityRepository;
import edu.baylor.cs.holder.security.service.accessobjects.AccessRule;
import edu.baylor.cs.holder.security.service.accessobjects.RoleMapping;
import edu.baylor.cs.holder.security.util.ReflectionUtils;

@SuppressWarnings("unchecked")
public class Dom4jSecurityRepository implements SecurityRepository {

    private static final String CONTEXT_PACKAGES = "contextPackages";
    private static final String PACKAGE = "package";

    private static final String CATEGORIES = "categories";
    private static final String CATEGORY = "category";
    private static final String NAME = "name";
    private static final String ACCESS_RULE = "accessRule";
    private static final String ACTION = "action";
    private static final String CONTEXT = "context";
    private static final String ACTIVE = "active";
    private static final String ROLES = "roles";
    private static final String ROLE = "role";

    private static final String ROLE_MAPPINGS = "roleMappings";
    private static final String ROLE_MAPPING = "roleMapping";
    private static final String USER_ID = "userId";
    private static final String CONTEXT_TYPE = "contextType";
    private static final String CONTEXT_ID = "contextId";
    
    private Set<String> roles = new HashSet<String>();
    private Set<String> packages = new TreeSet<String>(); // search order matters here

    private Set<AccessRule> accessRules = new HashSet<AccessRule>();
    private Set<RoleMapping> roleMappings = new HashSet<RoleMapping>();

    /**
     * Populate this {@link SecurityRepository} with XML input from the given
     * stream.
     * 
     * @param xmlInput
     * @throws IllegalArgumentException
     */
    public void populate(InputStream xmlInput) throws IllegalArgumentException {
        
        try {
            Document doc = parseDocument(xmlInput);
            Element root = doc.getRootElement();
            
            processRoles(root);
            processPackages(root);
            processCategories(root);
            processRoleMappings(root);
            
        } catch (DocumentException e) {
            throw new IllegalArgumentException("An error has occurred while parsing InputStream", e);
        }
    }

    /**
     * Parse an {@link InputStream} and populate the security objects.
     * 
     * @param xmlIn
     * @return
     * @throws DocumentException
     */
    private Document parseDocument(InputStream xmlIn) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(xmlIn);
        return document;
    }

    /**
     * Process the set of available roles from the root {@link Element}.
     * 
     * @param root
     */
    private void processRoles(Element root) {

        // iterate through child elements of root with element name ROLES
        for (Iterator<Element> allRolesElements = root.elementIterator(ROLES); allRolesElements.hasNext();) {
            Element rolesElement = allRolesElements.next();

            // iterate through child elements of roles with element name ROLE
            for (Iterator<Element> allRoleElements = rolesElement.elementIterator(ROLE); allRoleElements.hasNext();) {
                Element roleElement = allRoleElements.next();
                roles.add(roleElement.getText());
            }
        }
    }

    /**
     * Process the set of available context search packages from the root
     * {@link Element}.
     * 
     * @param root
     */
    private void processPackages(Element root) {

        // iterate through child elements of root with element name CONTEXT_PACKAGES
        for (Iterator<Element> allContextPackageElements = root.elementIterator(CONTEXT_PACKAGES); allContextPackageElements.hasNext();) {
            Element contextPackageElement = allContextPackageElements.next();
            
            // iterate through child elements of CONTEXT_PACKAGES with element name PACKAGE
            for (Iterator<Element> allPackageElements = contextPackageElement.elementIterator(PACKAGE); allPackageElements.hasNext();) {
                Element packageElement = allPackageElements.next();
                packages.add(packageElement.getText());
            }
        }
    }

    /**
     * Process category elements from the root {@link Element}.
     * 
     * @param root
     * @throws DocumentException
     */
    private void processCategories(Element root) throws DocumentException {
        
        // iterate through child elements of root with element name ROLES
        for (Iterator<Element> allCategoriesElements = root.elementIterator(CATEGORIES); allCategoriesElements.hasNext();) {
            Element categoriesElement = allCategoriesElements.next();

            // iterate through child elements of roles with element name ROLE
            for (Iterator<Element> allCategoryElements = categoriesElement.elementIterator(CATEGORY); allCategoryElements.hasNext();) {
                Element categoryElement = allCategoryElements.next();
                processAccessRules(categoryElement);
            }
        }
    }
    
    /**
     * Process {@link AccessRule}'s from a category {@link Element}.
     * 
     * @param categoryElement
     * @throws DocumentException
     */
    private void processAccessRules(Element categoryElement) throws DocumentException {
        
        String category = categoryElement.attributeValue(NAME);
        
        // iterate through child elements of root with element name ACCESS_RULE
        for (Iterator<Element> allAccessRuleElements = categoryElement.elementIterator(ACCESS_RULE); allAccessRuleElements.hasNext();) {
            Element accessRuleElement = allAccessRuleElements.next();
            
            // use element attributes to create the new AccessRule
            
            String action = accessRuleElement.attributeValue(ACTION);
            Boolean active = Boolean.valueOf(accessRuleElement.attributeValue(ACTIVE));
            
            AccessRule accessRule = new AccessRule();
            accessRule.setCategory(category);
            accessRule.setAction(action);
            accessRule.setActive(active);
            
            processAccessRuleRoles(accessRuleElement, accessRule);
            processAccessRuleContexts(accessRuleElement, accessRule);
            
            accessRules.add(accessRule);
        }
    }

    /**
     * Process {@link AccessRule} contexts from the access rule {@link Element}.
     * 
     * @param accessRuleElement
     * @param accessRule
     * @throws DocumentException
     */
    private void processAccessRuleContexts(Element accessRuleElement, AccessRule accessRule) throws DocumentException {

        // iterate through child elements of ACCESS_RULE with element name CONTEXT
        for (Iterator<Element> allContextElements = accessRuleElement.elementIterator(CONTEXT); allContextElements.hasNext();) {
            Element contextElement = allContextElements.next();
            String contextType = contextElement.getText();
            Class<?> contextClass = ReflectionUtils.getContextClass(packages, contextType);
            
            if(contextClass == null) {
                throw new DocumentException("No class found for context: " + contextType
                        + " while searching through available contextPackages");
            } else {
                accessRule.addContext(contextClass);
            }
        }
    }

    /**
     * Process {@link AccessRule} roles from the access rule {@link Element}.
     * 
     * @param accessRuleElement
     * @param accessRule
     * @throws DocumentException
     */
    private void processAccessRuleRoles(Element accessRuleElement, AccessRule accessRule) throws DocumentException {
        
        // iterate through child elements of accessRuleElement with element name ROLE
        for (Iterator<Element> allRoleElements = accessRuleElement.elementIterator(ROLE); allRoleElements.hasNext();) {
            Element roleElement = allRoleElements.next();

            // validate against all roles specified in the root by
            // <roles></roles>
            String role = roleElement.getText();
            if (!roles.contains(role)) {
                throw new DocumentException("An invalid role has been detected: " + role);
            } else {
                accessRule.addRole(role);
            }
        }
    }
    
    /**
     * Process {@link RoleMapping}'s from the root {@link Element}.
     * 
     * @param root
     * @throws DocumentException 
     */
    private void processRoleMappings(Element root) throws DocumentException {

        // iterate through child elements of root with element name ROLE_MAPPINGS
        for (Iterator<Element> allRoleMappingsElements = root.elementIterator(ROLE_MAPPINGS); allRoleMappingsElements.hasNext();) {
            
            Element roleMappingsElement = allRoleMappingsElements.next();
            
            // iterate through child elements of ROLE_MAPPINGS with element name ROLE_MAPPING
            for (Iterator<Element> allRoleMappingElements = roleMappingsElement.elementIterator(ROLE_MAPPING); allRoleMappingElements.hasNext();) {
                Element roleMappingElement = allRoleMappingElements.next();
                
                // use element attributes to create the new RoleMapping
                String userId = roleMappingElement.attributeValue(USER_ID);
                String contextType = roleMappingElement.attributeValue(CONTEXT_TYPE);
                String contextId = roleMappingElement.attributeValue(CONTEXT_ID);
                
                RoleMapping roleMapping = new RoleMapping();
                roleMapping.setUserId(userId == null ? null : Long.valueOf(userId));
                roleMapping.setContextType(contextType == null ? null : ReflectionUtils.getContextClass(packages, contextType));
                roleMapping.setContextId(contextId == null ? null : Long.valueOf(contextId));
                
                processRoleMappingRoles(roleMappingElement, roleMapping);
                
                roleMappings.add(roleMapping);
            }
        }
    }
    
    /**
     * Process {@link RoleMapping} roles from the role mapping {@link Element}.
     * 
     * @param roleMappingElement
     * @param roleMapping
     * @throws DocumentException
     */
    private void processRoleMappingRoles(Element roleMappingElement, RoleMapping roleMapping) throws DocumentException {
        
        // iterate through child elements of roleMappingElement with element name ROLE
        for (Iterator<Element> allRoleElements = roleMappingElement.elementIterator(ROLE); allRoleElements.hasNext();) {
            Element roleElement = allRoleElements.next();

            // validate against all roles specified in the root by
            // <roles></roles>
            String role = roleElement.getText();
            if (!roles.contains(role)) {
                throw new DocumentException("An invalid role has been detected: " + role);
            } else {
                roleMapping.addRole(role);
            }
        }
    }

    public Set<AccessRule> getAccessRules() {
        return accessRules;
    }

    public Set<RoleMapping> getRoleMappings() {
        return roleMappings;
    }

    public Set<String> getAllRoles() {
        return roles;
    }
    
    public Set<String> getContextPackages() {
        return packages;
    }
}
