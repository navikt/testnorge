package no.nav.registre.testnorge.arbeidsforhold.consumer.commnad;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.EDAGM;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.ObjectFactory;

@DependencyOn(value = "aareg-synt", external = true)
@RequiredArgsConstructor
public class SaveAmeldingCommand implements Runnable {
    private final WebClient webClient;
    private final EDAGM edagm;

    @SneakyThrows
    @Override
    public void run() {
        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<EDAGM> melding = objectFactory.createMelding(edagm);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(melding, sw);

        webClient
                .post()
                .uri(builder -> builder.path("/oppsummeringsdokument").build())
                .body(BodyInserters.fromPublisher(Mono.just(sw.toString()), String.class))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
