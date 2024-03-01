package no.nav.testnav.libs.data.tpsmessagingservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoedselsmeldingRequest {

    private PersonDTO barn;
    private PersonDTO mor;
    private PersonDTO far;
}
