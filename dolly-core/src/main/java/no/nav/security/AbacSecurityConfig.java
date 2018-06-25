package no.nav.security;

import no.nav.abac.xacml.NavAttributter;
import no.nav.freg.abac.core.annotation.attribute.AbacAttributeLocator;
import no.nav.freg.abac.core.annotation.attribute.ResolvingAbacAttributeLocator;
import no.nav.freg.abac.spring.config.AbacConfig;
import no.nav.freg.abac.spring.config.AbacRestTemplateConfig;
import no.nav.freg.security.oidc.common.OidcTokenAuthentication;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@Import({
        AbacConfig.class,
        AbacRestTemplateConfig.class
})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AbacSecurityConfig {

    @Bean
    Set<String> abacDefaultEnvironment() {
        Set<String> values = new HashSet<>();
        values.add(NavAttributter.ENVIRONMENT_FELLES_PEP_ID);
        values.add(NavAttributter.ENVIRONMENT_FELLES_OIDC_TOKEN_BODY);
        return values;
    }

    @Bean
    Set<String> abacDefaultResources() {
        Set<String> values = new HashSet<>();
        values.add(NavAttributter.RESOURCE_FELLES_DOMENE);
        return values;
    }

    @Bean
    AbacAttributeLocator pepIdLocator(@Value("${application.name}") String credential) {
        return new ResolvingAbacAttributeLocator(NavAttributter.ENVIRONMENT_FELLES_PEP_ID, () -> credential);
    }

    @Bean
    AbacAttributeLocator fellesDomeneLocator() {
        return new ResolvingAbacAttributeLocator(NavAttributter.RESOURCE_FELLES_DOMENE, () -> "registre");
    }

    @Bean
    AbacAttributeLocator oidcAttributeLocator() {
        return new ResolvingAbacAttributeLocator(NavAttributter.ENVIRONMENT_FELLES_OIDC_TOKEN_BODY,
                () -> (((OidcTokenAuthentication) SecurityContextHolder.getContext().getAuthentication()).getIdTokenBody()));
    }
}
