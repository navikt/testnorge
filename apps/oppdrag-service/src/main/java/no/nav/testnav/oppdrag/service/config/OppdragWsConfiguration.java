package no.nav.testnav.oppdrag.service.config;

import no.nav.testnav.oppdrag.service.consumer.OppdragClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.config.annotation.EnableWs;

@EnableWs
@Configuration
public class OppdragWsConfiguration {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this package must match the package in the <generatePackage> specified in
        // pom.xml
        marshaller.setContextPath("no.nav.testnav.oppdragservice.wsdl");
        return marshaller;
    }

    @Bean
    public OppdragClient countryClient(Jaxb2Marshaller marshaller) {

        var client = new OppdragClient();
        client.setDefaultUri("http://localhost:8080/ws");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
