package no.nav.dolly.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PACKAGE;

@Configuration
@ConfigurationProperties(prefix = "consumers")
@NoArgsConstructor(access = PACKAGE)
@Getter
@Setter(PACKAGE)
public class Consumers {

    private ServerProperties testnavAaregProxy;
    private ServerProperties testnavArbeidsplassenCVProxy;
    private ServerProperties testnavArenaForvalterenProxy;
    private ServerProperties testnavBrregStubProxy;
    private ServerProperties testnavDokarkivProxy;
    private ServerProperties testnavHistarkProxy;
    private ServerProperties testnavInntektsmeldingService;
    private ServerProperties testnavInntektstubProxy;
    private ServerProperties testnavInstProxy;
    private ServerProperties testnavKodeverkService;
    private ServerProperties testnavKontoregisterPersonProxy;
    private ServerProperties testnavFullmaktProxy;
    private ServerProperties testnavKrrstubProxy;
    private ServerProperties testnavMedlProxy;
    private ServerProperties testnavMiljoerService;
    private ServerProperties testnavNorg2Proxy;
    private ServerProperties testnavOrganisasjonForvalter;
    private ServerProperties testnavOrganisasjonService;
    private ServerProperties testnavPdlForvalter;
    private ServerProperties testnavPdlProxy;
    private ServerProperties testnavPensjonTestdataFacadeProxy;
    private ServerProperties testnavPersonService;
    private ServerProperties testnavSigrunstubProxy;
    private ServerProperties testnavSkjermingsregisterProxy;
    private ServerProperties testnavSykemeldingApi;
    private ServerProperties testnavSykemeldingProxy;
    private ServerProperties testnavTpsMessagingService;
    private ServerProperties testnavUdistubProxy;
    private ServerProperties testnavSkattekortService;
    private ServerProperties yrkesskadeProxy;
    private ServerProperties arbeidssoekerregisteretProxy;
    private ServerProperties brukerService;
    private ServerProperties safProxy;
    private ServerProperties etterlatte;
    private ServerProperties nomProxy;
}
