package no.nav.registre.orkestratoren.consumer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import no.nav.registre.orkestratoren.consumer.command.CreateSyntSykemeldingCommand;
import no.nav.registre.testnorge.dto.arbeidsforhold.v1.ArbeidsforholdDTO;

@Slf4j
@Component
public class SyntSykemeldingConsumer {
    private final RestTemplate restTemplate;
    private final String url;
    private final Executor executor;

    public SyntSykemeldingConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${consumers.syntsykemelding.url}") String url,
            @Value("${consumers.syntsykemelding.threads}") Integer threads
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = url;
        this.executor = Executors.newFixedThreadPool(threads);
    }

    @SneakyThrows
    public CompletableFuture<Void> createSyntSykemldinger(String ident, ArbeidsforholdDTO arbeidsforhold, LocalDate startDate) {
        log.info(
                "Oppretter synt sykemelding for {} i organisjasjon {} med arbeidsforhold id {}.",
                ident,
                arbeidsforhold.getOrgnummer(),
                arbeidsforhold.getArbeidsforholdId()
        );
        var command = new CreateSyntSykemeldingCommand(restTemplate, url, ident, arbeidsforhold, startDate);
        return CompletableFuture.supplyAsync(command::call, executor);
    }
}
