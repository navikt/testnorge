package no.nav.testnav.libs.reactivecore.router;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static java.util.Objects.nonNull;

@Component
public class InternalHandler {

    private final String image;

    public InternalHandler(@Value("${NAIS_APP_IMAGE:null}") String image) {
        this.image = image;
    }

    public Mono<ServerResponse> isAlive(ServerRequest ignored) {
        return ServerResponse.ok().build();
    }

    public Mono<ServerResponse> isReady(ServerRequest ignored) {
        return ServerResponse.ok().build();
    }

    public Mono<ServerResponse> getVersion(ServerRequest ignored) {

        return ServerResponse
                .ok()
                .body(BodyInserters.fromValue(
                        JsonResponse.builder()
                                .image(image)
                                .commit(nonNull(image) && image.lastIndexOf("-") > 0 ?
                                        "https://github.com/navikt/testnorge/commit/" +
                                                image.substring(image.lastIndexOf("-") + 1) : null)
                                .build()));
    }

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    record JsonResponse(
            String image,
            String commit
    ) {
    }
}
