package no.nav.dolly.mapper.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling.SyntetiskOrganisasjon;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.util.Objects.nonNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonBestillingMapper {

    private final ObjectMapper objectMapper;

    public SyntetiskOrganisasjon mapOrganisasjonBestillingRequest(String jsonInput) {
        try {
            return objectMapper.readValue(nonNull(jsonInput) ? jsonInput : "{}", SyntetiskOrganisasjon.class);
        } catch (IOException e) {
            log.error("Mapping av JSON fra database bestKriterier feilet. {}", e.getMessage(), e);
        }
        return new SyntetiskOrganisasjon();
    }
}
