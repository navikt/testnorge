package no.nav.testnav.apps.personservice.consumer.v1.pdl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class OpprettPersonDTO {
    String opprettetPersonident;
}
