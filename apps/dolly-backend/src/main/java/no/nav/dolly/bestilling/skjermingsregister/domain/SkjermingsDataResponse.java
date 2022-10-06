package no.nav.dolly.bestilling.skjermingsregister.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

    @JsonIgnore
    private boolean eksistererIkke;
}
