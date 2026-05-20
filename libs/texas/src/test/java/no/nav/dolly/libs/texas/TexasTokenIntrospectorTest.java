package no.nav.dolly.libs.texas;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionException;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TexasTokenIntrospectorTest {

    private final Texas texas = mock(Texas.class);
    private final TexasTokenIntrospector introspector = new TexasTokenIntrospector(texas, new ObjectMapper());

    @Test
    void shouldParseIntrospectionResultIntoPlainAttributeTypes() {

        when(texas.introspect(anyString()))
                .thenReturn(Mono.just("""
                        {
                          "active": true,
                          "preferred_username": "user@nav.no",
                          "exp": 1779285182,
                          "groups": ["group-1", "group-2"]
                        }
                        """));

        var principal = introspector.introspect("token").block();

        assertThat(principal)
                .isNotNull();
        assertThat(principal.<Boolean>getAttribute("active"))
                .isTrue();
        assertThat(principal.<String>getAttribute("preferred_username"))
                .isEqualTo("user@nav.no");
        assertThat(principal.<Object>getAttribute("exp"))
                .isInstanceOf(Number.class);
        assertThat(principal.<List<String>>getAttribute("groups"))
                .isEqualTo(List.of("group-1", "group-2"));

    }

    @Test
    void shouldFailWhenTokenIsInactive() {

        when(texas.introspect(anyString()))
                .thenReturn(Mono.just("""
                        {
                          "active": false
                        }
                        """));

        assertThatThrownBy(() -> introspector.introspect("token").block())
                .isInstanceOf(OAuth2IntrospectionException.class)
                .hasMessageContaining("Token is not active");

    }

}


