package no.nav.dolly.proxy.config;

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

    private ServerProperties aaregServicesQ1;
    private ServerProperties aaregServicesQ2;
    private ServerProperties aaregServicesQ4;
    private ServerProperties aaregVedlikeholdQ1;
    private ServerProperties aaregVedlikeholdQ2;
    private ServerProperties aaregVedlikeholdQ4;
    private ServerProperties arenaForvalteren;
    private ServerProperties arenaOrdsQ1;
    private ServerProperties arenaOrdsQ2;
    private ServerProperties arenaOrdsQ4;
    private ServerProperties brregstub;
    private ServerProperties dokarkivQ1;
    private ServerProperties dokarkivQ2;
    private ServerProperties dokarkivQ4;
    private ServerProperties eregQ1;
    private ServerProperties eregQ2;
    private ServerProperties eregQ4;
    private ServerProperties fullmakt;
    private ServerProperties henvendelse;
    private ServerProperties histark;
    private ServerProperties inntektstub;
    private ServerProperties inst;
    private ServerProperties kelvinAap;
    private ServerProperties kontoregister;
    private ServerProperties krrstub;
    private ServerProperties medl;
    private ServerProperties norg2;
    private ServerProperties oppfoelging;
    private ServerProperties oppfoelgingsvedtak14a;
    private ServerProperties pdlApiQ1;
    private ServerProperties pdlApiQ2;
    private ServerProperties pdlIdenthendelse;
    private ServerProperties pdlTestdata;
    private ServerProperties pensjon;
    private ServerProperties pensjonAfpQ1;
    private ServerProperties pensjonAfpQ2;
    private ServerProperties pensjonSamboerQ1;
    private ServerProperties pensjonSamboerQ2;
    private ServerProperties safQ1;
    private ServerProperties safQ2;
    private ServerProperties safQ4;
    private ServerProperties sigrunstub;
    private ServerProperties skattekortQ1;
    private ServerProperties skattekortQ2;
    private ServerProperties skjermingsregister;
    private ServerProperties sykemelding;
    private ServerProperties udistub;

}
