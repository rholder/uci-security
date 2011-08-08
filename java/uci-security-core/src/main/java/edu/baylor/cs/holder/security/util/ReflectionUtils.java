package edu.baylor.cs.holder.security.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * This class contains some generic reflection helper methods.
 * 
 * @author holder
 */
public class ReflectionUtils {

    /**
     * Returns the annotation specified by the annotationClass if it exists on
     * the given Method. The search order for the annotation is first to look at
     * the implementing class and then to check the interfaces of the given
     * Method's declaring class. If it exists in more than one interface, then
     * the annotation is checked for only in the first interface. According to
     * Class.getInterfaces(), this will be evaluated in the order of the
     * interface names in the implements clause of the declaration of the class.
     * 
     * Returns null if no annotation is found.
     * 
     * @param <T>
     *            returned annotation
     * @param m
     *            Method to check for an annotation
     * @param annotationClass
     *            Annotation class to check for
     * @return
     */
    public static <T extends Annotation> T getInheritedAnnotation(Method m,
            Class<T> annotationClass) {
        // First check the current class
        T annotation = m.getAnnotation(annotationClass);
        if (annotation == null) {
            annotation = getInterfaceAnnotation(m, annotationClass);
        }
        return annotation;
    }

    /**
     * Return the given annotation if it exists on any interface of the
     * declaring class for the given Method. If it exists in more than one
     * interface, then the annotation is checked for only in the first
     * interface. According to Class.getInterfaces(), this will be evaluated in
     * the order of the interface names in the implements clause of the
     * declaration of the class.
     * 
     * Returns null if no annotation is found.
     * 
     * @param <T>
     *            returned annotation
     * @param m
     *            Method to check for an annotation
     * @param annotationClass
     *            Annotation class to check for
     * @return
     */
    public static <T extends Annotation> T getInterfaceAnnotation(Method m,
            Class<T> annotationClass) {

        T returnValue = null;

        for (Class<?> c : m.getDeclaringClass().getInterfaces()) {
            try {
                Method iMethod = c
                        .getMethod(m.getName(), m.getParameterTypes());
                returnValue = iMethod.getAnnotation(annotationClass);
                break; // only care about the first one we find
            } catch (NoSuchMethodException e) {
                // ignore
            }
        }

        return returnValue;
    }
    
    /**
     * Return a {@link Class} based on the given String, searching the given
     * {@link Set} of packages. Return null if no {@link Class} is found.
     * 
     * @param contextType
     * @return
     */
    public static Class<?> getContextClass(Set<String> searchPackages, String className) {
        
        // evaluate context class using current contextPackages
        Class<?> contextClass = null;
        for (String contextPackage : searchPackages) {
            try {
                // break on the first class we find
                contextClass = Class.forName(contextPackage + "." + className);
                break;
            } catch (ClassNotFoundException e) {
                ; // ignore
            }
        }
        
        return contextClass;
    }
}
