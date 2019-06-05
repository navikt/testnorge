package no.nav.registre.sdForvalter.consumer.rs.request.AaregRequest;

import static no.nav.registre.sdForvalter.consumer.rs.request.AaregRequest.AktoerType.PERS;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
