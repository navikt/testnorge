package no.nav.dolly.consumer.brukerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrukerDTO {

    private String id;
    private String brukernavn;
    private String organisasjonsnummer;
}
