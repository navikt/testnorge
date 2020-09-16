package no.nav.registre.testnorge.libs.oauth2.domain;

import java.util.Arrays;
import java.util.List;

public class AccessScopes {
    final private List<String> scopes;

    public AccessScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public AccessScopes(String... scopes) {
        this.scopes = Arrays.asList(scopes);
    }

    public List<String> getScopes() {
        return scopes;
    }
}
