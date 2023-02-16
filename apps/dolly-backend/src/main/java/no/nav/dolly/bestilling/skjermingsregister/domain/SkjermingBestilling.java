package no.nav.dolly.bestilling.skjermingsregister.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkjermingBestilling {

    private LocalDateTime skjermetFra;
    private LocalDateTime skjermetTil;
}
