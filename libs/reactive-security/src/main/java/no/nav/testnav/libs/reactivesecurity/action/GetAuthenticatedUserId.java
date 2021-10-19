package no.nav.testnav.libs.reactivesecurity.action;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Component
@RequiredArgsConstructor
public class GetAuthenticatedUserId extends JwtResolver implements Callable<Mono<String>> {

    private final GetAuthenticatedResourceServerType getAuthenticatedResourceServerType;

    @Override
    public Mono<String> call() {
        return getAuthenticatedResourceServerType
                .call()
                .flatMap(serverType -> switch (serverType) {
                    case TOKEN_X -> getTokenAttribute("pid");
                    case AZURE_AD -> getTokenAttribute("oid");
                });
    }

    private Mono<String> getTokenAttribute(String attribute) {
        return getJwtAuthenticationToken().map(value -> value.getTokenAttributes().get(attribute).toString());
    }

}
