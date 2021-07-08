package no.nav.registre.testnorge.libs.dto.pdlforvalter.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForeldreansvarDTO extends DbVersjonDTO {

    private Ansvar ansvar;
    private String ansvarlig;
    private RelatertBiPersonDTO ansvarligUtenIdentifikator;
    public enum Ansvar {FELLES, MOR, FAR, MEDMOR, ANDRE, UKJENT}

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatertBiPersonDTO implements Serializable {

        private LocalDateTime foedselsdato;
        private KjoennDTO.Kjoenn kjoenn;
        private PersonnavnDTO navn;
        private String statsborgerskap;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonnavnDTO implements Serializable {

        private String etternavn;
        private String fornavn;
        private String mellomnavn;
    }
}
