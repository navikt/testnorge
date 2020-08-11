package no.nav.dolly.bestilling.skjermingsregister.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SkjermingsDataRequest {

    private String etternavn;
    private String fornavn;
    private String personident;
    private LocalDateTime skjermetFra;
    private LocalDateTime skjermetTil;
}