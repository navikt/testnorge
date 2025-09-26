package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AlderspensjonRequest implements PensjonTransaksjonId {

    private Set<String> miljoer;

    private String fnr;
    private LocalDate iverksettelsesdato;
    private Integer uttaksgrad;

    public Set<String> getMiljoer() {
        if (isNull(miljoer)) {
            miljoer = new HashSet<>();
        }
        return miljoer;
    }
}
