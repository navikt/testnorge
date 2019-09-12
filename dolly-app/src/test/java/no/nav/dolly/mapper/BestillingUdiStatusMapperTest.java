package no.nav.dolly.mapper;

import static org.assertj.core.util.Lists.newArrayList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusIdent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class BestillingUdiStatusMapperTest {

    private static final List<BestillingProgress> ERROR_STATUS = newArrayList(
            BestillingProgress.builder().ident("IDENT_1")
                    .udistubStatus("OK: ident=IDENT_1")
                    .build(),
            BestillingProgress.builder().ident("IDENT_2")
                    .udistubStatus("FEIL: Gjenopprett feilet for udistubclient: 500 Internal Server Error")
                    .build(),
            BestillingProgress.builder().ident("IDENT_3")
                    .udistubStatus("OK: ident=IDENT_3")
                    .build(),
            BestillingProgress.builder().ident("IDENT_4")
                    .udistubStatus("FEIL: Gjenopprett feilet for udistubclient: 500 Internal Server Error")
                    .build(),
            BestillingProgress.builder().ident("IDENT_5")
                    .udistubStatus("OK: ident=IDENT_5")
                    .build()
    );

    @Test
    public void udiStatusMapper_MapFeilmeldinger() {
        List<RsStatusIdent> mapperResult = BestillingUdiStubStatusMapper.buildUdiStubStatusMap(ERROR_STATUS);
        BestillingTpsfStatusMapper.buildTpsfStatusMap(ERROR_STATUS);

        assertThat(mapperResult.get(0).getStatusMelding(), is(equalTo("FEIL: Gjenopprett feilet for udistubclient: 500 Internal Server Error")));
        assertThat(mapperResult.get(0).getIdenter().get(0), is(equalTo("IDENT_2")));
        assertThat(mapperResult.get(0).getIdenter().get(1), is(equalTo("IDENT_4")));
        assertThat(mapperResult.get(1).getStatusMelding(), is(equalTo("OK: ident=IDENT_3")));
        assertThat(mapperResult.get(2).getStatusMelding(), is(equalTo("OK: ident=IDENT_5")));
        assertThat(mapperResult.get(3).getStatusMelding(), is(equalTo("OK: ident=IDENT_1")));
    }
}