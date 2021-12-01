package no.nav.registre.testnorge.helsepersonellservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import no.nav.registre.testnorge.helsepersonellservice.config.credentials.HodejegerenServerProperties;
import no.nav.registre.testnorge.helsepersonellservice.consumer.command.GetAlleIdenterCommand;
import no.nav.registre.testnorge.helsepersonellservice.consumer.command.GetPersondataCommand;
import no.nav.registre.testnorge.helsepersonellservice.domain.Persondata;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class HodejegerenConsumer {
    private static final String MILJOE = "q1";
    private final Executor executor;
    private final Long helsepersonellAvspillingsgruppeId;
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final HodejegerenServerProperties hodejegerenServerProperties;

    public HodejegerenConsumer(
            TokenExchange tokenExchange,
            HodejegerenServerProperties hodejegerenServerProperties,
            @Value("${avspillingsgruppe.helsepersonell.id}") Long helsepersonellAvspillingsgruppeId
    ) {
        this.hodejegerenServerProperties = hodejegerenServerProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient.builder().baseUrl(hodejegerenServerProperties.getUrl()).build();
        this.executor = Executors.newFixedThreadPool(hodejegerenServerProperties.getThreads());
        this.helsepersonellAvspillingsgruppeId = helsepersonellAvspillingsgruppeId;
    }

    public CompletableFuture<Persondata> getPersondata(String ident) {
        var accessToken = tokenExchange.generateToken(hodejegerenServerProperties).block();
        return CompletableFuture.supplyAsync(
                () -> new Persondata(new GetPersondataCommand(ident, MILJOE, webClient, accessToken.getTokenValue()).call()),
                executor
        );
    }

    public Set<String> getHelsepersonell() {
        var accessToken = tokenExchange.generateToken(hodejegerenServerProperties).block();
        return new GetAlleIdenterCommand(helsepersonellAvspillingsgruppeId, webClient, accessToken.getTokenValue()).call();
    }
}
