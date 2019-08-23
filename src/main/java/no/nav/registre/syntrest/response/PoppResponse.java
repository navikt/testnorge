package no.nav.registre.syntrest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PoppResponse {
//    [
//  {
//    "grunnlag": [
//      {
//        "tekniskNavn": "personinntektLoenn",
//        "verdi": "101827"
//      }
//    ],
//    "inntektsaar": "2018",
//    "personidentifikator": "19112937628",
//    "testdataEier": "synt_test",
//    "tjeneste": "Beregnet skatt"
//  },
//  {
//    "grunnlag": [
//      {
//        "tekniskNavn": "personinntektFiskeFangstFamiliebarnehage",
//        "verdi": "8998"
//      }
//    ],
//    "inntektsaar": "2018",
//    "personidentifikator": "19112937628",
//    "testdataEier": "synt_test",
//    "tjeneste": "Beregnet skatt"
//  }
//  ]

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
    private static class Grunnlag {

        @JsonProperty("tekniskNavn")
        private String tekniskNavn;

        @JsonProperty("verdi")
        private String verdi;

    }
}
