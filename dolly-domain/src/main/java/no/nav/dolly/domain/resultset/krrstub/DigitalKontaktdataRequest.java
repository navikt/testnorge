package no.nav.dolly.domain.resultset.krrstub;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigitalKontaktdataRequest {

        private ZonedDateTime gyldigFra;
        private String personident;
        private boolean reservert;
        private String mobil;
        private String epost;
}