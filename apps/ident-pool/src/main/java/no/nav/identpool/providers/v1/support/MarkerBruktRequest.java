package no.nav.identpool.providers.v1.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkerBruktRequest {
    private String personidentifikator;
    private String bruker;
}
