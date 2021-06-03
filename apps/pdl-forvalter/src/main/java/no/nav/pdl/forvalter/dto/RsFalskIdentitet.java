package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.pdl.forvalter.domain.PdlDbVersjon;
import no.nav.pdl.forvalter.domain.PdlKjoenn;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsFalskIdentitet extends PdlDbVersjon {

    @Schema(description = "Informasjon om rett identitet for folkeregisterperson som er opphørt som fiktiv, eller falsk. " +
            "Rett identitet er gitt ved en av følgende: rettIdentitetVedOpplysninger, rettIdentitetErUkjent eller " +
            "rettIdentitetVedIdentifikasjonsnummer. Maksimalt en av disse skal være satt. Hvis ingen angitt eller " +
            "nyFalskIdentitet er satt vil ny person oppreettes og bli satt til rettIdentitetVedIdentifikasjonsnummer.")

    private Boolean erFalsk;
    private LocalDateTime gyldigFom;
    private LocalDateTime gyldigTom;

    private RsPersonRequest nyFalskIdentitetPerson;

    private Boolean rettIdentitetErUkjent;
    private String rettIdentitetVedIdentifikasjonsnummer;
    private IdentifiserendeInformasjon rettIdentitetVedOpplysninger;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IdentifiserendeInformasjon implements Serializable {

        private LocalDateTime foedselsdato;
        private PdlKjoenn.Kjoenn kjoenn;
        private RsNavn personnavn;
        private List<String> statsborgerskap;
    }
}
