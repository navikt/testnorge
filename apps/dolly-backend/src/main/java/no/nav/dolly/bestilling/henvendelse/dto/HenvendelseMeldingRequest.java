package no.nav.dolly.bestilling.henvendelse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class HenvendelseMeldingRequest extends HenvendelseSamtalereferatRequest {

        private boolean tildelMeg;
}
