package no.nav.testnav.mocks.azuremock.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Arguments {
    private String audience;
    private String assertion;
    private String oid;
    private String sub;
}
