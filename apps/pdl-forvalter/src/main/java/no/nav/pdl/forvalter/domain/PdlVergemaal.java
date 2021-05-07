package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlVergemaal extends PdlDbVersjon {

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
    private Folkeregistermetadata folkeregistermetadata;
    private VergemaalType type;
    private VergeEllerFullmektig vergeEllerFullmektig;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VergeEllerFullmektig implements Serializable {

        private String motpartsPersonident;
        private Personnavn navn;
        private Omfang omfang;
        private Boolean omfangetErInnenPersonligOmraade;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Personnavn implements Serializable {

        private String etternavn;
        private String fornavn;
        private String mellomnavn;
    }
}
