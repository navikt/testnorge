package no.nav.registre.testnorge.identservice.testdata.config;

import no.nav.registre.testnorge.identservice.testdata.environments.FasitApiConsumer;
import no.nav.registre.testnorge.identservice.testdata.mapping.MapperConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan(basePackageClasses = {
        FasitApiConsumer.class,
        MapperConfig.class
})
public class FetchEnvironmentsConsumerConfig {
}