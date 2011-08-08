package edu.baylor.cs.holder.security.service;

public interface DomainService {
    
    /**
     * Retrieves the instance of the class which has the specified id.
     * 
     * @param <T>
     *            the type of Object returned
     * @param clazz
     *            class of the instance required
     * @param id
     *            id of the required instance
     * @return the instance if it exists or null otherwise
     */
    public <T> T readObjectById(Class<T> clazz, Long id);
    
    /**
     * Return the id of the given object.
     * 
     * @param object
     * @return
     */
    public Long getId(Object object);
    
}
