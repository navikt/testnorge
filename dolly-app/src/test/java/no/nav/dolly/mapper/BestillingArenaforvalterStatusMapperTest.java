package no.nav.dolly.mapper;

import static org.assertj.core.util.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsMeldingStatusIdent;

@RunWith(MockitoJUnitRunner.class)
public class BestillingArenaforvalterStatusMapperTest {

    private static final List<BestillingProgress> RUN_STATUS = newArrayList(
            BestillingProgress.builder().ident("IDENT_1")
                    .arenaforvalterStatus("HentBruker&status: OK$OpprettNyBruker&status: OK")
                    .build()
    );

    @Test
    public void buildArenaForvalterStatusMap_OK() {

        List<RsMeldingStatusIdent> identStatuses = BestillingArenaforvalterStatusMapper.buildArenaStatusMap(RUN_STATUS);

        assertThat(identStatuses.get(0).getMelding(), is(equalTo("OpprettNyBruker")));
        assertThat(identStatuses.get(0).getStatusIdent().get("status: OK"), containsInAnyOrder("IDENT_1"));

        assertThat(identStatuses.get(1).getMelding(), is(equalTo("HentBruker")));
        assertThat(identStatuses.get(1).getStatusIdent().get("status: OK"), containsInAnyOrder("IDENT_1"));
    }
}