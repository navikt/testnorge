package no.nav.registre.sdForvalter.consumer.rs.request.aareg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static no.nav.registre.sdForvalter.consumer.rs.request.aareg.AktoerType.ORG;

@Getter
@Setter
@AllArgsConstructor
public class Arbeidsgiver {

    @JsonProperty("aktoertype")
    private final AktoerType aktoertype = ORG;

    @JsonProperty("orgnummer")
    private String orgnummer;
}
