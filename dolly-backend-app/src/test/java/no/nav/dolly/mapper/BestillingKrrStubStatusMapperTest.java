package no.nav.dolly.mapper;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class BestillingKrrStubStatusMapperTest {

    private static final List<BestillingProgress> RUN_STATUS = asList(
            BestillingProgress.builder().ident("IDENT_1")
                    .krrstubStatus("OK")
                    .build(),
            BestillingProgress.builder().ident("IDENT_2")
                    .krrstubStatus("FEIL")
                    .build(),
            BestillingProgress.builder().ident("IDENT_3")
                    .krrstubStatus("OK")
                    .build(),
            BestillingProgress.builder().ident("IDENT_4")
                    .krrstubStatus("FEIL")
                    .build(),
            BestillingProgress.builder().ident("IDENT_5")
                    .krrstubStatus("OK")
                    .build()
    );

    @Test
    public void krrStubStatusMap() {

        List<RsStatusRapport> rsStatusRapporter = BestillingKrrStubStatusMapper.buildKrrStubStatusMap(RUN_STATUS);

        assertThat(rsStatusRapporter.get(0).getStatuser().get(0).getMelding(), is(equalTo("FEIL")));
        assertThat(rsStatusRapporter.get(0).getStatuser().get(0).getIdenter(), containsInAnyOrder("IDENT_2", "IDENT_4"));

        assertThat(rsStatusRapporter.get(0).getStatuser().get(1).getIdenter(), is(equalTo("OK")));
        assertThat(rsStatusRapporter.get(0).getStatuser().get(1).getIdenter(), containsInAnyOrder("IDENT_1", "IDENT_3", "IDENT_5"));
    }
}