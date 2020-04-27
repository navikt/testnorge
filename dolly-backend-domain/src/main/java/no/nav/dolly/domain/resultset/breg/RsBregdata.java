package no.nav.dolly.domain.resultset.breg;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
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

    public enum Rollekode {
        BEST, BOBE, DAGL, DELT, DTPR, DTSO, FFØR, INNH, KOMP, KONT,
        LEDE, MEDL, NEST, OBS, PROK, REGN, REPR, SIGN, STYR, SAM, VARA
    }

    @ApiModelProperty(
            position = 1
    )
    private List<RolleTo> enheter;

    @ApiModelProperty(
            position = 2
    )
    private Integer understatuser;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RolleTo {

        @ApiModelProperty(
                position = 1
        )
        private NavnTo foretaksNavn;

        @ApiModelProperty(
                position = 2
        )
        private AdresseTo forretningsAdresse;

        @ApiModelProperty(
                position = 3,
                required = true
        )
        private Integer orgNr;

        @ApiModelProperty(
                position = 4
        )
        private AdresseTo postAdresse;

        @ApiModelProperty(
                position = 5
        )
        private LocalDateTime registreringsdato;

        @ApiModelProperty(
                position = 6,
                required = true
        )
        private Rollekode rollekode;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NavnTo {

        @ApiModelProperty(
                position = 1,
                required = true
        )
        private String navn1;

        @ApiModelProperty(
                position = 2
        )
        private String navn2;

        @ApiModelProperty(
                position = 3
        )
        private String navn3;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AdresseTo {

        @ApiModelProperty(
                position = 1,
                required = true
        )
        private String adresse1;

        @ApiModelProperty(
                position = 2
        )
        private String adresse2;

        @ApiModelProperty(
                position = 3
        )
        private String adresse3;

        @ApiModelProperty(
                position = 4
        )
        private String kommune;

        @ApiModelProperty(
                position = 5
        )
        private String kommunenr;

        @ApiModelProperty(
                position = 6,
                required = true
        )
        private String land;

        @ApiModelProperty(
                position = 7,
                required = true
        )
        private String landKode;

        @ApiModelProperty(
                position = 8,
                required = true
        )
        private String postnr;

        @ApiModelProperty(
                position = 9,
                required = true
        )
        private String poststed;
    }
}
