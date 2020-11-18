package no.nav.registre.orgnrservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class EregCommand implements Callable<String> {

    private final WebClient webClient;
    private final String orgnummer;
    private final String miljoe;


    public String call() {
        log.info("Henter orgnummer {} fra miljoe {}", orgnummer, miljoe);
        var response = webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path("ereg/api/v1/organisasjon/{orgnummer}?inkluderHierarki=false&inkluderHistorikk=false")
                                .queryParam("miljoe", miljoe)
                                .queryParam("orgnummer", orgnummer)
                                .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return "hei";
    }
}