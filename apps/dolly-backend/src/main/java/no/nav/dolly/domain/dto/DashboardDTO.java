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
public class DashboardDTO {

    private LocalDate dato;
    private Long personer;
    private Long nyePersoner;
    private Long gjenopprettinger;
    private Long pdlFeil;
    private Long aaregFeil;
    private Long penFeil;
}
