package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PdlForeldreansvar extends PdlOpplysning {

    public enum Ansvar {FELLES, MOR, FAR, MEDMOR, ANDRE, UKJENT}

    private Ansvar ansvar;
    private String ansvarlig;
    private RelatertBiPerson ansvarligUtenIdentifikator;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatertBiPerson {

        private LocalDate foedselsdato;
        private Kjoenn kjoenn;
        private Personnavn navn;
        private String statsborgerskap;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Personnavn {

        private String etternavn;
        private String fornavn;
        private String mellomnavn;
    }
}
