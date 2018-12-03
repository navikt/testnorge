package no.nav.dolly.domain.resultset.krrstub;

import java.time.ZonedDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DigitalKontaktdataRequest {

        private ZonedDateTime gyldigFra;
        private String personident;
        private boolean reservert;
        private String mobil;
        private String epost;
}