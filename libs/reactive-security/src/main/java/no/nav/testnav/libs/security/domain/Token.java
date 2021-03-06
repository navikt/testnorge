package no.nav.testnav.libs.security.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Token {
    String value;
    String oid;
    boolean clientCredentials;
}
