package no.nav.dolly.bestilling.nom.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NomRessursRequest {

    private String personident;
    private String etternavn;
    private String fornavn;
    private String mellomnavn;
    private LocalDate startDato;
    private LocalDate sluttDato;
}