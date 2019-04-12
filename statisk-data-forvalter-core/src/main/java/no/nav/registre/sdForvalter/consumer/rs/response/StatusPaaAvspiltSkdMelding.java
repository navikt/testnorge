package no.nav.registre.sdForvalter.consumer.rs.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
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
