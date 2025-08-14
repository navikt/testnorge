package no.nav.dolly.domain.resultset.krrstub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsDigitalKontaktdata {

    private LocalDate gyldigFra;
    private boolean reservert;
    private String mobil;
    private String landkode;
    private String land;
    private String epost;
    private boolean registrert;
    private String sdpAdresse;
    private Integer sdpLeverandoer;
    private String spraak;
}