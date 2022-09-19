package no.nav.dolly.bestilling.brregstub.domain;

import static java.util.Objects.isNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RolleoversiktTo {

    public enum Egenskap {Deltager, Komplementar, Kontaktperson, Sameier, Styre}

    private AdresseTo adresse;
    private List<RolleTo> enheter;
    private String fnr;
    private LocalDate fodselsdato;
    private Integer hovedstatus;
    private NavnTo navn;
    private List<Integer> understatuser;

    public List<Integer> getUnderstatuser() {
        if (isNull(understatuser)){
            understatuser = new ArrayList<>();
        }
        return understatuser;
    }

    public List<RolleTo> getEnheter() {
        if (isNull(enheter)) {
            enheter = new ArrayList<>();
        }
        return enheter;
    }

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RolleTo {

        @EqualsAndHashCode.Exclude
        private NavnTo foretaksNavn;

        @EqualsAndHashCode.Exclude
        private AdresseTo forretningsAdresse;

        private Integer orgNr;

        private List<RolleStatus> personRolle;

        @EqualsAndHashCode.Exclude
        private AdresseTo postAdresse;

        @EqualsAndHashCode.Exclude
        private LocalDate registreringsdato;

        private String rolle;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AdresseTo {

        private String adresse1;
        private String adresse2;
        private String adresse3;
        private String kommunenr;
        private String landKode;
        private String postnr;
        private String poststed;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RolleStatus {

        private Egenskap egenskap;
        private Boolean fratraadt;
    }
}