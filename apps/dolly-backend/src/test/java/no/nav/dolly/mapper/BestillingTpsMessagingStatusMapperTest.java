package no.nav.dolly.mapper;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

@ExtendWith(MockitoExtension.class)
class BestillingTpsMessagingStatusMapperTest {

    private static final List<BestillingProgress> RUN_STATUS = List.of(
            BestillingProgress.builder().ident("IDENT_1")
                    .tpsMessagingStatus("Egenansatt_send#t4:OK,Feil= error=Miljø ikke støttet")
                    .build()
    );

    private static final List<BestillingProgress> ERROR_STATUS = List.of(
            BestillingProgress.builder().ident("IDENT_1")
                    .tpsMessagingStatus("Feil=error=Bad Request")
                    .build()
    );

    @Test
    void buildTpsMessagingStatusMap_OK() {

        List<RsStatusRapport> identStatuses = BestillingTpsMessagingStatusMapper.buildTpsMessagingStatusMap(RUN_STATUS);

        assertThat(identStatuses.get(0).getStatuser().get(0).getMelding(), is(equalTo("OK")));
        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("t4")));
        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getIdenter(), containsInAnyOrder("IDENT_1"));

        assertThat(identStatuses.get(0).getStatuser().get(1).getMelding(), is(equalTo("error:Miljø ikke støttet")));
        assertThat(identStatuses.get(0).getStatuser().get(1).getDetaljert().get(0).getMiljo(), is(equalTo("NA")));
        assertThat(identStatuses.get(0).getStatuser().get(1).getDetaljert().get(0).getIdenter(), containsInAnyOrder("IDENT_1"));

    }

    @Test
    void buildTpsMessagingStatusMap_ERROR() {

        List<RsStatusRapport> identStatuses = BestillingTpsMessagingStatusMapper.buildTpsMessagingStatusMap(ERROR_STATUS);

        assertThat(identStatuses.get(0).getStatuser().get(0).getMelding(), is(equalTo("error:Bad Request")));
        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("NA")));
        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getIdenter(), containsInAnyOrder("IDENT_1"));
    }
}