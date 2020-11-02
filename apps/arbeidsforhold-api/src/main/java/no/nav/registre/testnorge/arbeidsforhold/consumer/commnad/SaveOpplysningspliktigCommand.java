package no.nav.registre.testnorge.arbeidsforhold.consumer.commnad;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;

@Slf4j
@DependencyOn(value = "aareg-synt", external = true)
@RequiredArgsConstructor
public class SaveOpplysningspliktigCommand implements Runnable {
    private final WebClient webClient;
    private final String xml;

    @SneakyThrows
    @Override
    public void run() {
        log.trace(xml);

        //TODO
//        webClient
//                .post()
//                .uri(builder -> builder.path("/oppsummeringsdokument").build())
//                .body(BodyInserters.fromPublisher(Mono.just(xml), String.class))
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
//                .retrieve()
//                .bodyToMono(Void.class)
//                .block();
    }
}
