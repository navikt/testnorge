package no.nav.testnav.libs.securitycore.domain.azuread;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Conditional that matches if the application is configured for Nav.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(OnDollyApplicationConfiguredForNavCondition.class)
public @interface ConditionalOnDollyApplicationConfiguredForNav {
}

class OnDollyApplicationConfiguredForNavCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(
            ConditionContext context,
            AnnotatedTypeMetadata metadata
    ) {
        var issuerUri = context
                .getEnvironment()
                .getProperty("AZURE_NAV_OPENID_CONFIG_TOKEN_ENDPOINT");
        // Check for AZURE_NAV_APP_CLIENT_ID/AZURE_NAV_APP_CLIENT_SECRET?
        var match = StringUtils.hasText(issuerUri);
        var message = match ? "Dolly application configured for Nav." : "Dolly application not configured for Nav. Missing required property 'AZURE_NAV_OPENID_CONFIG_TOKEN_ENDPOINT'";
        return new ConditionOutcome(match, message);
    }
}
