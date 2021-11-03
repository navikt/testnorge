package no.nav.dolly.mapper;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.List;
import org.junit.jupiter.api.Test;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

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

        List<RsStatusRapport> aaregStatus = BestillingAaregStatusMapper.buildAaregStatusMap(singletonList(progress));

        assertThat(aaregStatus.get(0).getStatuser().get(0).getMelding(), is(equalTo("arbforhold=2: Feil, OpprettArbeidsforholdSikkerhetsbegrensning -> Bruker har ikke rettighet til å opprette denne personen.")));
        assertThat(aaregStatus.get(0).getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("t0")));
        assertThat(aaregStatus.get(0).getStatuser().get(0).getDetaljert().get(0).getIdenter(), contains(IDENT));
        assertThat(aaregStatus.get(0).getStatuser().get(1).getMelding(), is(equalTo("arbforhold=1: Feil, OpprettArbeidsforholdSikkerhetsbegrensning -> Bruker har ikke rettighet til å opprette denne personen.")));
        assertThat(aaregStatus.get(0).getStatuser().get(1).getDetaljert().get(0).getMiljo(), is(equalTo("t0")));
        assertThat(aaregStatus.get(0).getStatuser().get(1).getDetaljert().get(0).getIdenter(), contains(IDENT));
        assertThat(aaregStatus.get(0).getStatuser().get(2).getMelding(), is(equalTo("OK")));
        assertThat(aaregStatus.get(0).getStatuser().get(2).getDetaljert().get(0).getMiljo(), is(equalTo("t6")));
        assertThat(aaregStatus.get(0).getStatuser().get(2).getDetaljert().get(0).getIdenter(), contains(IDENT));
    }
}