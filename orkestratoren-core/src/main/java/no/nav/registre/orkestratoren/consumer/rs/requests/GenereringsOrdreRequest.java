package no.nav.registre.orkestratoren.consumer.rs.requests;

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
    private String miljoe;
    private Map<String, Integer> antallMeldingerPerAarsakskode;
}