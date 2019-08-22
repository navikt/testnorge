package no.nav.dolly.domain.resultset.krrstub;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DigitalKontaktdata {

        private Long id;

        private ZonedDateTime gyldigFra;
        private String personident;
        private boolean reservert;
        private String mobil;
        private String epost;

        private ZonedDateTime epostOppdatert;
        private ZonedDateTime epostVerifisert;
        private ZonedDateTime mobilOppdatert;
        private ZonedDateTime mobilVerifisert;
}