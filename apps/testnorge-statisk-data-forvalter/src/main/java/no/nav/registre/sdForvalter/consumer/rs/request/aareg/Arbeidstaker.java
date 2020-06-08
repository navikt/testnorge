package no.nav.registre.sdForvalter.consumer.rs.request.aareg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static no.nav.registre.sdForvalter.consumer.rs.request.aareg.AktoerType.PERS;

@Getter
@Setter
@AllArgsConstructor
public class Arbeidstaker {

    @JsonProperty("aktoertype")
    private static final AktoerType aktoertype = PERS;
    @JsonProperty("identtype")
    private static final String identtype = "FNR";
    @JsonProperty("ident")
    private String ident;
}
