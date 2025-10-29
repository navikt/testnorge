package no.nav.dolly.web.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PACKAGE;

/**
 * Samler alle placeholders for ulike {@code consumers.*}-konfigurasjon her, dvs. subklasser av {@code ServerProperties}.
 * <br/><br/>
 * Husk at Spring Boot bruker <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.typesafe-configuration-properties.relaxed-binding">relaxed binding</a>
 * mellom configuration properties og field names.
 *
 * @see ServerProperties
 */
@Configuration
@ConfigurationProperties(prefix = "consumers")
@NoArgsConstructor(access = PACKAGE)
@Getter
@Setter(PACKAGE)
public class Consumers {

    private ServerProperties dollyBackend;
    private ServerProperties genererNavnService;
    private ServerProperties oppsummeringsdokumentService;
    private ServerProperties testnavAaregProxy;
    private ServerProperties testnavAdresseService;
    private ServerProperties testnavArbeidsplassenCVProxy;
    private ServerProperties testnavArenaForvalterenProxy;
    private ServerProperties testnavBrregstubProxy;
    private ServerProperties testnavBrukerService;
    private ServerProperties testnavDokarkivProxy;
    private ServerProperties testnavHelsepersonellService;
    private ServerProperties testnavDollyProxy;
    private ServerProperties testnavInstProxy;
    private ServerProperties testnavJoarkDokumentService;
    private ServerProperties testnavKodeverkService;
    private ServerProperties testnavKontoregisterPersonProxy;
    private ServerProperties testnavKrrstubProxy;
    private ServerProperties testnavFullmaktProxy;
    private ServerProperties testnavMedlProxy;
    private ServerProperties testnavMiljoerService;
    private ServerProperties testnavNorg2Proxy;
    private ServerProperties testnavOrganisasjonFasteDataService;
    private ServerProperties testnavOrganisasjonForvalter;
    private ServerProperties testnavOrganisasjonService;
    private ServerProperties testnavPdlForvalter;
    private ServerProperties testnavPensjonTestdataFacadeProxy;
    private ServerProperties testnavDollySearchService;
    private ServerProperties testnavPersonService;
    private ServerProperties testnavSigrunstubProxy;
    private ServerProperties testnavSkjermingsregisterProxy;
    private ServerProperties testnavTpsMessagingService;
    private ServerProperties testnavUdistubProxy;
    private ServerProperties testnavVarslingerService;
    private ServerProperties testnorgeProfilApi;
    private ServerProperties testnorgeTilbakemeldingApi;
    private ServerProperties testnavTenorSearchService;
    private ServerProperties testnavSykemeldingApi;
    private ServerProperties testnavSkattekortService;
    private ServerProperties testnavLevendeArbeidsforholdAnsettelse;
    private ServerProperties testnavLevendeArbeidsforholdScheduler;
    private ServerProperties testnavYrkesskadeProxy;
    private ServerProperties testnavSykemeldingProxy;
    private ServerProperties testnavNomProxy;
    private ServerProperties testnavAltinn3TilgangService;
    private ServerProperties testnavArbeidssoekerregisteretProxy;
    private ServerProperties testnavApiOversiktService;
}
