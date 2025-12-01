package no.nav.testnav.libs.dto.tpsmessagingservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoedselsmeldingRequest {

    private PersonDTO barn;
    private PersonDTO mor;
    private PersonDTO far;
}
