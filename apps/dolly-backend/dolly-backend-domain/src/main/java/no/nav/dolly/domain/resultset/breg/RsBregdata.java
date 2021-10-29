package no.nav.dolly.domain.resultset.breg;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsBregdata {

    public enum Egenskap {Deltager, Komplementar, Kontaktperson, Sameier, Styre}

    private List<RolleTo> enheter;

    private List<Integer> understatuser;

    public List<RolleTo> getEnheter() {
        if (isNull(enheter)) {
            enheter = new ArrayList<>();
        }
        return enheter;
    }

    public List<Integer> getUnderstatuser() {
        if (isNull(understatuser)) {
            understatuser = new ArrayList<>();
        }
        return understatuser;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RolleTo {

        private NavnTo foretaksNavn;

        private AdresseTo forretningsAdresse;

        private Integer orgNr;

        private AdresseTo postAdresse;

        @Schema(defaultValue = "dagens dato")
        private LocalDateTime registreringsdato;

        @Schema(required = true)
        private String rolle;

        private List<PersonRolle> personroller;

        public List<PersonRolle> getPersonroller() {
            if (isNull(personroller)) {
                personroller = new ArrayList<>();
            }
            return personroller;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PersonRolle {

        private Egenskap egenskap;

        @Schema(defaultValue = "false")
        private Boolean fratraadt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NavnTo {

        @Schema(required = true)
        private String navn1;

        private String navn2;

        private String navn3;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AdresseTo {

        @Schema(required = true)
        private String adresse1;

        private String adresse2;

        private String adresse3;

        private String kommunenr;

        @Schema(required = true)
        private String landKode;

        private String postnr;

        private String poststed;
    }
}
