package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.pdl.GetPdlPersonCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential.PdlApiProxyProperties;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.PdlPerson;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.FilLaster;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PdlPersonConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;

    private static final String SINGLE_PERSON_QUERY = "pdlperson/pdlquery.graphql";

    public PdlPersonConsumer(
            PdlApiProxyProperties serviceProperties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder().baseUrl(serviceProperties.getUrl()).build();
        this.tokenExchange = tokenExchange;
    }

    public PdlPerson getPdlPersoner(String ident) {
        var query = getSinglePersonQueryFromFile();
        return tokenExchange.exchange(serviceProperties)
                .flatMap(accessToken -> new GetPdlPersonCommand(ident, query, accessToken.getTokenValue(), webClient).call())
                .block();
    }

    private static String getSinglePersonQueryFromFile() {
        try (var reader = new BufferedReader(new InputStreamReader(FilLaster.instans().lastRessurs(SINGLE_PERSON_QUERY), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            log.error("Lesing av query ressurs {} feilet", SINGLE_PERSON_QUERY, e);
            return null;
        }
    }

}