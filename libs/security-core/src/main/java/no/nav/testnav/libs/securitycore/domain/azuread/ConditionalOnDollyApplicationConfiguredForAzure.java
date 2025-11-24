package no.nav.testnav.libs.securitycore.domain.azuread;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

/**
 * Conditional that matches if the application is configured for Nav.
 * Requires the following properties set:
 * <ul>
 *     <li>AZURE_OPENID_CONFIG_TOKEN_ENDPOINT</li>
 *     <li>AZURE_APP_CLIENT_ID</li>
 *     <li>AZURE_APP_CLIENT_SECRET</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(OnDollyApplicationConfiguredForAzureCondition.class)
public @interface ConditionalOnDollyApplicationConfiguredForAzure {
}

class OnDollyApplicationConfiguredForAzureCondition extends SpringBootCondition {

    private static final List<String> REQUIRED = Arrays.asList(
            "AZURE_OPENID_CONFIG_TOKEN_ENDPOINT",
            "AZURE_APP_CLIENT_ID",
            "AZURE_APP_CLIENT_SECRET"
    );

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata
    ) {
        var env = context.getEnvironment();
        var match = REQUIRED
                .stream()
                .allMatch(key -> hasText(env.getProperty(key)));
        return new ConditionOutcome(
                match,
                match ? "Dolly configured for Azure" : "Dolly not configured for Azure - missing one or more required properties %s".formatted(REQUIRED)
        );
    }

}
