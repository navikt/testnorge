package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AlderspensjonRequest {

    private List<String> miljoer;

    private String fnr;
    private LocalDate iverksettelsesdato;
    private Integer uttaksgrad;

    public List<String> getMiljoer() {
        if (isNull(miljoer)) {
            miljoer = new ArrayList<>();
        }
        return miljoer;
    }
}
