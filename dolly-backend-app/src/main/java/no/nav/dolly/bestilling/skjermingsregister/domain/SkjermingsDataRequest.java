package no.nav.dolly.bestilling.skjermingsregister.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SkjermingsDataRequest {

    private String personident;
    private String fornavn;
    private String etternavn;
    private LocalDateTime skjermetFra;
    private LocalDateTime skjermetTil;
}