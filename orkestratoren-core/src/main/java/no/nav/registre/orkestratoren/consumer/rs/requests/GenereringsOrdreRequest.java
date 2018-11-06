package no.nav.registre.orkestratoren.consumer.rs.requests;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

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