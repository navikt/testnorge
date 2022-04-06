package no.nav.pdl.forvalter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PdlForeldreansvar extends DbVersjonDTO {

    private Ansvar ansvar;
    private String ansvarlig;
    private PersonRequestDTO nyAnsvarlig;
    private RelatertBiPersonDTO ansvarligUtenIdentifikator;

    public enum Ansvar {FELLES, MOR, FAR, MEDMOR, ANDRE, UKJENT}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(Include.NON_NULL)
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
    @JsonInclude(Include.NON_NULL)
    public static class PersonnavnDTO implements Serializable {

        private String etternavn;
        private String fornavn;
        private String mellomnavn;
    }
}