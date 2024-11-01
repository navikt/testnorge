package no.nav.testnav.oppdragservice.config;

import jakarta.xml.bind.JAXBException;
import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.oppdragservice.consumer.OppdragConsumer;
import no.nav.testnav.oppdragservice.mapper.LocalDateCustomMapping;
import no.nav.testnav.oppdragservice.mapper.LocalDateTimeCustomMapping;
import no.nav.testnav.oppdragservice.mapper.MapperTestUtils;
import no.nav.testnav.oppdragservice.mapper.OppdragRequestMappingStrategy;
import no.nav.testnav.oppdragservice.service.OppdragService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class TestConfig {

    @Bean
    public OppdragService oppdragService() throws JAXBException {
        return new OppdragService(oppdragConsumer(), mapperFacade());
    }

    @Bean
    public OppdragConsumer oppdragConsumer() {
        return new OppdragConsumer();
    }

    @Bean
    public MapperFacade mapperFacade() {

        return MapperTestUtils.createMapperFacadeForMappingStrategy(
                List.of(new LocalDateCustomMapping(),
                        new LocalDateTimeCustomMapping()),
                new OppdragRequestMappingStrategy());
    }
}