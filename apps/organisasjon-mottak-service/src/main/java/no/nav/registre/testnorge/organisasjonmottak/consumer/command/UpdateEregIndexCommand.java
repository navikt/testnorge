package no.nav.registre.testnorge.organisasjonmottak.consumer.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
public class UpdateEregIndexCommand implements Runnable {
    private final WebClient webClient;
    private final LocalDateTime startTime;

    @Override
    public void run() {

        var data = new IndexJob(startTime);

        log.info("Starter index Ereg jobb med argumentene: {}.", data);

        webClient
                .post()
                .uri(builder -> builder.path("/oppsummeringsdokument").build())
                .body(BodyInserters.fromPublisher(Mono.just(data), IndexJob.class))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Getter
    @ToString
    private class IndexJob {
        private final String jobParameters;

        public IndexJob(LocalDateTime startTime) {
            String value = startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss:SS"));
            this.jobParameters = "startTime=" + value + ",modus=AJOURHOLD,workUnit=100,stepSelection=7;8";
        }
    }
}
