package no.nav.registre.syntrest.domain.eia;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pasient {

    @JsonProperty
    String fornavn;
    @JsonProperty
    String mellomnavn;
    @JsonProperty
    String etternavn;
    @JsonProperty
    String fnr;
    @JsonProperty
    Addresse addresse;
    @JsonProperty
    String tlf;
    @JsonProperty
    String fastlege;
    @JsonProperty("nav_kontor")
    String navKontor;
    @JsonProperty
    Arbeidsgiver arbeidsgiver;

    @Getter
    @Setter
    @AllArgsConstructor
    private static class Addresse {

        @JsonProperty
        String gate;
        @JsonProperty
        String postnummer;
        @JsonProperty
        String by;
        @JsonProperty
        Fylke fylke;
        @JsonProperty("land_kode")
        String landKode;

        @Getter
        @Setter
        @AllArgsConstructor
        private static class Fylke {

            @JsonProperty
            String dn;
            @JsonProperty
            String v;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class Arbeidsgiver {

        @JsonProperty
        String navn;
        @JsonProperty
        String yrkesbetegnelse;
        @JsonProperty
        int stillingsprosent;
    }
}
