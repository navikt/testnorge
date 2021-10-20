package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FalskIdentitetDTO extends DbVersjonDTO {

    @Schema(description = "Informasjon om rett identitet for folkeregisterperson som er opphørt som fiktiv, eller falsk. " +
            "Rett identitet er gitt ved en av følgende: rettIdentitetVedOpplysninger, rettIdentitetErUkjent eller " +
            "rettIdentitetVedIdentifikasjonsnummer. Maksimalt en av disse skal være satt. Hvis ingen angitt eller " +
            "nyFalskIdentitet er satt vil ny person oppreettes og bli satt til rettIdentitetVedIdentifikasjonsnummer.")

    private Boolean erFalsk;
    private LocalDateTime gyldigFraOgMed;
    private LocalDateTime gyldigTilOgMed;

    private PersonRequestDTO nyFalskIdentitetPerson;

    private Boolean rettIdentitetErUkjent;
    private String rettIdentitetVedIdentifikasjonsnummer;
    private IdentifiserendeInformasjonDTO rettIdentitetVedOpplysninger;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class IdentifiserendeInformasjonDTO implements Serializable {

        private LocalDateTime foedselsdato;
        private KjoennDTO.Kjoenn kjoenn;
        private FalsktNavnDTO personnavn;
        private List<String> statsborgerskap;

        public List<String> getStatsborgerskap() {
            if (isNull(statsborgerskap)) {
                statsborgerskap = new ArrayList<>();
            }
            return statsborgerskap;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FalsktNavnDTO implements Serializable {

        private String etternavn;
        private String fornavn;
        private String mellomnavn;
        private Boolean hasMellomnavn;
    }
}
