package no.nav.dolly.mapper;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.List;
import org.junit.Test;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusMiljoeIdentForhold;

public class BestillingAaregStatusMapperTest {

    private static final String IDENT = "111111111";

    @Test
    public void buildAaregStatusMap() {
        BestillingProgress progress = BestillingProgress.builder()
                .aaregStatus("t0: arbforhold=1$Feil& OpprettArbeidsforholdSikkerhetsbegrensning -> Bruker har ikke rettighet til å opprette denne personen.,"
                        + "t6: arbforhold=1$OK,"
                        + "t0: arbforhold=2$Feil& OpprettArbeidsforholdSikkerhetsbegrensning -> Bruker har ikke rettighet til å opprette denne personen.,"
                        + "t6: arbforhold=2$OK")
                .ident(IDENT)
                .build();

        List<RsStatusMiljoeIdentForhold> aaregStatus = BestillingAaregStatusMapper.buildAaregStatusMap(singletonList(progress));

        assertThat(aaregStatus.get(0).getStatusMelding(), is(equalTo("Feil, OpprettArbeidsforholdSikkerhetsbegrensning -> Bruker har ikke rettighet til å opprette denne personen.")));
        assertThat(aaregStatus.get(0).getEnvironmentIdentsForhold().get("t0").get(IDENT), containsInAnyOrder("arbforhold: 1", "arbforhold: 2"));
        assertThat(aaregStatus.get(1).getStatusMelding(), is(equalTo("OK")));
        assertThat(aaregStatus.get(1).getEnvironmentIdentsForhold().get("t6").get(IDENT), containsInAnyOrder("arbforhold: 1", "arbforhold: 2"));
    }
}