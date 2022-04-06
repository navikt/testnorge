package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PdlFalskIdentitet extends DbVersjonDTO {

    private Boolean erFalsk;
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
    @Builder
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
