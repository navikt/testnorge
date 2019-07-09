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
    private final AktoerType aktoertype = PERS;
    @JsonProperty("identtype")
    private final String identtype = "FNR";
    @JsonProperty("ident")
    private String ident;
}
