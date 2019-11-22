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
public class Lege {

    @JsonProperty
    String fornavn;
    @JsonProperty
    String mellomnavn;
    @JsonProperty
    String etternavn;
    @JsonProperty
    String fnr;
    @JsonProperty("her_id")
    long herId;
    @JsonProperty("hpr_id")
    long hprId;
    @JsonProperty
    String epost;
    @JsonProperty
    String tlf;
    @JsonProperty
    Legekontor legekontor;

    @Getter
    @Setter
    @AllArgsConstructor
    private static class Legekontor {

        @JsonProperty
        String navn;
        @JsonProperty("ereg_id")
        String eregId;
        @JsonProperty("her_id")
        long herId;
        @JsonProperty
        Addresse addresse;

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
        }
    }
}
