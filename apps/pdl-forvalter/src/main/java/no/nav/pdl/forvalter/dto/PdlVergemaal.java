package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;

import java.io.Serializable;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@AllArgsConstructor
public class PdlVergemaal extends DbVersjonDTO {

    private String embete;
    private VergemaalType type;
    private VergeEllerFullmektig vergeEllerFullmektig;

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
