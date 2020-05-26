package no.nav.registre.frikort.config;

import no.nav.registre.frikort.domain.xml.Egenandelsmelding;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


@Configuration
@ComponentScan
@EnableAutoConfiguration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Marshaller marshaller() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Egenandelsmelding.class);
        Marshaller marshaller = context.createMarshaller();

        // enable pretty-print XML output
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        // set encoding property
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");

        return marshaller;
    }

}