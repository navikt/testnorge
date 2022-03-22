package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.pdl.GetPdlPersonCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.pdl.TagsOpprettingCommand;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential.PdlProxyProperties;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pdl.PdlPerson;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.FilLaster;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.Tags;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static com.google.common.base.Strings.isNullOrEmpty;

@Slf4j
@Service
public class PdlProxyConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serviceProperties;

    private static final String SINGLE_PERSON_QUERY = "pdlperson/pdlquery.graphql";
    private static final List<Tags> SYNT_TAGS = Collections.singletonList(Tags.ARENASYNT);

    public PdlProxyConsumer(
            PdlProxyProperties serviceProperties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder().baseUrl(serviceProperties.getUrl()).build();
        this.tokenExchange = tokenExchange;
    }

    public PdlPerson getPdlPerson(String ident) {
        if (isNullOrEmpty(ident)) return null;
        var query = getSinglePersonQueryFromFile();
        try {
            return tokenExchange.exchange(serviceProperties)
                    .flatMap(accessToken -> new GetPdlPersonCommand(ident, query, accessToken.getTokenValue(), webClient).call())
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke hente pdlperson.", e);
            return null;
        }

    }

    private static String getSinglePersonQueryFromFile() {
        try (var reader = new BufferedReader(new InputStreamReader(FilLaster.instans().lastRessurs(SINGLE_PERSON_QUERY), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            log.error("Lesing av query ressurs {} feilet", SINGLE_PERSON_QUERY, e);
            return null;
        }
    }

    public String createSyntTags(List<String> identer) {
        try {
            if (isNull(identer) || identer.isEmpty()) return null;
            return tokenExchange.exchange(serviceProperties)
                    .flatMap(accessToken -> new TagsOpprettingCommand(webClient, identer, SYNT_TAGS, accessToken.getTokenValue()).call())
                    .block();
        } catch (Exception e) {
            log.error("Feil i opprettelse av tag(s) på ident(er).", e);
            return null;
        }
    }

}