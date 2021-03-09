package no.nav.registre.testnorge.helsepersonell.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.helsepersonell.consumer.command.GetAlleIdenterCommand;
import no.nav.registre.testnorge.helsepersonell.consumer.command.GetPersondataCommand;
import no.nav.registre.testnorge.helsepersonell.domain.Persondata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class HodejegerenConsumer {
    private static final String MILJOE = "q1";
    private final Executor executor;
    private final Long helsepersonellAvspillingsgruppeId;
    private final WebClient webClient;

    public HodejegerenConsumer(
            @Value("${hodejegeren.api.url}") String url,
            @Value("${hodejegeren.api.threads}") Integer threads,
            @Value("${avspillingsgruppe.helsepersonell.id}") Long helsepersonellAvspillingsgruppeId
    ) {
        this.webClient = WebClient.builder().baseUrl(url).build();
        this.executor = Executors.newFixedThreadPool(threads);
        this.helsepersonellAvspillingsgruppeId = helsepersonellAvspillingsgruppeId;
    }

    public CompletableFuture<Persondata> getPersondata(String ident) {
        return CompletableFuture.supplyAsync(
                () -> new Persondata(new GetPersondataCommand(ident, MILJOE, webClient).call()),
                executor
        );
    }

    public Set<String> getHelsepersonell() {
        return new GetAlleIdenterCommand(helsepersonellAvspillingsgruppeId, webClient).call();
    }
}
