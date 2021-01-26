package no.nav.registre.testnorge.libs.oauth2.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class RemoteServiceProperties implements Scopeable {
    private String url;
    private String clientId;

    @Override
    public String toScope() {
        return "api://" + clientId + "/.default";
    }
}