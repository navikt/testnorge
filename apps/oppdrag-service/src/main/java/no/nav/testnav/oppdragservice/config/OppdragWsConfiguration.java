package no.nav.testnav.oppdragservice.config;

import no.nav.testnav.oppdragservice.consumer.OppdragClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;

@EnableWs
@Configuration
public class OppdragWsConfiguration extends WsConfigurerAdapter {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this package must match the package in the <generatePackage> specified in
        // build.gradle
        marshaller.setContextPath("no.nav.testnav.oppdragservice.wsdl");
        return marshaller;
    }

    @Bean
    public OppdragClient oppdragClient(Jaxb2Marshaller marshaller) {

        var client = new OppdragClient();
        client.setDefaultUri("http://localhost:8080/ws");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
