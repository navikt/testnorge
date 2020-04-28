package no.nav.dolly.bestilling.bregstub.domain;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RolleutskriftTo {

    private AdresseTo adresse;
    private List<RolleTo> enheter;
    private String fnr;
    private LocalDate fodselsdato;
    private Integer hovedstatus;
    private NavnTo navn;
    private Integer understatuser;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RolleTo {

        private NavnTo foretaksNavn;
        private AdresseTo forretningsAdresse;
        private String orgNr;
        private AdresseTo postAdresse;
        private LocalDate registreringsdato;
        private String rollebeskrivelse;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NavnTo {

        private String navn1;
        private String navn2;
        private String navn3;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdresseTo {

        private String adresse1;
        private String adresse2;
        private String adresse3;
        private String kommunenr;
        private String landKode;
        private String postnr;
        private String poststed;
    }
}
