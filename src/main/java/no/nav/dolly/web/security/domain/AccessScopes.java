package no.nav.dolly.web.security.domain;

import lombok.Value;

import java.util.Collections;
import java.util.List;

import no.nav.dolly.web.config.credentials.Scopeable;

@Value
public class AccessScopes {

    public AccessScopes(String scope) {
        this.scopes = Collections.singletonList("api://" + scope + "/.default");
    }

    public AccessScopes(Scopeable scopeable) {
        this.scopes = Collections.singletonList(scopeable.toScope());
    }

    List<String> scopes;
}
