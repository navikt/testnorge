package no.nav.dolly.bestilling.arbeidssoekerregisteret.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArbeidssoekerregisteretResponse {

     private HttpStatus status;
     private String feilmelding;
}