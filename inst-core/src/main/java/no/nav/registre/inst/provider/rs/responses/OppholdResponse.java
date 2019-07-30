package no.nav.registre.inst.provider.rs.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import no.nav.registre.inst.Institusjonsforholdsmelding;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OppholdResponse {

    private HttpStatus status;
    private Institusjonsforholdsmelding institusjonsforholdsmelding;
    private String feilmelding;
}
