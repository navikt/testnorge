package no.nav.dolly.consumer.organisasjon.tilgang.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganisasjonTilgang {

    private String navn;
    private Integer organisasjonsnummer;
    private String organisasjonsform;
    private LocalDateTime gyldigTil;
    private String miljoe;
}
