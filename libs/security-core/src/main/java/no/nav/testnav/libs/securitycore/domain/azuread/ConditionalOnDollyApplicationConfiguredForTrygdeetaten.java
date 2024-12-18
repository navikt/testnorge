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
 * Conditional that matches if the application is configured for Trygdeetaten.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(OnDollyApplicationConfiguredForTrygdeetatenCondition.class)
public @interface ConditionalOnDollyApplicationConfiguredForTrygdeetaten {
}

class OnDollyApplicationConfiguredForTrygdeetatenCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(
            ConditionContext context,
            AnnotatedTypeMetadata metadata
    ) {
        var issuerUri = context
                .getEnvironment()
                .getProperty("AZURE_TRYGDEETATEN_OPENID_CONFIG_TOKEN_ENDPOINT");
        // Check for AZURE_TRYGDEETATEN_APP_CLIENT_ID/AZURE_TRYGDEETATEN_APP_CLIENT_SECRET?
        var match = StringUtils.hasText(issuerUri);
        var message = match ? "Dolly application configured for Trygdeetaten." : "Dolly application not configured for Trygdeetaten. Missing required property 'AZURE_TRYGDEETATEN_OPENID_CONFIG_TOKEN_ENDPOINT'";
        return new ConditionOutcome(match, message);
    }
}
