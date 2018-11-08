package no.nav.registre.orkestratoren.consumer.rs.requests;

import java.util.Map;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenereringsOrdreRequest {

    @NotNull
    private Long gruppeId;
    @NotNull
    private String miljoe;
    @NotNull
    private Map<String, Integer> antallMeldingerPerEndringskode;
}