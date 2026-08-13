package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling.SyntetiskOrganisasjon;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonBestillingMapper {

    private final JsonMapper jsonMapper;

    public SyntetiskOrganisasjon mapOrganisasjonBestillingRequest(String jsonInput) {
        try {
            return jsonMapper.readValue(nonNull(jsonInput) ? jsonInput : "{}", SyntetiskOrganisasjon.class);
        } catch (JacksonException e) {
            log.error("Mapping av JSON fra database bestKriterier feilet. {}", e.getMessage(), e);
        }
        return new SyntetiskOrganisasjon();
    }
}
