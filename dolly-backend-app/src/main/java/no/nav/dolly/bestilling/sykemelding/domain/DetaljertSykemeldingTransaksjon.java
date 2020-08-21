package no.nav.dolly.bestilling.sykemelding.domain;

import static no.nav.dolly.domain.resultset.sykemelding.RsSykemelding.RsDetaljertSykemelding.DollyDiagnose;
import static no.nav.dolly.domain.resultset.sykemelding.RsSykemelding.RsDetaljertSykemelding.Pasient;

import java.time.LocalDateTime;

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
public class DetaljertSykemeldingTransaksjon {

    private DollyDiagnose hovedDiagnose;
    private Pasient pasient;
    private LocalDateTime startDato;
}
