package no.nav.dolly.domain.resultset.krrstub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsDigitalKontaktdata {
    private LocalDateTime gyldigFra;
    private boolean reservert;
    private String mobil;
    private String epost;
    private boolean registrert;
}