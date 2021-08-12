package no.nav.dolly.web.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.web.config.credentials.NaisServerProperties;
import no.nav.dolly.web.config.credentials.TestnavArenaForvalterenProxyProperties;
import no.nav.dolly.web.config.credentials.TestnavBrregstubProxyProperties;
import no.nav.dolly.web.config.credentials.TestnavHodejegerenProxyProperties;
import no.nav.dolly.web.config.credentials.TestnavInntektstubProxyProperties;
import no.nav.dolly.web.config.credentials.TestnavJoarkDokumentServiceProperties;
import no.nav.dolly.web.config.credentials.TestnavKrrstubProxyProperties;
import no.nav.dolly.web.config.credentials.TestnavOrganisasjonFasteDataServiceProperties;
import no.nav.dolly.web.config.credentials.TestnavPensjonTestdataFacadeProxyProperties;
import no.nav.dolly.web.config.credentials.TestnavSigrunstubProxyProperties;
import no.nav.dolly.web.config.credentials.TestnavTestnorgeAaregProxyProperties;
import no.nav.dolly.web.config.credentials.TestnavTestnorgeInstProxyProperties;
import no.nav.dolly.web.config.credentials.TpsForvalterenProxyProperties;
import no.nav.dolly.web.config.filters.AddAuthorizationToRouteFilter;
import no.nav.dolly.web.security.TokenService;
import no.nav.dolly.web.security.domain.AccessScopes;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependenciesOn;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;

@Slf4j
@Configuration
@RequiredArgsConstructor
@DependenciesOn({
        @DependencyOn("dolly-backend"),
        @DependencyOn("testnorge-profil-api"),
        @DependencyOn("testnorge-varslinger-api"),
        @DependencyOn("testnav-organisasjon-forvalter"),
        @DependencyOn("testnav-organisasjon-service"),
        @DependencyOn("testnav-miljoer-service"),
        @DependencyOn("udi-stub-dev")
})
public class ApplicationConfig {
    private final RemoteApplicationsProperties properties;
    private final TokenService tokenService;
    private final TestnavOrganisasjonFasteDataServiceProperties testnavOrganisasjonFasteDataServiceProperties;
    private final TestnavJoarkDokumentServiceProperties testnavJoarkDokumentServiceProperties;
    private final TestnavInntektstubProxyProperties testnavInntektstubProxyProperties;
    private final TpsForvalterenProxyProperties tpsForvalterenProxyProperties;
    private final TestnavBrregstubProxyProperties testnavBrregstubProxyProperties;
    private final TestnavHodejegerenProxyProperties testnavHodejegerenProxyProperties;
    private final TestnavArenaForvalterenProxyProperties testnavArenaForvalterenProxyProperties;
    private final TestnavTestnorgeAaregProxyProperties testnavTestnorgeAaregProxyProperties;
    private final TestnavKrrstubProxyProperties testnavKrrstubProxyProperties;
    private final TestnavTestnorgeInstProxyProperties testnavTestnorgeInstProxyProperties;
    private final TestnavSigrunstubProxyProperties testnavSigrunstubProxyProperties;
    private final TestnavPensjonTestdataFacadeProxyProperties testnavPensjonTestdataFacadeProxyProperties;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public AddAuthorizationToRouteFilter testnavOrganisasjonFasteDataServiceAddAuthorizationToRouteFilter() {
        return createFilterFrom(testnavOrganisasjonFasteDataServiceProperties);
    }

    @Bean
    public AddAuthorizationToRouteFilter testnavJoarkDokumentServiceAddAuthorizationToRouteFilter() {
        return createFilterFrom(testnavJoarkDokumentServiceProperties);
    }

    @Bean
    public AddAuthorizationToRouteFilter testnavPensjonTestdataFacadeProxyAddAuthorizationToRouteFilter() {
        return createFilterFrom(testnavPensjonTestdataFacadeProxyProperties);
    }

    @Bean
    public AddAuthorizationToRouteFilter testnavInntektstubProxyAddAuthorizationToRouteFilter() {
        return createFilterFrom(testnavInntektstubProxyProperties);
    }
    @Bean
    public AddAuthorizationToRouteFilter testnavHodejegerenProxyAddAuthorizationToRouteFilter() {
        return createFilterFrom(testnavHodejegerenProxyProperties);
    }

