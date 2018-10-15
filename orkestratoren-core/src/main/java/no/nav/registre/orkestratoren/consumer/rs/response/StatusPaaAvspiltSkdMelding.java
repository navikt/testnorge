package no.nav.registre.orkestratoren.consumer.rs.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusPaaAvspiltSkdMelding {
    private String foedselsnummer;
    private Long sekvensnummer;
    private String status;
}
