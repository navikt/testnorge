package no.nav.registre.syntrest.kubernetes;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QueryObject {
    private String query;
}
