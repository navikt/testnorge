package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class ForeldreansvarDTO extends DbVersjonDTO {

    private Ansvar ansvar;
    private String ansvarlig;
    private PersonRequestDTO nyAnsvarlig;
    private RelatertBiPersonDTO ansvarligUtenIdentifikator;
    private LocalDateTime gyldigFraOgMed;
    private LocalDateTime gyldigTilOgMed;
    public enum Ansvar {FELLES, MOR, FAR, MEDMOR, ANDRE, UKJENT}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_EMPTY)
    public static class RelatertBiPersonDTO implements Serializable {

        private LocalDateTime foedselsdato;
        private KjoennDTO.Kjoenn kjoenn;
        private PersonnavnDTO navn;
        private String statsborgerskap;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_EMPTY)
    public static class PersonnavnDTO implements Serializable {

        private String etternavn;
        private String fornavn;
        private String mellomnavn;
    }
}