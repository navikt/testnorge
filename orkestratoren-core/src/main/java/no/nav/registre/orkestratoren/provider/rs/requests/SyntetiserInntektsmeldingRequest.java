package no.nav.registre.orkestratoren.provider.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SyntetiserInntektsmeldingRequest {

    private Long skdMeldingGruppeId;
    private String miljoe;
}
