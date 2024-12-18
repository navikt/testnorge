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
 * Conditional that matches if the application is configured for Azure.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Conditional(OnDollyApplicationConfiguredForAzureCondition.class)
public @interface ConditionalOnDollyApplicationConfiguredForAzure {
}

class OnDollyApplicationConfiguredForAzureCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(
            ConditionContext context,
            AnnotatedTypeMetadata metadata
    ) {
        var issuerUri = context
                .getEnvironment()
                .getProperty("AAD_ISSUER_URI");
        // Check for AZURE_APP_CLIENT_ID/AZURE_APP_CLIENT_SECRET?
        var match = StringUtils.hasText(issuerUri);
        var message = match ? "Dolly application configured for Azure." : "Dolly application not configured for Azure. Missing required property 'AAD_ISSUER_URI'";
        return new ConditionOutcome(match, message);
    }
}
