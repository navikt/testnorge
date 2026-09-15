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
public class AdferdFragment {

    private Long id;
    private String bestkriterier;
    private LocalDate dato;
    private Integer antall;
}
