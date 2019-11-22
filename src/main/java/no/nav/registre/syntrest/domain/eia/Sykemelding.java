package no.nav.registre.syntrest.domain.eia;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sykemelding {

    @JsonProperty
    private List<Diagnose> biDiagnoser;

    @JsonProperty
    private Diagnose hovedDiagnose;

    @JsonProperty
    @ApiModelProperty(value = "Dato for kontakt med pasient", example = "yyyy-mm-dd")
    private String kontaktMedPasient;

    @JsonProperty
    private boolean meldingTilNav;

    @JsonProperty
    private boolean reisetilskudd;

    @JsonProperty
    @ApiModelProperty(value = "Dato for stans for den gjeldende sykemeldingen.", example = "yyyy-mm-dd")
    private String sluttPeriode;

    @JsonProperty
    @ApiModelProperty(value = "Dato for start for den gjeldende sykemeldingen.", example = "yyyy-mm-dd")
    private String startPeriode;

    @JsonProperty
    @ApiModelProperty("Gradering av sykemelding/stillingsprosent")
    private int sykemeldingprosent;

    @JsonProperty
    @ApiModelProperty("Arbeidsfør etter endt periode")
    private boolean arbeidsforEtterEndtPeriode;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Diagnose {

        @JsonProperty
        @ApiModelProperty("Diagnose navn")
        private String dn;

        @JsonProperty
        private String s;

        @JsonProperty
        @ApiModelProperty("Diagnose kode")
        private String v;
    }
}
