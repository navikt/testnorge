package no.nav.dolly.mapper.strategy;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonBestillingMapper {

    private final ObjectMapper objectMapper;

    public RsTpsfUtvidetBestilling mapTpsfRequest(String jsonInput) {
        try {
            if (isBlank(jsonInput)) {
                return null;
            }
            RsTpsfUtvidetBestilling tpsfUtvidetBestilling = objectMapper.readValue(jsonInput, RsTpsfUtvidetBestilling.class);
            if (nonNull(tpsfUtvidetBestilling.getRelasjoner()) && nonNull(tpsfUtvidetBestilling.getRelasjoner().getPartner())) {
                tpsfUtvidetBestilling.getRelasjoner().getPartnere().add(tpsfUtvidetBestilling.getRelasjoner().getPartner());
                tpsfUtvidetBestilling.getRelasjoner().setPartner(null);
            }
            return tpsfUtvidetBestilling;
        } catch (IOException e) {
            log.error("Mapping av JSON fra database tpsfKriterier feilet. {}", e.getMessage(), e);
        }
        return new RsTpsfUtvidetBestilling();
    }

    public RsDollyBestillingRequest mapBestillingRequest(String jsonInput) {
        try {
            return objectMapper.readValue(nonNull(jsonInput) ? jsonInput : "{}", RsDollyBestillingRequest.class);
        } catch (IOException e) {
            log.error("Mapping av JSON fra database bestKriterier feilet. {}", e.getMessage(), e);
        }
        return new RsDollyBestillingRequest();
    }
}
