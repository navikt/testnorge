package no.nav.dolly.bestilling.tpsmessagingservice.mapper;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.SpraakDTO;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TpsMessagingMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(String.class, SpraakDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(String rsSpraakKode, SpraakDTO spraakKode, MappingContext context) {

                        spraakKode.setSprakKode(rsSpraakKode);
                    }
                })
                .register();
    }
}
