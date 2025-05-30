package no.nav.dolly.domain.resultset.krrstub;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DigitalKontaktdata {

    private Long id;

    private ZonedDateTime gyldigFra;
    private String personident;
    private boolean reservert;
    private boolean registrert;
    private String mobil;
    private String epost;
    private String sdpAdresse;
    private Integer sdpLeverandoer;
    private String spraak;

    private ZonedDateTime epostOppdatert;
    private ZonedDateTime epostVerifisert;
    private ZonedDateTime mobilOppdatert;
    private ZonedDateTime mobilVerifisert;
    private ZonedDateTime spraakOppdatert;
    private ZonedDateTime reservertOppdatert;
}