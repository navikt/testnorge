package no.nav.dolly.mapper;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
public class BestillingUdiStatusMapperTest {

    private static final List<BestillingProgress> ERROR_STATUS = List.of(
            BestillingProgress.builder().ident("IDENT_1")
                    .udistubStatus("OK")
                    .build(),
            BestillingProgress.builder().ident("IDENT_2")
                    .udistubStatus("FEIL: Gjenopprett feilet for udistubclient: 500 Internal Server Error")
                    .build(),
            BestillingProgress.builder().ident("IDENT_3")
                    .udistubStatus("OK")
                    .build(),
            BestillingProgress.builder().ident("IDENT_4")
                    .udistubStatus("FEIL: Gjenopprett feilet for udistubclient: 500 Internal Server Error")
                    .build(),
            BestillingProgress.builder().ident("IDENT_5")
                    .udistubStatus("OK")
                    .build()
    );

    @Test
    public void udiStatusMapper_MapFeilmeldinger() {
        List<RsStatusRapport> statusRapport = BestillingUdiStubStatusMapper.buildUdiStubStatusMap(ERROR_STATUS);

        assertThat(statusRapport.get(0).getStatuser().get(0).getMelding(), is(equalTo("FEIL: Gjenopprett feilet for udistubclient: 500 Internal Server Error")));
        assertThat(statusRapport.get(0).getStatuser().get(0).getIdenter(), containsInAnyOrder("IDENT_2", "IDENT_4"));
        assertThat(statusRapport.get(0).getStatuser().get(1).getMelding(), is(equalTo("OK")));
        assertThat(statusRapport.get(0).getStatuser().get(1).getIdenter(), containsInAnyOrder("IDENT_3", "IDENT_5", "IDENT_1"));
    }
}