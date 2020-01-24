package no.nav.dolly.mapper.strategy;

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper;
import no.nav.dolly.mapper.MappingStrategy;
import springfox.documentation.spring.web.json.Json;

@Slf4j
@Component
@RequiredArgsConstructor
public class MalBestillingMappingStrategy implements MappingStrategy {

    private final ObjectMapper objectMapper;

    @Override public void register(MapperFactory factory) {
        factory.classMap(Bestilling.class, RsMalBestillingWrapper.RsBestilling.class)
                .customize(new CustomMapper<Bestilling, RsMalBestillingWrapper.RsBestilling>() {
                    @Override
                    public void mapAtoB(Bestilling bestilling, RsMalBestillingWrapper.RsBestilling malBestilling, MappingContext context) {

                        RsDollyBestillingRequest bestillingRequest = mapBestillingRequest(bestilling.getBestKriterier());
                        mapperFacade.map(bestillingRequest, malBestilling);
                        malBestilling.setEnvironments(newArrayList(bestilling.getMiljoer().split(",")));
                        malBestilling.setTpsf(mapperFacade.map(bestilling.getTpsfKriterier(), Json.class));
                    }

                    private RsDollyBestillingRequest mapBestillingRequest(String jsonInput) {
                        try {
                            return objectMapper.readValue(jsonInput, RsDollyBestillingRequest.class);
                        } catch (IOException e) {
                            log.error("Mapping av JSON fra database bestKriterier feilet. {}", e.getMessage(), e);
                        }
                        return new RsDollyBestillingRequest();
                    }
                })
                .byDefault()
                .register();
    }
}
