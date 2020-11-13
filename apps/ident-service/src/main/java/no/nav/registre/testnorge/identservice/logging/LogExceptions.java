package no.nav.registre.testnorge.identservice.logging;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that indicates that exceptions thrown by method should be logged.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LogExceptions {

    /**
     * Classes for exceptions that should NOT be logged. Examples are exceptions that gets thrown when validating.
     */
    Class<?>[] ignored() default {};

    /**
     * Classes for exceptions that should be logged into a functional log. This will exclude the stacktrace.
     */
    Class<?>[] functional() default {};
}