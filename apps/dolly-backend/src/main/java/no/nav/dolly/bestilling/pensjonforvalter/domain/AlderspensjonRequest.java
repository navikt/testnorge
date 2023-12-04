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
public class AlderspensjonRequest {

    private List<String> miljoer;
    private String fnr;

    private LocalDate kravFremsattDato;
    private LocalDate iverksettelsesdato;
    private Integer uttaksgrad;
    private String saksbehandler;
    private String attesterer;
    private String navEnhetId;

    public List<String> getMiljoer() {

        if (isNull(miljoer)) {
            miljoer = new ArrayList<>();
        }
        return miljoer;
    }
}
