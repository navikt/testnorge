package no.nav.dolly.bestilling.sykemelding.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.sykemelding.RsDetaljertSykemelding.Diagnose;
import no.nav.dolly.domain.resultset.sykemelding.RsDetaljertSykemelding.Pasient;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetaljertSykemeldingTransaksjon {

    private Diagnose hovedDiagnose;
    private Pasient pasient;
    private LocalDateTime startDato;
}
