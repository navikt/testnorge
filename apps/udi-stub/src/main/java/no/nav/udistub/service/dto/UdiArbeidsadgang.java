package no.nav.udistub.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.udi.mt_1067_nav_data.v1.ArbeidOmfangKategori;
import no.udi.mt_1067_nav_data.v1.ArbeidsadgangType;
import no.udi.mt_1067_nav_data.v1.JaNeiUavklart;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class UdiArbeidsadgang {

    private JaNeiUavklart harArbeidsAdgang;
    private ArbeidsadgangType typeArbeidsadgang;
    private ArbeidOmfangKategori arbeidsOmfang;
    private UdiPeriode periode;
    private String forklaring;
    private String hjemmel;
}
