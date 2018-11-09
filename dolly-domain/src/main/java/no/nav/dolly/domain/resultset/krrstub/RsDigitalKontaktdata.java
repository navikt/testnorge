package no.nav.dolly.domain.resultset.krrstub;

import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RsDigitalKontaktdata {

        private ZonedDateTime gyldigFra;
        private String ident;
        private String reservert;
        private String mobil;
        private String epost;
}