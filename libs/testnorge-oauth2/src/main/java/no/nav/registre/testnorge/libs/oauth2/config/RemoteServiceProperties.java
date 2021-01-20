package no.nav.registre.testnorge.libs.oauth2.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public abstract class RemoteServiceProperties {
    private String url;
    private String clientId;
}
