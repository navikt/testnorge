package no.nav.dolly.bestilling.instdata.domain;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.inst.Instdata;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstdataResponse {

    private HttpStatus status;
    private String personident;
    private Instdata institusjonsopphold;
    private String feilmelding;
}
