package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdlForeldreansvar extends PdlDbVersjon {

    private Ansvar ansvar;
    private String ansvarlig;
    private RelatertBiPerson ansvarligUtenIdentifikator;
    public enum Ansvar {FELLES, MOR, FAR, MEDMOR, ANDRE, UKJENT}

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatertBiPerson implements Serializable {

        private LocalDateTime foedselsdato;
        private PdlKjoenn.Kjoenn kjoenn;
        private Personnavn navn;
        private String statsborgerskap;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Personnavn implements Serializable {

        private String etternavn;
        private String fornavn;
        private String mellomnavn;
    }
}
