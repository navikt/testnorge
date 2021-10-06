package no.nav.testnav.libs.reactivesecurity.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Token {
    String value;
    String userId;
    boolean clientCredentials;
}
