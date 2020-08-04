package no.nav.dolly.bestilling.skjermingsregister.domain;

import java.time.LocalDateTime;

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
public class SkjermingsDataTransaksjon {

    private String heltNavn;
    private LocalDateTime skjermetFra;
}