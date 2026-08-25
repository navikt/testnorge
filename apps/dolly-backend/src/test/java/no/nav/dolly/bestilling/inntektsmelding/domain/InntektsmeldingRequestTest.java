package no.nav.dolly.bestilling.inntektsmelding.domain;

import no.nav.dolly.domain.resultset.inntektsmeldingstub.NaturalytelseType;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class InntektsmeldingRequestTest {

    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    void testJsonSerializationOfNaturalYtelseDetaljer() {

        for (NaturalytelseType type : NaturalytelseType.values()) {
            var detail = new InntektsmeldingRequest.NaturalYtelseDetaljer();
            detail.setNaturalytelseType(type);
            var json = jsonMapper.writeValueAsString(detail);
            assertThat(json)
                    .contains("\"naturalytelseType\":\"" + type + "\"");
        }

    }

}
