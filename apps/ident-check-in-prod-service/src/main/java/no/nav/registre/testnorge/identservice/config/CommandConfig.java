package no.nav.registre.testnorge.identservice.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.Command;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.M201HentFnrNavnDiskresjonPaFlerePersoner;
import no.nav.registre.testnorge.identservice.testdata.servicerutiner.resolvers.ServiceRoutineResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = Command.class)
public class CommandConfig {

    @Bean
    XmlMapper xmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        xmlMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        return xmlMapper;
    }

    @Bean
    ServiceRoutineResolver hentHistorieForFlereFnr() {
        return new M201HentFnrNavnDiskresjonPaFlerePersoner();
    }
}
