package no.nav.registre.hodejegeren.mongodb.tpsStatusQuo.Kjerneinfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Personnavn {

    private String gjeldendePersonnavn;
    private AllePersonnavn allePersonnavn;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AllePersonnavn {

        private String kortnavn;
        private String navnTidspunkt;
        private String mellomnavn;
        private String etternavn;
        private String navnSaksbehandler;
        private String fornavn;
        private String navnSystem;
    }
}
