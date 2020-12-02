package no.nav.identpool.rs.v1.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarkerBruktBatchResponse {

    private List<String> personidentifikatorerMarkertSomBrukt;
    private List<String> personidentifikatorerSomIkkeKunneMarkeresSomBrukt;
}
