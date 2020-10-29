package no.nav.registre.testnorge.arbeidsforhold.consumer.commnad;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.xml.bind.JAXBElement;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.EDAGM;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.ObjectFactory;

@DependencyOn(value = "aareg-synt", external = true)
@RequiredArgsConstructor
public class SaveAmeldingCommand implements Runnable {
    private final WebClient webClient;
    private final EDAGM edagm;

    @Override
    public void run() {

        ObjectFactory objectFactory = new ObjectFactory();

        webClient
                .post()
                .uri(builder -> builder.path("/oppsummeringsdokument").build())
                .body(BodyInserters.fromPublisher(Mono.just(objectFactory.createMelding(edagm)), JAXBElement.class))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
