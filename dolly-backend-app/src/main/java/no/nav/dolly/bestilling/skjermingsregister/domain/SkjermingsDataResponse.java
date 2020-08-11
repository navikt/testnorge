package no.nav.dolly.bestilling.skjermingsregister.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkjermingsDataResponse {

    private String endretDato;
    private String etternavn;
    private String fornavn;
    private String opprettetDato;
    private String personident;
    private String skjermetFra;
    private String skjermetTil;
}
