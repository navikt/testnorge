package no.nav.testnav.apps.tenorsearchservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tenorsearchservice.consumers.TenorClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class TenorSearchService {

    private final TenorClient tenorClient;

    public Mono<JsonNode> getTestdata(String testDataQuery) {

        return tenorClient.getTestdata(isNotBlank(testDataQuery) ? testDataQuery : "");
    }
}
