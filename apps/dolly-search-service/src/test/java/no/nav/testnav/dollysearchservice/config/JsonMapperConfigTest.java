package no.nav.testnav.dollysearchservice.config;

import no.nav.dolly.libs.test.DollyApplicationContextTest;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.Test;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
class JsonMapperConfigTest extends DollyApplicationContextTest {

    @MockitoBean
    OpenSearchClient openSearchClient; // Not actually used in any tests, but required by BestillingQueryService.

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void shouldApplyConfiguredDateTimeDeserializers() {

        var dto = jsonMapper.readValue("""
                {
                  "localDate": "2026-01-02T12:34:56",
                  "localDateTime": "2026-01-02"
                }
                """, DateFieldsDto.class);

        assertThat(dto.localDate()).isEqualTo(LocalDate.of(2026, 1, 2));
        assertThat(dto.localDateTime()).isEqualTo(LocalDateTime.of(2026, 1, 2, 0, 0));

    }

    private record DateFieldsDto(LocalDate localDate, LocalDateTime localDateTime) {
    }

}
