package no.nav.registre.bisys.consumer.rs.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BidragsmeldingAugments {

    @Boforhold
    private final String andelForsorging;
    private final boolean barnRegistrertPaaAdresse;
    private final String beslaarsakKode;
    private final String gebyrBeslAarsakKode;
    private final int inntektBmEgneOpplysninger;
    private final int inntektBpEgneOpplysninger;
    private final String kodeUnntForsk;
    private final String samvarsklasse;
    private final int sartilskuddFradrag;
    private final int sartilskuddGodkjentBelop;
    private final int sartilskuddKravbelop;
    private final String sivilstandBm;
    private final int skatteklasse;

}
