package no.nav.dolly.bestilling.pdlforvalter.domain;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdlForeldreansvar {

    public enum Ansvar {FELLES, MOR, FAR, MEDMOR, ANDRE, UKJENT}

    private Ansvar ansvar;
    private String ansvarlig;
    private String kilde;
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
