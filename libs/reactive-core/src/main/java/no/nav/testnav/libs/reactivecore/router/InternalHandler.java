package no.nav.testnav.libs.reactivecore.router;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@SuppressWarnings("java:S1172")
@RequiredArgsConstructor
public class InternalHandler {

    private final Environment env;
    private JsonResponse json;

    public Mono<ServerResponse> isAlive(ServerRequest ignored) {
        return ServerResponse
                .ok()
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(body()));
    }

    public Mono<ServerResponse> isReady(ServerRequest ignored) {
        return ServerResponse
                .ok()
                .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromValue(body()));
    }

    private synchronized JsonResponse body() {
        if (json == null) {
            json = new JsonResponse("OK", null, null);
            var naisImage = env.getProperty("NAIS_APP_IMAGE");
            if (naisImage != null) {
                var i = naisImage.lastIndexOf("-");
                if (i > 0) {
                    json = new JsonResponse("OK", naisImage, "https://github.com/navikt/testnorge/commit/" + naisImage.substring(i + 1));
                }
            }
        }
        return json;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record JsonResponse(
            String status,
            String image,
            String commit
    ) {
    }

}
