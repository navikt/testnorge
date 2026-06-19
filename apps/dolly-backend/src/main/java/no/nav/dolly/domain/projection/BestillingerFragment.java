package no.nav.dolly.domain.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BestillingerFragment {

    private LocalDate dato;
    private Long personer;
    private String gjenopprettstatus;
}
