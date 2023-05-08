package no.nav.testnav.libs.reactivecore.router;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@SuppressWarnings("java:S1172")
@RequiredArgsConstructor
public class InternalHandler {

    private final Environment env;
    private String html;

    public Mono<ServerResponse> isAlive(ServerRequest ignored) {
        return ServerResponse.ok().body(BodyInserters.fromValue(body()));
    }
    public Mono<ServerResponse> isReady(ServerRequest ignored) {
        return ServerResponse.ok().body(BodyInserters.fromValue(body()));
    }

    private synchronized String body() {
        if (html == null) {
            html = "OK";
            var naisAppImage = env.getProperty("NAIS_APP_IMAGE");
            if (naisAppImage != null) {
                var i = naisAppImage.lastIndexOf("-");
                if (i > 0) {
                    var hash = naisAppImage.substring(i + 1);
                    html = "OK - image is <a href=https://github.com/navikt/testnorge/commit/%s>%s</a>".formatted(hash, naisAppImage);
                }
            }
        }
        return html;
    }

}
