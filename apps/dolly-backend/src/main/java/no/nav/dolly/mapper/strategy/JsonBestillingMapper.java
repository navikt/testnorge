package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling.SyntetiskOrganisasjon;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonBestillingMapper {

    private final ObjectMapper objectMapper;

    public SyntetiskOrganisasjon mapOrganisasjonBestillingRequest(String jsonInput) {
        try {
            return objectMapper.readValue(nonNull(jsonInput) ? jsonInput : "{}", SyntetiskOrganisasjon.class);
        } catch (JacksonException e) {
            log.error("Mapping av JSON fra database bestKriterier feilet. {}", e.getMessage(), e);
        }
        return new SyntetiskOrganisasjon();
    }
}
