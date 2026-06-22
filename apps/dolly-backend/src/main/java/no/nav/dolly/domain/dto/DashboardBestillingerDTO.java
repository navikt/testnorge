package no.nav.dolly.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardBestillingerDTO {

    private LocalDate dato;
    private Long bestillinger;
    private Long personerTotalt;
    private Long nye;
    private Long gjenopprettede;
    private Long navIdenter;
    private Long testnorgeIdenter;
}
