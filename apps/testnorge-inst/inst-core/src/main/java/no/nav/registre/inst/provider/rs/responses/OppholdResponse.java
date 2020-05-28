package no.nav.registre.inst.provider.rs.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import no.nav.registre.inst.Institusjonsopphold;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OppholdResponse {

    private String personident;
    private HttpStatus status;
    private Institusjonsopphold institusjonsopphold;
    private String feilmelding;
}
