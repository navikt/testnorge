package no.nav.dolly.mapper;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusMiljoeIdent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class BestillingTpsfStatusMapperTest {

    private static final List<BestillingProgress> ERROR_STATUS = asList(
            BestillingProgress.builder().ident("IDENT_1")
                    .feil("u5: FEIL: Data om innvandring var ikke utfylt,t0: FEIL: Data om innvandring var ikke utfylt,t1: FEIL: Data om innvandring var ikke utfylt,q0: FEIL: Data om innvandring var ikke utfylt\n")
                    .build(),
            BestillingProgress.builder().ident("IDENT_2")
                    .feil("u5: FEIL: Data om innvandring var ikke utfylt,t0: FEIL: Data om innvandring var ikke utfylt,t1: FEIL: Data om innvandring var ikke utfylt,q0: FEIL: Data om innvandring var ikke utfylt\n")
                    .build(),
            BestillingProgress.builder().ident("IDENT_3")
                    .feil("t4: FEIL: UKJENT,t5: FEIL: UKJENT,t6: FEIL: UKJENT,t10: FEIL: UKJENT,t8: FEIL: UKJENT,t13: FEIL: Ikke tilgang til endringsmelding,t2: FEIL: UKJENT,t3: FEIL: UKJENT\n")
                    .build(),
            BestillingProgress.builder().ident("IDENT_4")
                    .feil("q1: FEIL: UKJENT,qx: FEIL: SQL-problem i KC57308,q10: FEIL: UKJENT,t13: FEIL: Ikke tilgang til endringsmelding\n")
                    .build(),
            BestillingProgress.builder().ident("IDENT_5").feil("t0: FEIL: UKJENT,t1: FEIL: UKJENT").build()
    );

    @Test
    public void tpsfStatusMapper_MapFeilmeldinger() {

        List<RsStatusMiljoeIdent> mapperResult = BestillingTpsfStatusMapper.buildTpsfStatusMap(ERROR_STATUS);

        assertThat(mapperResult.get(0).getStatusMelding(), is(equalTo("FEIL: SQL-problem i KC57308")));
        assertThat(mapperResult.get(0).getEnvironmentIdents().get("qx"), contains("IDENT_4"));

        assertThat(mapperResult.get(1).getStatusMelding(), is(equalTo("FEIL: Data om innvandring var ikke utfylt")));
        assertThat(mapperResult.get(1).getEnvironmentIdents().get("u5"), containsInAnyOrder("IDENT_1", "IDENT_2"));
        assertThat(mapperResult.get(1).getEnvironmentIdents().get("t0"), containsInAnyOrder("IDENT_1", "IDENT_2"));
        assertThat(mapperResult.get(1).getEnvironmentIdents().get("t1"), containsInAnyOrder("IDENT_1", "IDENT_2"));
        assertThat(mapperResult.get(1).getEnvironmentIdents().get("q0"), containsInAnyOrder("IDENT_1", "IDENT_2"));

        assertThat(mapperResult.get(2).getStatusMelding(), is(equalTo("FEIL: Ikke tilgang til endringsmelding")));
        assertThat(mapperResult.get(2).getEnvironmentIdents().get("t13"), containsInAnyOrder("IDENT_3", "IDENT_4"));

        assertThat(mapperResult.get(3).getStatusMelding(), is(equalTo("FEIL: UKJENT")));
        assertThat(mapperResult.get(3).getEnvironmentIdents().get("t0"), contains("IDENT_5"));
        assertThat(mapperResult.get(3).getEnvironmentIdents().get("t1"), contains("IDENT_5"));
        assertThat(mapperResult.get(3).getEnvironmentIdents().get("t2"), contains("IDENT_3"));
        assertThat(mapperResult.get(3).getEnvironmentIdents().get("t3"), contains("IDENT_3"));
        assertThat(mapperResult.get(3).getEnvironmentIdents().get("t4"), contains("IDENT_3"));
        assertThat(mapperResult.get(3).getEnvironmentIdents().get("t5"), contains("IDENT_3"));
        assertThat(mapperResult.get(3).getEnvironmentIdents().get("t8"), contains("IDENT_3"));
        assertThat(mapperResult.get(3).getEnvironmentIdents().get("q1"), contains("IDENT_4"));
        assertThat(mapperResult.get(3).getEnvironmentIdents().get("q10"), contains("IDENT_4"));
    }
}