package no.nav.registre.testnorge.applikasjonsanalyseservice.domain.yml;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class AccessPolicy {
    Inbound inbound;
    Outbound outbound;
}
