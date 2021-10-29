package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PdlVergemaal extends PdlOpplysning {

    public enum VergemaalType {
        ENSLIG_MINDREAARIG_ASYLSOEKER,
        ENSLIG_MINDREAARIG_FLYKTNING,
        VOKSEN,
        MIDLERTIDIG_FOR_VOKSEN,
        MINDREAARIG,
        MIDLERTIDIG_FOR_MINDREAARIG,
        FORVALTNING_UTENFOR_VERGEMAAL,
        STADFESTET_FREMTIDSFULLMAKT
    }

    public enum Omfang {
        UTLENDINGSSAKER_PERSONLIGE_OG_OEKONOMISKE_INTERESSER,
        PERSONLIGE_OG_OEKONOMISKE_INTERESSER,
        OEKONOMISKE_INTERESSER,
        PERSONLIGE_INTERESSER
    }

    private String embete;
    private VergemaalType type;
    private VergeEllerFullmektig vergeEllerFullmektig;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VergeEllerFullmektig {

        private String motpartsPersonident;
        private Personnavn navn;
        private Omfang omfang;
        private Boolean omfangetErInnenPersonligOmraade;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Personnavn {

        private String etternavn;
        private String fornavn;
        private String mellomnavn;
    }
}
