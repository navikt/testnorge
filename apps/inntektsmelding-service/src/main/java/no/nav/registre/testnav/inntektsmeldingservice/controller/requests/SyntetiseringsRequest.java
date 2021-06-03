package no.nav.registre.testnav.inntektsmeldingservice.controller.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SyntetiseringsRequest {

    @JsonProperty("avspillergruppeId")
    private Long avspillergruppeId;

    @JsonProperty("miljoe")
    private String miljoe;
}
