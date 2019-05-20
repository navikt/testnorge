package no.nav.dolly.mapper;

import static org.assertj.core.util.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusIdent;

@RunWith(MockitoJUnitRunner.class)
public class BestillingArenaForvalterStatusMapperTest {

    private static final List<BestillingProgress> RUN_STATUS = newArrayList(
            BestillingProgress.builder().ident("IDENT_1")
                    .arenaforvalterStatus("OK")
                    .build(),
            BestillingProgress.builder().ident("IDENT_2")
                    .arenaforvalterStatus("FEIL")
                    .build(),
            BestillingProgress.builder().ident("IDENT_3")
                    .arenaforvalterStatus("OK")
                    .build(),
            BestillingProgress.builder().ident("IDENT_4")
                    .arenaforvalterStatus("FEIL")
                    .build(),
            BestillingProgress.builder().ident("IDENT_5")
                    .arenaforvalterStatus("OK")
                    .build()
    );

    @Test
    public void buildArenaForvalterStatusMap() {

        List<RsStatusIdent> identStatuses = BestillingArenaForvalterStatusMapper.buildArenaForvalterStatusMap(RUN_STATUS);

        assertThat(identStatuses.get(0).getStatusMelding(), is(equalTo("FEIL")));
        assertThat(identStatuses.get(0).getIdenter(), containsInAnyOrder("IDENT_2", "IDENT_4"));

        assertThat(identStatuses.get(1).getStatusMelding(), is(equalTo("OK")));
        assertThat(identStatuses.get(1).getIdenter(), containsInAnyOrder("IDENT_1", "IDENT_3", "IDENT_5"));
    }
}