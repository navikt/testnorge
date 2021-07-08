package no.nav.testnav.apps.personservice.consumer.dto.tpsf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonMiljoeResponse {
    String environment;
    TpsPerson person;
}
