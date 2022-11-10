package no.nav.registre.sdforvalter.consumer.rs.tpsf.response;

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
