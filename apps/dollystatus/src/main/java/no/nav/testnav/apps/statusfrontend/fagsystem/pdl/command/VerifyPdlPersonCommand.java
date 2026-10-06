package no.nav.testnav.apps.statusfrontend.fagsystem.pdl.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class VerifyPdlPersonCommand implements Callable<Mono<Boolean>> {

    private static final String QUERY = """
            query($ident: ID!) {
              hentPerson(ident: $ident) {
                navn {
                  metadata {
                    opplysningsId
                  }
                }
              }
              hentIdenter(ident: $ident, historikk: false, grupper: [AKTORID, FOLKEREGISTERIDENT, NPID]) {
                identer {
                  ident
                  historisk
                }
              }
            }
            """;

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final FunctionalTestEnvironment environment;
    private final RunId runId;
    private final Duration timeout;

    @Override
    public Mono<Boolean> call() {
        return webClient.post()
                .uri(environment == FunctionalTestEnvironment.Q1
                        ? "/pdl-api-q1/graphql"
                        : "/pdl-api/graphql")
                .headers(headers -> headers.setBearerAuth(token))
                .header("Tema", "GEN")
                .header("Nav-Consumer-Id", "Dollystatus")
                .header("Nav-Call-Id", "Dollystatus-" + runId.value())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new GraphQlRequest(QUERY, Map.of("ident", ident)))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::isVerified)
                .timeout(timeout);
    }

    private boolean isVerified(JsonNode response) {
        var errors = response.path("errors");
        if (errors.isArray() && !errors.isEmpty()) {
            return false;
        }
        var data = response.path("data");
        if (data.path("hentPerson").isMissingNode() || data.path("hentPerson").isNull()) {
            return false;
        }
        var identifiers = data.path("hentIdenter").path("identer");
        if (!identifiers.isArray()) {
            return false;
        }
        for (var identifier : identifiers) {
            if (ident.equals(identifier.path("ident").asString())
                    && !identifier.path("historisk").asBoolean(true)) {
                return true;
            }
        }
        return false;
    }

    private record GraphQlRequest(String query, Map<String, String> variables) {
    }
}
