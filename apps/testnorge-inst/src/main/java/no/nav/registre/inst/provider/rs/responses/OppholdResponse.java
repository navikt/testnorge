package no.nav.registre.inst.provider.rs.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.inst.domain.Institusjonsopphold;
import org.springframework.http.HttpStatus;

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
