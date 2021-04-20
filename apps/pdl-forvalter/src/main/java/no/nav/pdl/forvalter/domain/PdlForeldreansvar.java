package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdlForeldreansvar extends PdlDbVersjon {

    private Ansvar ansvar;
    private String ansvarlig;
    private String kilde;
    private RelatertBiPerson ansvarligUtenIdentifikator;
    public enum Ansvar {FELLES, MOR, FAR, MEDMOR, ANDRE, UKJENT}

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatertBiPerson {

        private LocalDate foedselsdato;
        private PdlKjoenn.Kjoenn kjoenn;
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
