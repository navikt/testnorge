package no.nav.dolly.bestilling.skjermingsregister.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SkjermingDataRequest {

    private String etternavn;
    private String fornavn;
    private String personident;
    private LocalDateTime skjermetFra;
    private LocalDateTime skjermetTil;
}