package no.nav.dolly.bestilling.inntektsmelding.domain;

import no.nav.dolly.domain.resultset.inntektsmeldingstub.NaturalytelseType;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class InntektsmeldingRequestTest {

    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    @Test
    void testJsonSerializationOfNaturalYtelseDetaljer() {

        for (NaturalytelseType type : NaturalytelseType.values()) {
            var detail = new InntektsmeldingRequest.NaturalYtelseDetaljer();
            detail.setNaturalytelseType(type);
            var json = objectMapper.writeValueAsString(detail);
            assertThat(json)
                    .contains("\"naturalytelseType\":\"" + type + "\"");
        }

    }

}
