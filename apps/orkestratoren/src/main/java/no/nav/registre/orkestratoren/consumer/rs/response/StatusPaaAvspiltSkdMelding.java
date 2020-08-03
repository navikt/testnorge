package no.nav.registre.orkestratoren.consumer.rs.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusPaaAvspiltSkdMelding {

    private String foedselsnummer;
    private Long sekvensnummer;
    private String status;
}
