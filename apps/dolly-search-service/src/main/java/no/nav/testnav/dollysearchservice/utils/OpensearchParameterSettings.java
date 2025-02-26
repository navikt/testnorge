package no.nav.testnav.dollysearchservice.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.consumer.ElasticParamsConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Profile("!test")
@Component
@RequiredArgsConstructor
public class OpensearchParameterSettings implements ApplicationListener<ContextRefreshedEvent> {

    private static final String INDEX_SETTING =
            "{\"settings\":{\"index\":{\"max_terms_count\":\"%s\"}}}";

    @Value("${open.search.max-terms-count}")
    private String maxTermsCount;

    private final ElasticParamsConsumer elasticParamsConsumer;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        try {
            var indexSetting = INDEX_SETTING.formatted(maxTermsCount);
            var jsonFactory = objectMapper.getFactory();
            var jsonParser = jsonFactory.createParser(indexSetting);
            var jsonNode = (JsonNode) objectMapper.readTree(jsonParser);
            elasticParamsConsumer.oppdaterParametre(jsonNode)
                    .subscribe(status -> log.info("OpenSearch oppdatering av indeks, status: {}", status));

        } catch (IOException e) {
            log.error("Feilet å gjøre setting for indekser {}", INDEX_SETTING, e);
        }
    }
}