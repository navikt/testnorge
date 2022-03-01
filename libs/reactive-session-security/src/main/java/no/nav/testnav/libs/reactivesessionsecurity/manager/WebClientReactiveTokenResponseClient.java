package no.nav.testnav.libs.reactivesessionsecurity.manager;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.endpoint.NimbusJwtClientAuthenticationParametersConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.ReactiveOAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.endpoint.PkceParameterNames;
import org.springframework.security.oauth2.core.web.reactive.function.OAuth2BodyExtractors;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Set;

class WebClientReactiveTokenResponseClient implements ReactiveOAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private WebClient webClient = WebClient.builder().build();
    private NimbusJwtClientAuthenticationParametersConverter<OAuth2AuthorizationCodeGrantRequest> converter;

    @Override
    public Mono<OAuth2AccessTokenResponse> getTokenResponse(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        Assert.notNull(grantRequest, "grantRequest cannot be null");
        return Mono.defer(() -> this.webClient.post()
                .uri(clientRegistration(grantRequest).getProviderDetails().getTokenUri())
                .headers((headers) -> populateTokenRequestHeaders(grantRequest, headers))
                .body(createTokenRequestBody(grantRequest))
                .exchangeToMono(clientResponse -> readTokenResponse(grantRequest, clientResponse))
        );
    }

    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public void setConverter(NimbusJwtClientAuthenticationParametersConverter<OAuth2AuthorizationCodeGrantRequest> converter) {
        this.converter = converter;
    }

    private ClientRegistration clientRegistration(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        return grantRequest.getClientRegistration();
    }

    private Set<String> defaultScopes(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        return grantRequest.getAuthorizationExchange().getAuthorizationRequest().getScopes();
    }

    private BodyInserters.FormInserter<String> populateTokenRequestBody(
            OAuth2AuthorizationCodeGrantRequest grantRequest,
            BodyInserters.FormInserter<String> body
    ) {

        ClientRegistration clientRegistration = clientRegistration(grantRequest);
        if (!ClientAuthenticationMethod.CLIENT_SECRET_BASIC.equals(clientRegistration.getClientAuthenticationMethod())) {
            body.with(OAuth2ParameterNames.CLIENT_ID, clientRegistration.getClientId());
        }
        if (ClientAuthenticationMethod.CLIENT_SECRET_POST.equals(clientRegistration.getClientAuthenticationMethod())) {
            body.with(OAuth2ParameterNames.CLIENT_SECRET, clientRegistration.getClientSecret());
        }

        OAuth2AuthorizationExchange authorizationExchange = grantRequest.getAuthorizationExchange();
        OAuth2AuthorizationResponse authorizationResponse = authorizationExchange.getAuthorizationResponse();
        body.with(OAuth2ParameterNames.CODE, authorizationResponse.getCode());
        String redirectUri = authorizationExchange.getAuthorizationRequest().getRedirectUri();
        if (redirectUri != null) {
            body.with(OAuth2ParameterNames.REDIRECT_URI, redirectUri);
        }
        String codeVerifier = authorizationExchange.getAuthorizationRequest()
                .getAttribute(PkceParameterNames.CODE_VERIFIER);
        if (codeVerifier != null) {
            body.with(PkceParameterNames.CODE_VERIFIER, codeVerifier);
        }
        if (grantRequest.getClientRegistration().getClientAuthenticationMethod().equals(ClientAuthenticationMethod.PRIVATE_KEY_JWT)) {
            Assert.notNull(converter, "converter cannot be null");
            var map = converter.convert(grantRequest);
            map.forEach((key, value) -> value.stream().findFirst().ifPresent(first -> body.with(key, first)));
        }
        return body;
    }

    private void populateTokenRequestHeaders(OAuth2AuthorizationCodeGrantRequest grantRequest, HttpHeaders headers) {
        ClientRegistration clientRegistration = clientRegistration(grantRequest);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.equals(clientRegistration.getClientAuthenticationMethod())) {
            headers.setBasicAuth(clientRegistration.getClientId(), clientRegistration.getClientSecret());
        }
    }

    private BodyInserters.FormInserter<String> createTokenRequestBody(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        BodyInserters.FormInserter<String> body = BodyInserters.fromFormData(OAuth2ParameterNames.GRANT_TYPE,
                grantRequest.getGrantType().getValue());
        return populateTokenRequestBody(grantRequest, body);
    }

    private Mono<OAuth2AccessTokenResponse> readTokenResponse(OAuth2AuthorizationCodeGrantRequest grantRequest, ClientResponse response) {
        return response.body(OAuth2BodyExtractors.oauth2AccessTokenResponse())
                .map((tokenResponse) -> populateTokenResponse(grantRequest, tokenResponse));
    }

    private OAuth2AccessTokenResponse populateTokenResponse(OAuth2AuthorizationCodeGrantRequest grantRequest, OAuth2AccessTokenResponse tokenResponse) {
        if (CollectionUtils.isEmpty(tokenResponse.getAccessToken().getScopes())) {
            Set<String> defaultScopes = defaultScopes(grantRequest);
            tokenResponse = OAuth2AccessTokenResponse
                    .withResponse(tokenResponse)
                    .scopes(defaultScopes)
                    .build();
        }
        return tokenResponse;
    }
}
