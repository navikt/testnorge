package no.nav.dolly.bestilling.bistandsbehov.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BistandVedtakRequestDTO {

    private String fnr;
    private String innsatsgruppe;
    private String hovedmal;
    private LocalDateTime vedtakFattet;
    private String oppfolgingsEnhet;
    private String begrunnelse;
    private String veilederIdent;
}
