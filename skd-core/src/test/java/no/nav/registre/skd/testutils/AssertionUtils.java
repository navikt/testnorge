package no.nav.registre.skd.testutils;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AssertionUtils {

    public static <T> void assertAllFieldsNotNull(T objekt, List<String> ignoreMethod) throws InvocationTargetException, IllegalAccessException {
        var methods = Arrays.asList(objekt.getClass().getDeclaredMethods());

        var getters = methods.stream().filter(method -> "get".equals(method.getName().substring(0, 3))).collect(Collectors.toList());
        for (var method : getters) {
            if (!ignoreMethod.contains(method.getName())) {
                assertNotNull(method.getName() + " returned null", method.invoke(objekt));
            }
        }
        var methods_is = methods.stream().filter(method -> "is".equals(method.getName().substring(0, 2))).collect(Collectors.toList());
        for (var method : methods_is) {
            assertNotNull(method.getName() + " returned null", method.invoke(objekt));
        }

    }
}
