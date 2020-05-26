package no.nav.registre.testnorge.elsam.consumer.rs.response.ereg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EregResponse {

    private String organisasjonsnummer;
    private String type;
    private EregNavn navn;
    private Organisasjonsdetaljer organisasjonDetaljer;
}
