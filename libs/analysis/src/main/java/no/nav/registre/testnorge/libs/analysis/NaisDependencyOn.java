package no.nav.registre.testnorge.libs.analysis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NaisDependencyOn {
    String name();
    String cluster() default "unknown";
    String namespace() default "unknown";

    boolean external() default false;

    DependencyType type() default DependencyType.REST;
}