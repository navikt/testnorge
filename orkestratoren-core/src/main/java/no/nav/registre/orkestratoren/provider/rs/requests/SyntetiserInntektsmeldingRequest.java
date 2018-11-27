package no.nav.registre.orkestratoren.provider.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SyntetiserInntektsmeldingRequest {

    private Long skdMeldingGruppeId;
}
