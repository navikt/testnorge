package no.nav.registre.sdForvalter.consumer.rs.request.AaregRequest;

import static no.nav.registre.sdForvalter.consumer.rs.request.AaregRequest.AktoerType.ORG;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Arbeidsgiver {

    @JsonProperty("aktoertype")
    private final AktoerType aktoertype = ORG;

    @JsonProperty("orgnummer")
    private String orgnummer;
}
