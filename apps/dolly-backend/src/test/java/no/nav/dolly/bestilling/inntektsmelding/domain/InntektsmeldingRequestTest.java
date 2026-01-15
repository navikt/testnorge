package no.nav.dolly.bestilling.inntektsmelding.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.NaturalytelseType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class InntektsmeldingRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testJsonSerializationOfNaturalYtelseDetaljer()
            throws JsonProcessingException {

        for (NaturalytelseType type : NaturalytelseType.values()) {
            var detail = new InntektsmeldingRequest.NaturalYtelseDetaljer();
            detail.setNaturalytelseType(type);
            var json = objectMapper.writeValueAsString(detail);
            assertThat(json)
                    .contains("\"naturalytelseType\":\"" + type + "\"");
        }

    }

}
