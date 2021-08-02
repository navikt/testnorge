package no.nav.registre.testnorge.arena.consumer.rs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.command.pdl.GetPdlPersonCommand;
import no.nav.registre.testnorge.arena.consumer.rs.response.pdl.PdlPerson;

import no.nav.registre.testnorge.arena.domain.FilLaster;
import no.nav.testnav.libs.securitytokenservice.StsOidcTokenService;

@Slf4j
@Service
public class PdlPersonConsumer {

    private final WebClient webClient;
    private final StsOidcTokenService tokenService;

    private static final String SINGLE_PERSON_QUERY = "pdlperson/pdlquery.graphql";

    public PdlPersonConsumer(
            @Value("${pdl.rest-api.url}") String pdlApiUrl,
            StsOidcTokenService tokenService
    ) {
        this.tokenService = tokenService;
        this.webClient = WebClient.builder()
                .baseUrl(pdlApiUrl)
                .build();
    }

    public PdlPerson getPdlPerson(String ident) {
        var query = getSinglePersonQueryFromFile();
        return new GetPdlPersonCommand(ident, query, tokenService.getToken(), webClient).call();
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
