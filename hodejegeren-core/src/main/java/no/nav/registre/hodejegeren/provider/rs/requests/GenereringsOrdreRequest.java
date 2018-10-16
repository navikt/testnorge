package no.nav.registre.hodejegeren.provider.rs.requests;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenereringsOrdreRequest {

    private Long gruppeId;
    private String eksisterendeIdenterIMiljoe;
    private Map<String, String> antallMeldingerPerAarsakskode;
}
