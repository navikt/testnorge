package no.nav.dolly.web.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SessionConfigTest {

    @Test
    void shouldSaveSessionRightAfterRefreshedClientIsStored() {
        var repository = mock(ServerOAuth2AuthorizedClientRepository.class);
        var session = mock(WebSession.class);
        when(session.getAttributes()).thenReturn(new HashMap<>());
        when(session.save()).thenReturn(Mono.empty());
        when(repository.saveAuthorizedClient(any(), any(), any())).thenReturn(Mono.empty());
        var exchange = MockServerWebExchange.builder(MockServerHttpRequest.get("/dolly-backend/api/v1/bruker"))
                .session(session)
                .build();
        var client = mock(OAuth2AuthorizedClient.class);
        var principal = new TestingAuthenticationToken("bruker", null);

        SessionConfig.saveAndPersistSession(repository)
                .onAuthorizationSuccess(client, principal, Map.of(ServerWebExchange.class.getName(), exchange))
                .block();

        var order = inOrder(repository, session);
        order.verify(repository).saveAuthorizedClient(client, principal, exchange);
        order.verify(session).save();
    }
}
