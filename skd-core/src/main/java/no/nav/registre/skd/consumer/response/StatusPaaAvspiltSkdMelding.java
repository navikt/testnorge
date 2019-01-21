package no.nav.registre.skd.consumer.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusPaaAvspiltSkdMelding {

    @JsonProperty("foedselsnummer")
    private String foedselsnummer;

    @JsonProperty("sekvensnummer")
    private Long sekvensnummer;

    @JsonProperty("status")
    private String status;
}
