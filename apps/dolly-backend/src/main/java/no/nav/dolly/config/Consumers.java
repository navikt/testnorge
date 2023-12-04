package no.nav.dolly.config;

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

    private ServerProperties testnavAaregProxy;
    private ServerProperties testnavAmeldingService;
    private ServerProperties testnavArbeidsplassenCVProxy;
    private ServerProperties testnavArenaForvalterenProxy;
    private ServerProperties testnavBrregStubProxy;
    private ServerProperties testnavDokarkivProxy;
    private ServerProperties testnavHistarkProxy;
    private ServerProperties testnavInntektsmeldingService;
    private ServerProperties testnavInntektstubProxy;
    private ServerProperties testnavInstProxy;
    private ServerProperties testnavKodeverkProxy;
    private ServerProperties testnavKontoregisterPersonProxy;
    private ServerProperties testnavKrrstubProxy;
    private ServerProperties testnavMedlProxy;
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
    private ServerProperties testnavSyntSykemeldingApi;
    private ServerProperties testnavTpsMessagingService;
    private ServerProperties testnavMiljoerService;
    private ServerProperties testnavUdistubProxy;

}
