package no.nav.dolly.service;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.dolly.service.RsTransaksjonMapping.TransaksjonId;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransaksjonIdMappingStrategy implements MappingStrategy {

    private final ObjectMapper objectMapper;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(TransaksjonMapping.class, RsTransaksjonMapping.class).customize(new CustomMapper<TransaksjonMapping, RsTransaksjonMapping>() {
            @Override
            public void mapAtoB(TransaksjonMapping transaksjonMapping, RsTransaksjonMapping rsTransaksjonMapping, MappingContext context) {

                try {
                    rsTransaksjonMapping.setTransaksjonId(objectMapper.readValue(transaksjonMapping.getTransaksjonId(), TransaksjonId.class));
                } catch (JsonProcessingException e) {
                    log.error("TransaksjonMapping feilet: ", e);
                }
            }
        })
                .byDefault()
                .register();
    }
}
