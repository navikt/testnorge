package no.nav.registre.testnorge.endringsmeldingservice.consumer.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class TpsFoedselsmeldingResponse {
    String personId;
}
