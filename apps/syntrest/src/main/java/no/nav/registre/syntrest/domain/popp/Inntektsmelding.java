package no.nav.registre.syntrest.domain.popp;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Inntektsmelding {

    @ApiModelProperty("Liste med grunnlag")
    @JsonProperty("grunnlag")
    private List<Grunnlag> grunnlag;

    @JsonProperty("inntektsaar")
    private String inntektsaar;

    @JsonProperty("personidentifikator")
    private String personidentifikator;

    @JsonProperty("testdataEier")
    private String testdataEier;

    @JsonProperty("tjeneste")
    private String tjeneste;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Grunnlag {
        @JsonProperty("tekniskNavn")
        private String tekniskNavn;

        @JsonProperty("verdi")
        private String verdi;
    }
}