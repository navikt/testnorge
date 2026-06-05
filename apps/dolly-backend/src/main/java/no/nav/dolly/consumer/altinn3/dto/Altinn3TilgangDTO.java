package no.nav.dolly.consumer.altinn3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Altinn3TilgangDTO {

    private String navn;
    private String organisasjonsnummer;
    private String organisasjonsform;
}
