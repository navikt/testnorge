package no.nav.dolly.mapper;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(MockitoExtension.class)
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

        List<RsStatusRapport> identStatuses = BestillingKrrStubStatusMapper.buildKrrStubStatusMap(RUN_STATUS);

        assertThat(identStatuses.get(0).getStatuser().get(0).getMelding(), is(equalTo("FEIL")));
        assertThat(identStatuses.get(0).getStatuser().get(0).getIdenter(), containsInAnyOrder("IDENT_2", "IDENT_4"));

        assertThat(identStatuses.get(0).getStatuser().get(1).getMelding(), is(equalTo("OK")));
        assertThat(identStatuses.get(0).getStatuser().get(1).getIdenter(), containsInAnyOrder("IDENT_1", "IDENT_3", "IDENT_5"));
    }
}