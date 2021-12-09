package no.nav.dolly.mapper;

import no.nav.dolly.domain.jpa.BestillingProgress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class BestillingTpsMessagingStatusMapperTest {

    private static final List<BestillingProgress> RUN_STATUS = List.of(
            BestillingProgress.builder().ident("IDENT_1")
                    .tpsMessagingStatus("Egenansatt_send#t4:OK,Feil= error=Miljø ikke støttet")
                    .build()
    );

    private static final List<BestillingProgress> ERROR_NO_ENV = List.of(
            BestillingProgress.builder().ident("IDENT_1")
                    .tpsMessagingStatus("Feil=error=Bad Request")
                    .build()
    );

    private static final List<BestillingProgress> ERROR_WITH_ENV = List.of(
            BestillingProgress.builder().ident("IDENT_1")
                    .tpsMessagingStatus("Spraakkode#t4:FEIL:Ingen svarstatus mottatt fra TPS,Spraakkode#q2:FEIL:Ingen svarstatus mottatt fra TPS")
                    .build()
    );

    @Test
    void buildTpsMessagingStatusMap_OK() {

//        List<RsStatusRapport> identStatuses = BestillingTpsMessagingStatusMapper.buildTpsMessagingStatusMap(RUN_STATUS);
//
//        assertThat(identStatuses.get(0).getStatuser().get(0).getMelding(), is(equalTo("OK")));
//        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("t4")));
//        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getIdenter(), containsInAnyOrder("IDENT_1"));
//
//        assertThat(identStatuses.get(0).getStatuser().get(1).getMelding(), is(equalTo("error:Miljø ikke støttet")));
//        assertThat(identStatuses.get(0).getStatuser().get(1).getDetaljert().get(0).getMiljo(), is(equalTo("NA")));
//        assertThat(identStatuses.get(0).getStatuser().get(1).getDetaljert().get(0).getIdenter(), containsInAnyOrder("IDENT_1"));

    }

    @Test
    void buildTpsMessagingStatusMap_ERROR_NO_ENV() {

//        List<RsStatusRapport> identStatuses = BestillingTpsMessagingStatusMapper.buildTpsMessagingStatusMap(ERROR_NO_ENV);
//
//        assertThat(identStatuses.get(0).getStatuser().get(0).getMelding(), is(equalTo("error:Bad Request")));
//        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("NA")));
//        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getIdenter(), containsInAnyOrder("IDENT_1"));
    }

    @Test
    void buildTpsMessagingStatusMap_ERROR_WITH_ENV() {

//        List<RsStatusRapport> identStatuses = BestillingTpsMessagingStatusMapper.buildTpsMessagingStatusMap(ERROR_WITH_ENV);
//
//        assertThat(identStatuses.get(0).getStatuser().get(0).getMelding(), is(equalTo("FEIL:Ingen svarstatus mottatt fra TPS")));
//        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("t4")));
//        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getIdenter(), containsInAnyOrder("IDENT_1"));
//
//        assertThat(identStatuses.get(0).getStatuser().get(1).getMelding(), is(equalTo("FEIL:Ingen svarstatus mottatt fra TPS")));
//        assertThat(identStatuses.get(0).getStatuser().get(1).getDetaljert().get(0).getMiljo(), is(equalTo("q2")));
//        assertThat(identStatuses.get(0).getStatuser().get(1).getDetaljert().get(0).getIdenter(), containsInAnyOrder("IDENT_1"));
    }
}