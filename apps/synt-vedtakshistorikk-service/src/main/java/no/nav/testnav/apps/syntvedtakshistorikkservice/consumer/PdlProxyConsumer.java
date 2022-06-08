package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.pdl.GetPdlPersonCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.pdl.GetPdlPersonerCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.pdl.TagsOpprettingCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.pdl.TagsSlettingCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential.PdlProxyProperties;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pdl.PdlPerson;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pdl.PdlPersonBolk;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.FilLaster;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.Tags;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class PdlProxyConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;

    private static final String SINGLE_PERSON_QUERY = "pdlperson/pdlquery.graphql";
    private static final String BOLK_PERSON_QUERY = "pdlperson/pdlbolkquery.graphql";

    public PdlProxyConsumer(
            PdlProxyProperties serviceProperties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
        this.tokenExchange = tokenExchange;
    }

    public PdlPerson getPdlPerson(String ident) {
        if (isNullOrEmpty(ident)) return null;
        try {
            var query = getQueryFromFile(SINGLE_PERSON_QUERY);
            var response = tokenExchange.exchange(serviceProperties)
                    .flatMap(accessToken -> new GetPdlPersonCommand(ident, query, accessToken.getTokenValue(), webClient).call())
                    .block();
            if (nonNull(response) && nonNull(response.getErrors()) && !response.getErrors().isEmpty()) {
                log.error("Klarte ikke hente pdlperson: " + response.getErrors().get(0).getMessage());
                return null;
            }
            return response;
        } catch (Exception e) {
            log.error("Klarte ikke hente pdlperson.", e);
            return null;
        }

    }

    public PdlPersonBolk getPdlPersoner(List<String> identer) {
        if (isNull(identer) || identer.isEmpty()) return null;
        try {
            var query = getQueryFromFile(BOLK_PERSON_QUERY);
            return tokenExchange.exchange(serviceProperties)
                    .flatMap(accessToken -> new GetPdlPersonerCommand(identer, query, accessToken.getTokenValue(), webClient).call())
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke hente pdlpersoner.", e);
            return null;
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

    public boolean createTags(List<String> identer, List<Tags> tags) {
        try {
            if (isNull(identer) || identer.isEmpty()) return false;
            var response = tokenExchange.exchange(serviceProperties)
                    .flatMap(accessToken -> new TagsOpprettingCommand(webClient, identer, tags, accessToken.getTokenValue()).call())
                    .block();

            if (isNull(response) || !response.getStatusCode().is2xxSuccessful()) {
                var status = isNull(response) ? "" : "Status: " + response.getStatusCode();
                log.error("Feil i opprettelse av tag(s) på ident(er).{}", status);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Feil i opprettelse av tag(s) på ident(er).", e);
            return false;
        }
    }

    public boolean deleteTags(List<String> identer, List<Tags> tags){
        try {
            if (isNull(identer) || identer.isEmpty()) return false;
            var response =  tokenExchange.exchange(serviceProperties)
                    .flatMap(accessToken -> new TagsSlettingCommand(webClient, identer, tags, accessToken.getTokenValue()).call())
                    .block();
            if (isNull(response) || !response.getStatusCode().is2xxSuccessful()) {
                var status = isNull(response) ? "" : "Status: " + response.getStatusCode();
                log.error("Feil i opprettelse av tag(s) på ident(er).{}", status);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Feil i sletting av tag(s) på ident(er).", e);
            return false;
        }
    }
}