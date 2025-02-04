package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AlderspensjonSoknadRequest extends AlderspensjonRequest {

    public enum SivilstandType {ENKE, GIFT, GJPA, NULL, REPA, SEPA, SEPR, SKIL, SKPA, UGIF}

    public enum RelasjonType {EKTEF, PARTNER, SAMBO}

    private String statsborgerskap;
    private SivilstandType sivilstand;
    private LocalDate sivilstandDatoFom;
    private Boolean inkluderAfpPrivat;
    private PensjonData.Alderspensjon.AfpPrivatResultat afpPrivatResultat;

    private List<SkjemaRelasjon> relasjonListe;

    public List<SkjemaRelasjon> getRelasjonListe() {
        if (isNull(relasjonListe)) {
            relasjonListe = new ArrayList<>();
        }
        return relasjonListe;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkjemaRelasjon {

        private LocalDate relasjonFraDato;
        private LocalDate dodsdato;
        private Boolean varigAdskilt;
        private String fnr;
        private LocalDate samlivsbruddDato;
        private Boolean harVaertGift;
        private Boolean harFellesBarn;
        private Integer sumAvForventetArbeidKapitalPensjonInntekt;
        private RelasjonType relasjonType;
    }
}