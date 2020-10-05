package no.nav.dolly.domain.resultset.krrstub;

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