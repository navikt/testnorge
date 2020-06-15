package no.nav.registre.testnorge.helsepersonell.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import no.nav.registre.testnorge.helsepersonell.consumer.command.GetAlleIdenterCommand;
import no.nav.registre.testnorge.helsepersonell.consumer.command.GetPersondataCommand;
import no.nav.registre.testnorge.helsepersonell.domain.Persondata;

@Slf4j
@Component
public class HodejegerenConsumer {
    private static final String MILJOE = "q1";
    private final String url;
    private final RestTemplate restTemplate;
    private final Executor executor;
    private final Long legerAvspillingsgruppeId;

    public HodejegerenConsumer(
            @Value("${hodejegeren.api.url}") String url,
            @Value("${hodejegeren.api.threads}") Integer threads,
            @Value("${avspillingsgruppe.leger.id}") Long legerAvspillingsgruppeId,
            RestTemplateBuilder restTemplateBuilder) {
        this.url = url;
        this.restTemplate = restTemplateBuilder.build();
        this.executor = Executors.newFixedThreadPool(threads);
        this.legerAvspillingsgruppeId = legerAvspillingsgruppeId;
    }

    public CompletableFuture<Persondata> getPersondata(String ident) {
        return CompletableFuture.supplyAsync(
                () -> new Persondata(new GetPersondataCommand(url, ident, MILJOE, restTemplate).call()),
                executor
        );
    }

    public Set<String> getLeger() {
        return new GetAlleIdenterCommand(url, legerAvspillingsgruppeId, restTemplate).call();
    }
}
