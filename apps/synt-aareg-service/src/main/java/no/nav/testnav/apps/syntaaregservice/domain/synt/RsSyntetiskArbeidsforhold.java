package no.nav.testnav.apps.syntaaregservice.domain.synt;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import no.nav.testnav.apps.syntaaregservice.domain.aareg.RsAktoer;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsSyntetiskArbeidsforhold {

    private RsSyntetiskPeriode ansettelsesPeriode;
    private List<RsSyntetiskAntallTimerForTimeloennet> antallTimerForTimeloennet;
    private RsSyntetiskArbeidsavtale arbeidsavtale;
    private String arbeidsforholdID;
    private Long arbeidsforholdIDnav;
    private String arbeidsforholdstype;
    private RsAktoer arbeidsgiver;
    private RsSyntPerson arbeidstaker;
    private List<RsSyntetiskPermisjon> permisjon;
    private List<RsSyntetiskUtenlandsopphold> utenlandsopphold;
}
