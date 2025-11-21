package no.nav.dolly.proxy.route;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PACKAGE;

@Configuration
@ConfigurationProperties(prefix = "app.targets")
@NoArgsConstructor(access = PACKAGE)
@Getter
@Setter
class Targets {

    String aaregServices;
    String aaregVedlikehold;
    String arenaForvalteren;
    String arenaOrds;
    String batch;
    String brregstub;
    String dokarkiv;
    String ereg;
    String fullmakt;
    String histark;
    String inntektstub;
    String inst;
    String kontoregister;
    String krrstub;
    String medl;
    String norg2;
    String pensjon;
    String pensjonAfp;
    String pensjonSamboer;
    String saf;
    String sigrunstub;
    String skjermingsregister;
    String udistub;

}
