package no.nav.registre.hodejegeren.testutils;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AssertionUtils {
    
    public static <T> void assertAllFieldsNotNull(T objekt) throws InvocationTargetException, IllegalAccessException {
        List<Method> methods = Arrays.asList(objekt.getClass().getDeclaredMethods());
        
        List<Method> getters = methods.stream().filter(method -> "get".equals(method.getName().substring(0, 3))).collect(Collectors.toList());
        for (Method method : getters) {
            assertNotNull(method.getName() + " returned null", method.invoke(objekt));
        }
        List<Method> methods_is = methods.stream().filter(method -> "is".equals(method.getName().substring(0, 2))).collect(Collectors.toList());
        for (Method method : methods_is) {
            assertNotNull(method.getName() + " returned null", method.invoke(objekt));
        }
        
    }
    
    public static <T> void assertAllFieldsNotNull(T objekt, List<String> ignoreMethod) throws InvocationTargetException, IllegalAccessException {
        List<Method> methods = Arrays.asList(objekt.getClass().getDeclaredMethods());
        
        List<Method> getters = methods.stream().filter(method -> "get".equals(method.getName().substring(0, 3))).collect(Collectors.toList());
        for (Method method : getters) {
            if (!ignoreMethod.contains(method.getName())) {
                assertNotNull(method.getName() + " returned null", method.invoke(objekt));
            }
        }
        List<Method> methods_is = methods.stream().filter(method -> "is".equals(method.getName().substring(0, 2))).collect(Collectors.toList());
        for (Method method : methods_is) {
            assertNotNull(method.getName() + " returned null", method.invoke(objekt));
        }
        
    }
    
    public static <T> void assertAllListsNotEmpty(T objekt, List ignoreFields) throws InvocationTargetException, IllegalAccessException {
        List<Method> methods = Arrays.asList(objekt.getClass().getDeclaredMethods());
        if (ignoreFields != null) {
            methods.removeAll(ignoreFields);
        }
        
        List<Method> getters = methods.stream().filter(method -> "get".equals(method.getName().substring(0, 3))).collect(Collectors.toList());
        List<Method> collectionOfListFields = getters.stream().filter(method -> method.getReturnType().equals(List.class)).collect(Collectors.toList());
        for (Method method : collectionOfListFields) {
            assertFalse(method.getName() + " was empty", ((List) method.invoke(objekt)).isEmpty());
        }
    }
    
}
