package no.nav.dolly.bestilling.yrkesskade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YrkesskadeResponseDTO {

    private HttpStatusCode status;
    private String melding;
}
