package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlderspensjonNyUtaksgradRequest implements PensjonTransaksjonId {

    private String fnr;
    private Integer nyUttaksgrad;
    private LocalDate fom;
    private String saksbehandler;
    private String attesterer;
    private String navEnhetId;
    private Set<String> miljoer;
}