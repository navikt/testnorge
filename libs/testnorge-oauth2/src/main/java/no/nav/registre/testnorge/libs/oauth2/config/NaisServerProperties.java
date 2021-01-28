package no.nav.registre.testnorge.libs.oauth2.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaisServerProperties implements Scopeable {
    private String url;
    private String cluster;
    private String name;
    private String namespace;
    private String clientId;

    @Override
    public String toScope() {
        return "api://" + clientId + "/.default";
    }
}
