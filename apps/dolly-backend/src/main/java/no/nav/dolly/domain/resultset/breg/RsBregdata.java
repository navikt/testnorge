package no.nav.dolly.domain.resultset.breg;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsBregdata {

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

    public enum Egenskap {DELTAGER, KOMPLEMENTAR, KONTAKTPERSON, SAMEIER, STYRE}

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
        @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        private LocalDateTime registreringsdato;

        @Schema
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

        @Schema
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

        @Schema
        private String adresse1;

        private String adresse2;

        private String adresse3;

        private String kommunenr;

        @Schema
        private String landKode;

        private String postnr;

        private String poststed;
    }
}
