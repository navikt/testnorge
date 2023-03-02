package no.nav.testnav.libs.reactivesecurity.exchange.ideporten;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.domain.ResourceServerType;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenService;
import no.nav.testnav.libs.securitycore.command.idporten.OnBehalfOfExchangeCommand;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.securitycore.domain.idporten.IdportenProperties;
import no.nav.testnav.libs.securitycore.domain.tokenx.TokenXProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Properties;

@Slf4j
@Service
@ConditionalOnProperty("spring.security.oauth2.resourceserver.tokenx.issuer-uri")
public class IdportenService implements TokenService {
    private final GetAuthenticatedToken getAuthenticatedTokenAction;
    private final WebClient webClient;
    private final IdportenProperties idportenProperties;

    IdportenService(IdportenProperties idportenProperties, GetAuthenticatedToken tokenResolver) {
        log.info("Init Idporten token exchange.");
        this.webClient = WebClient.builder().build();
        this.idportenProperties = idportenProperties;
        this.getAuthenticatedTokenAction = tokenResolver;
    }

    @Override
    public Mono<AccessToken> exchange(ServerProperties serverProperties) {
        return getAuthenticatedTokenAction.call()
                .flatMap(token -> new OnBehalfOfExchangeCommand(webClient, idportenProperties, serverProperties.toTokenXScope(), token).call());
    }

    @Override
    public ResourceServerType getType() {
        return ResourceServerType.TOKEN_X;
    }
}