    @Bean
    public AddAuthorizationToRouteFilter tpsForvalterenProxyAddAuthorizationToRouteFilter() {
        return createFilterFrom(tpsForvalterenProxyProperties, "tps-forvalteren-proxy");
    }

    @Bean
    public AddAuthorizationToRouteFilter testnavBrregstubProxyAddAuthorizationToRouteFilter() {
       return createFilterFrom(testnavBrregstubProxyProperties);
    }

    @Bean
    public AddAuthorizationToRouteFilter testnavArenaForvalterenProxyAddAuthorizationToRouteFilter() {
       return createFilterFrom(testnavArenaForvalterenProxyProperties);
    }

    @Bean
    public AddAuthorizationToRouteFilter testnavTestnorgeInstProxyPropertiesAddAuthorizationToRouteFilter() {
       return createFilterFrom(testnavTestnorgeInstProxyProperties);
    }

    @Bean
    public AddAuthorizationToRouteFilter testnavTestnorgeAaregProxyAddAuthorizationToRouteFilter() {
       return createFilterFrom(testnavTestnorgeAaregProxyProperties);
    }

    @Bean
    public AddAuthorizationToRouteFilter testnavKrrstubProxyAddAuthorizationToRouteFilter() {
       return createFilterFrom(testnavKrrstubProxyProperties);
    }
    @Bean
    public AddAuthorizationToRouteFilter testnavSigrunstubProxyAddAuthorizationToRouteFilter() {
       return createFilterFrom(testnavSigrunstubProxyProperties);
    }

    @Bean
    public AddAuthorizationToRouteFilter dollyBackendAddAuthorizationToRouteFilter() {
        return createFilterFrom("dolly-backend");
    }

    @Bean
    public AddAuthorizationToRouteFilter profilApiAddAuthorizationToRouteFilter() {
        return createFilterFrom("testnorge-profil-api");
    }

    @Bean
    public AddAuthorizationToRouteFilter varslingerApiAddAuthorizationToRouteFilter() {
        return createFilterFrom("testnorge-varslinger-api");
    }

    @Bean
    public AddAuthorizationToRouteFilter organisasjonForvalterAddAuthorizationToRouteFilter() {
        return createFilterFrom("organisasjon-forvalter");
    }

    @Bean
    public AddAuthorizationToRouteFilter organisasjonServiceAddAuthorizationToRouteFilter() {
        return createFilterFrom("organisasjon-service");
    }

    @Bean
    public AddAuthorizationToRouteFilter miljoerServiceAddAuthorizationToRouteFilter() {
        return createFilterFrom("testnav-miljoer-service");
    }

    @Bean
    public AddAuthorizationToRouteFilter adresseServiceAddAuthorizationToRouteFilter() {
        return createFilterFrom("testnav-adresse-service");
    }

    @Bean
    public AddAuthorizationToRouteFilter udiStubAddAuthorizationToRouteFilter() {
        return createFilterFrom("udi-stub");
    }

    @Bean
    public AddAuthorizationToRouteFilter personSearchAddAuthorizationToRouteFilter() {
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.getAccessToken(new AccessScopes("dev-fss.dolly.person-search-service")).getTokenValue(),
                "person-search-service"
        );
    }

    private AddAuthorizationToRouteFilter createFilterFrom(String route) {
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.getAccessToken(new AccessScopes(properties.get(route))).getTokenValue(),
                route
        );
    }


    private AddAuthorizationToRouteFilter createFilterFrom(NaisServerProperties serverProperties, String route) {
        log.info("Setter opp proxy for route {} for {}.", route, serverProperties.getName());
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.getAccessToken(new AccessScopes(serverProperties)).getTokenValue(),
                route
        );
    }

    private AddAuthorizationToRouteFilter createFilterFrom(NaisServerProperties serverProperties) {
        return createFilterFrom(serverProperties, serverProperties.getName());
    }

    @Bean
    public FilterRegistrationBean<SessionTimeoutCookieFilter> loggingFilter() {
        FilterRegistrationBean<SessionTimeoutCookieFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new SessionTimeoutCookieFilter());
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }
}
