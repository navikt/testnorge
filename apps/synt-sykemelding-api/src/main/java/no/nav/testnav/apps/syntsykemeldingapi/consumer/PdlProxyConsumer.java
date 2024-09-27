package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntsykemeldingapi.config.Consumers;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.command.GetPdlPersonCommand;
import no.nav.testnav.apps.syntsykemeldingapi.domain.pdl.PdlPerson;
import no.nav.testnav.apps.syntsykemeldingapi.exception.PdlPersonException;
import no.nav.testnav.apps.syntsykemeldingapi.util.FilLaster;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Component
public class PdlProxyConsumer {
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final WebClient webClient;

    private static final String SINGLE_PERSON_QUERY = "pdlperson/pdlquery.graphql";

    public PdlProxyConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient.Builder webClientBuilder) {

        serverProperties = consumers.getTestnavPdlProxy();
        this.tokenExchange = tokenExchange;
        this.webClient = webClientBuilder
                .exchangeStrategies(ExchangeStrategies
                        .builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public PdlPerson getPdlPerson(String ident) {
        if (isNull(ident) || ident.equals("")) {
            return null;
        }
        try {
            var query = getQueryFromFile(SINGLE_PERSON_QUERY);
            var response = tokenExchange.exchange(serverProperties)
                    .flatMap(accessToken -> new GetPdlPersonCommand(ident, query, accessToken.getTokenValue(), webClient).call())
                    .block();
            if (nonNull(response) && !response.getErrors().isEmpty()) {
                var melding = response.getErrors().get(0).getMessage();
                log.error("Klarte ikke hente pdlperson: " + melding);
                throw new PdlPersonException("Feil i henting av person fra pdl" + melding);
            }
            return response;
        } catch (Exception e) {
            log.error("Klarte ikke hente pdlperson.", e);
            throw new PdlPersonException("Feil i henting av person fra pdl");
        }

    }

    private static String getQueryFromFile(String file) {
        try (var reader = new BufferedReader(new InputStreamReader(FilLaster.instans().lastRessurs(file), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            log.error("Lesing av query ressurs {} feilet", SINGLE_PERSON_QUERY, e);
            return null;
        }
    }

}
