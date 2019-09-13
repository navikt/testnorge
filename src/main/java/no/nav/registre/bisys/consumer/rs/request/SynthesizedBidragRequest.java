package no.nav.registre.bisys.consumer.rs.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.bidrag.ui.bisys.kodeverk.KodeSoknGrKomConstants;
import no.nav.bidrag.ui.bisys.kodeverk.KodeVirkAarsConstants;
import no.nav.bidrag.ui.bisys.soknad.request.SoknadRequest;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SynthesizedBidragRequest {

    @BidragsmeldingConstant
    private String andelForsorging;

    @BidragsmeldingConstant
    private boolean barnRegistrertPaaAdresse;

    @BidragsmeldingConstant
    private String samvarsklasse;

    @BidragsmeldingConstant
    private String gebyrBeslAarsakKode;

    @BidragsmeldingConstant
    private int inntektBmEgneOpplysninger;

    @BidragsmeldingConstant
    private int inntektBpEgneOpplysninger;

    @BidragsmeldingConstant
    private String kodeUnntForsk;

    @BidragsmeldingConstant
    @Getter(AccessLevel.NONE)
    private String kodeVirkAarsak;

    @BidragsmeldingConstant
    private int sartilskuddGodkjentBelop;

    @BidragsmeldingConstant
    private int sartilskuddFradrag;

    @BidragsmeldingConstant
    private int sartilskuddKravbelop;

    @BidragsmeldingConstant
    private String sivilstandBm;

    @BidragsmeldingConstant
    private int skatteklasse;

    private SoknadRequest soknadRequest;

    public String getKodeVirkAarsak(String kodeSoknGrKom) {
        if (KodeSoknGrKomConstants.FORSKUDD.equals(kodeSoknGrKom)) {
            return KodeVirkAarsConstants.ANNET_SF;
        } else {
            return KodeVirkAarsConstants.ANNET_SB;
        }
    }

}
