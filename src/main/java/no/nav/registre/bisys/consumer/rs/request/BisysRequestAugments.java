package no.nav.registre.bisys.consumer.rs.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BisysRequestAugments {

    private final boolean barnRegistrertPaaAdresse;
    private final int inntektBmEgneOpplysninger;
    private final int inntektBpEgneOpplysninger;
    private final String andelForsorging;
    private final String gebyrBeslAarsakKode;
    private final String kodeUnntForsk;
    private final String samvarsklasse;
    private final String sivilstandBm;

}
