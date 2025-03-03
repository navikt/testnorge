package no.nav.dolly.libs.test;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.lang.annotation.*;

/**
 * <p>Convenience annotation for Dolly tests normally annotated with {@code @SpringBootTest}.</p>
 * <p>Specifies a custom {@link NaisEnvironmentApplicationContextInitializer} that sets common dummy values.</p>
 * <p>Note behaviour of {@code initializers} attribute, where you need to explicitly also include the default {@link NaisEnvironmentApplicationContextInitializer} if you add other initializers.</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(initializers = NaisEnvironmentApplicationContextInitializer.class)
public @interface DollySpringBootTest {

    /**
     * {@inheritDoc}
     * <p>Defaults to {@link SpringBootTest.WebEnvironment#RANDOM_PORT}.</p>
     */
    @AliasFor(annotation = SpringBootTest.class, attribute = "webEnvironment")
    SpringBootTest.WebEnvironment webEnvironment() default SpringBootTest.WebEnvironment.RANDOM_PORT;

    /**
     * {@inheritDoc}
     * <p>Defaults to an empty array.</p>
     */
    @AliasFor(annotation = SpringBootTest.class, attribute = "classes")
    Class<?>[] classes() default {};

    /**
     * {@inheritDoc}
     * <p>Defaults to an empty array.</p>
     */
    @AliasFor(annotation = SpringBootTest.class, attribute = "properties")
    String[] properties() default {};

    /**
     * {@inheritDoc}
     * <p>If specifying other initializers, be sure to also include the default initializer {@link NaisEnvironmentApplicationContextInitializer}.</p>
     */
    @AliasFor(annotation = ContextConfiguration.class, attribute = "initializers")
    @SuppressWarnings("java:S1452")
    Class<? extends ApplicationContextInitializer<?>>[] initializers() default { NaisEnvironmentApplicationContextInitializer.class };
}
