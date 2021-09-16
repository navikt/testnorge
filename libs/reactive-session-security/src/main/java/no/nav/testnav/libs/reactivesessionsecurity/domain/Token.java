package no.nav.testnav.libs.reactivesessionsecurity.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Token {
    String value;
}
