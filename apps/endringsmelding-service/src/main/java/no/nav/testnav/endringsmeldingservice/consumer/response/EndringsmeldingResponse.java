package no.nav.testnav.endringsmeldingservice.consumer.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Map;


@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class EndringsmeldingResponse {
    String personId;
    Map<String, String> status;
}
