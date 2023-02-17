package no.nav.dolly.domain.resultset.krrstub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsDigitalKontaktdata {

    private LocalDateTime gyldigFra;
    private boolean reservert;
    private String mobil;
    private String epost;
    private boolean registrert;
    private String sdpAdresse;
    private Integer sdpLeverandoer;
    private String spraak;
}