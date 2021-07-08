package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.NavnDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PdlFalskIdentitet extends DbVersjonDTO {

    private Boolean erFalsk;
    private Folkeregistermetadata folkeregistermetadata;
    private RettIdentitet rettIdentitet;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RettIdentitet implements Serializable {

        private Boolean rettIdentitetErUkjent;
        private String rettIdentitetVedIdentifikasjonsnummer;
        private IdentifiserendeInformasjon rettIdentitetVedOpplysninger;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IdentifiserendeInformasjon implements Serializable {

        private LocalDateTime foedselsdato;
        private KjoennDTO.Kjoenn kjoenn;
        private NavnDTO personnavn;
        private List<String> statsborgerskap;
    }
}
