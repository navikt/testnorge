package no.nav.registre.hodejegeren.provider.rs.requests;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenereringsOrdreRequest {

    private Long gruppeId;
    private String eksisterendeIdenterIMiljoe;
    private Map<String, String> antallMeldingerPerAarsakskode;
}
