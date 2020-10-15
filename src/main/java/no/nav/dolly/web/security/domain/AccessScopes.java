package no.nav.dolly.web.security.domain;

import lombok.Value;

import java.util.Collections;
import java.util.List;

@Value
public class AccessScopes {

    public AccessScopes(String scope) {
        this.scopes = Collections.singletonList("api://" + scope + "/.default");
    }

    List<String> scopes;
}
