package no.nav.dolly.bestilling.nom.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NomRessursRequest {

    private String personident;
    private String fornavn;
    private String etternavn;
}
