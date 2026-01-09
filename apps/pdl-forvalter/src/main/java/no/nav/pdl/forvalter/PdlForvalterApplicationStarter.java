package no.nav.pdl.forvalter;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class PdlForvalterApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PdlForvalterApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}

// QWERT FIKSE (FOR SØK I INTELLIJ, SKAL FJERNES): // TODO: SLETT MEG
// TODO: PDL-FORVALTER FORNAVN/ETTERNAVN UNDER BESTILLING - ###TROR DENNE ER GOOD NÅ###
// TODO: ORGANISASJON-SERVICE/FORVALTER - ved arbeidsforhold bestilling kommer feilen "org ikke funnet" i frontend etter valg av org
// TODO: DOLLY-BACKEND/JOARK-SERVICE? - FUNGERER IKKE Å SENDE INN DOKUMENTER TIL JOARK LENGER, ingen feilmelding i logg
// TODO: INNTEKTSMELDING-GENERATOR/SERVICE - INGEN ERROR I LOGG, MEN INGEN DATA SER UT TIL Å BLI SENDT VED BESTILLING
// TODO: GcpSecretManager - Må vente på kompabilitet med spring boot 4
