package no.nav.dolly.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransaksjonIdMappingStrategy implements MappingStrategy {

    private final ObjectMapper objectMapper;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(TransaksjonMapping.class, RsTransaksjonMapping.class).customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(TransaksjonMapping kilde, RsTransaksjonMapping destinasjon, MappingContext context) {

                        try {
                            destinasjon.setTransaksjonId(objectMapper.readTree(kilde.getTransaksjonId()));
                        } catch (JsonProcessingException e) {
                            log.error("Feilet å konvertere {} til JsonNode", kilde.getTransaksjonId());
                        }
                    }
                })
                .exclude("transaksjonId")
                .byDefault()
                .register();
    }
}
