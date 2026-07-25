package no.nav.dolly.domain.resultset.bistandsbehov;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsBistandsbehovDTO {

    private Innsatsgruppe innsatsgruppe;
    private Hovedmal hovedmal;
    private LocalDateTime vedtakFattet;
    private String oppfolgingsEnhet;
    private String begrunnelse;
    private String veilederIdent;

    public enum Innsatsgruppe {
        GODE_MULIGHETER,
        TRENGER_VEILEDNING,
        TRENGER_VEILEDNING_NEDSATT_ARBEIDSEVNE,
        JOBBE_DELVIS,
        LITEN_MULIGHET_TIL_A_JOBBE
    }

    public enum Hovedmal {
        SKAFFE_ARBEID,
        BEHOLDE_ARBEID
    }
}
