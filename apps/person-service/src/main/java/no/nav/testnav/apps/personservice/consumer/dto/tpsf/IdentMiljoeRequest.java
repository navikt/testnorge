package no.nav.testnav.apps.personservice.consumer.dto.tpsf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class IdentMiljoeRequest {
    String ident;
    List<String> miljoe;
}
