package no.nav.testnav.libs.security.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class AccessScopes {
    final private List<String> scopes;

    public AccessScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public AccessScopes(String... scopes) {
        this.scopes = Arrays.asList(scopes);
    }

    public AccessScopes(Scopeable scopeable) {
        this.scopes = Collections.singletonList(scopeable.toScope());
    }

    public List<String> getScopes() {
        return scopes;
    }
}
