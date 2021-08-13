package no.nav.dolly.web.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.web.config.credentials.DollyBackendProperties;
import no.nav.dolly.web.config.credentials.NaisServerProperties;
import no.nav.dolly.web.config.credentials.PersonSearchServiceProperties;
import no.nav.dolly.web.config.credentials.TestnavAdresseServiceProperties;
import no.nav.dolly.web.config.credentials.TestnavArenaForvalterenProxyProperties;
import no.nav.dolly.web.config.credentials.TestnavBrregstubProxyProperties;
import no.nav.dolly.web.config.credentials.TestnavHodejegerenProxyProperties;
import no.nav.dolly.web.config.credentials.TestnavInntektstubProxyProperties;
import no.nav.dolly.web.config.credentials.TestnavJoarkDokumentServiceProperties;
import no.nav.dolly.web.config.credentials.TestnavKrrstubProxyProperties;
import no.nav.dolly.web.config.credentials.TestnavMiljoerServiceProperties;
import no.nav.dolly.web.config.credentials.TestnavOrganisasjonFasteDataServiceProperties;
import no.nav.dolly.web.config.credentials.TestnavOrganisasjonForvalterProperties;
import no.nav.dolly.web.config.credentials.TestnavOrganisasjonServiceProperties;
import no.nav.dolly.web.config.credentials.TestnavPensjonTestdataFacadeProxyProperties;
import no.nav.dolly.web.config.credentials.TestnavSigrunstubProxyProperties;
import no.nav.dolly.web.config.credentials.TestnavTestnorgeAaregProxyProperties;
import no.nav.dolly.web.config.credentials.TestnavTestnorgeInstProxyProperties;
import no.nav.dolly.web.config.credentials.TestnavVarslingerApiProperties;
import no.nav.dolly.web.config.credentials.TestnorgeProfilApiProperties;
import no.nav.dolly.web.config.credentials.TpsForvalterenProxyProperties;
import no.nav.dolly.web.config.credentials.UdiStubProperties;
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
    private final DollyBackendProperties dollyBackendProperties;
    private final TestnorgeProfilApiProperties testnorgeProfilApiProperties;
    private final TestnavVarslingerApiProperties testnorgeVarslingerApiProperties;
    private final TestnavOrganisasjonForvalterProperties testnavOrganisasjonForvalterProperties;
    private final TestnavOrganisasjonServiceProperties testnavOrganisasjonServiceProperties;
    private final TestnavMiljoerServiceProperties testnavMiljoerServiceProperties;
    private final UdiStubProperties udiStubProperties;
    private final PersonSearchServiceProperties personSearchServiceProperties;
    private final TestnavAdresseServiceProperties testnavAdresseServiceProperties;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public AddAuthorizationToRouteFilter testnavOrganisasjonFasteDataServiceAddAuthorizationToRouteFilter() {
        return createFilterFrom(testnavOrganisasjonFasteDataServiceProperties);
    }

    @Bean
    public AddAuthorizationToRouteFilter testnavAdresseServiceAddAuthorizationToRouteFilter() {
        return createFilterFrom(testnavAdresseServiceProperties);
    }

    @Bean
    public AddAuthorizationToRouteFilter testnavOrganisasjonForvalterAddAuthorizationToRouteFilter() {
        return createFilterFrom(testnavOrganisasjonForvalterProperties);
    }

    @Bean
    public AddAuthorizationToRouteFilter testnorgeVarslingerApiAddAuthorizationToRouteFilter() {
        return createFilterFrom(testnorgeVarslingerApiProperties);
    }

    @Bean
    public AddAuthorizationToRouteFilter testnorgeProfilApiAddAuthorizationToRouteFilter() {
        return createFilterFrom(testnorgeProfilApiProperties);
    }

    @Bean
    public AddAuthorizationToRouteFilter testnavMiljoerServiceAddAuthorizationToRouteFilter() {
        return createFilterFrom(testnavMiljoerServiceProperties);
    }

    @Bean
    public AddAuthorizationToRouteFilter dollyBackendAddAuthorizationToRouteFilter() {
        return createFilterFrom(dollyBackendProperties, "dolly-backend");
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
    public AddAuthorizationToRouteFilter testnavOrganisasjonServiceAddAuthorizationToRouteFilter() {
        return createFilterFrom(testnavOrganisasjonServiceProperties);
    }

    @Bean
    public AddAuthorizationToRouteFilter testnavSigrunstubProxyAddAuthorizationToRouteFilter() {
        return createFilterFrom(testnavSigrunstubProxyProperties);
    }

    @Bean
    public AddAuthorizationToRouteFilter udiStubAddAuthorizationToRouteFilter() {
        return createFilterFrom(udiStubProperties, "udi-stub");
    }

    @Bean
    public AddAuthorizationToRouteFilter personSearchServiceAddAuthorizationToRouteFilter() {
        return createFilterFrom(personSearchServiceProperties);
    }


    private AddAuthorizationToRouteFilter createFilterFrom(NaisServerProperties serverProperties, String route) {
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
