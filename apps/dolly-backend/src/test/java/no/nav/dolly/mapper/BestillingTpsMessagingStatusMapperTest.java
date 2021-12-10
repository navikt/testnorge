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
                    .tpsMessagingStatus("Egenansatt_send#t4:OK,t6:Feil= Miljø ikke støttet")
                    .build()
    );

    private static final List<BestillingProgress> ERROR_AND_OK = List.of(
            BestillingProgress.builder().ident("IDENT_1")
                    .tpsMessagingStatus("Telefonnummer#t4:Feil= Bad Request$Sprakkode#t4:OK")
                    .build()
    );

    private static final List<BestillingProgress> ERROR_WITH_ENV = List.of(
            BestillingProgress.builder().ident("IDENT_1")
                    .tpsMessagingStatus("Spraakkode#t4:FEIL= Ingen svarstatus mottatt fra TPS,q2:FEIL= Ingen svarstatus mottatt fra TPS")
                    .build()
    );

    @Test
    void buildTpsMessagingStatusMap_OK() {

        var identStatuses = BestillingTpsMessagingStatusMapper.buildTpsMessagingStatusMap(RUN_STATUS);

        assertThat(identStatuses.get(0).getStatuser().get(1).getMelding(), is(equalTo("OK")));
        assertThat(identStatuses.get(0).getStatuser().get(1).getDetaljert().get(0).getMiljo(), is(equalTo("t4")));
        assertThat(identStatuses.get(0).getStatuser().get(1).getDetaljert().get(0).getIdenter(), containsInAnyOrder("IDENT_1"));

        assertThat(identStatuses.get(0).getStatuser().get(0).getMelding(), is(equalTo("Egenansatt_send Feil: Miljø ikke støttet")));
        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("t6")));
        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getIdenter(), containsInAnyOrder("IDENT_1"));

    }

    @Test
    void buildTpsMessagingStatusMap_ERROR_AND_OK() {

        List<RsStatusRapport> identStatuses = BestillingTpsMessagingStatusMapper.buildTpsMessagingStatusMap(ERROR_AND_OK);

        assertThat(identStatuses.get(0).getStatuser().get(0).getMelding(), is(equalTo("Telefonnummer Feil: Bad Request")));
        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("t4")));
        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getIdenter(), containsInAnyOrder("IDENT_1"));
    }

    @Test
    void buildTpsMessagingStatusMap_ERROR_WITH_ENV() {

        List<RsStatusRapport> identStatuses = BestillingTpsMessagingStatusMapper.buildTpsMessagingStatusMap(ERROR_WITH_ENV);

        assertThat(identStatuses.get(0).getStatuser().get(0).getMelding(), is(equalTo("Spraakkode FEIL: Ingen svarstatus mottatt fra TPS")));
        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("t4")));
        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getIdenter(), containsInAnyOrder("IDENT_1"));
        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(1).getMiljo(), is(equalTo("q2")));
        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(1).getIdenter(), containsInAnyOrder("IDENT_1"));
    }
}