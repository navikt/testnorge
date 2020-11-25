package no.nav.dolly.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;

@RunWith(MockitoJUnitRunner.class)
public class BestillingTpsfStatusMapperTest {

    private static final List<BestillingProgress> ERROR_STATUS = List.of(
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

        List<RsStatusRapport> statusrapport = BestillingTpsfStatusMapper.buildTpsfStatusMap(ERROR_STATUS);

        assertThat(statusrapport.get(0).getStatuser().get(0).getMelding(), is(equalTo("FEIL: SQL-problem i KC57308")));
        assertThat(statusrapport.get(0).getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("qx")));
        assertThat(statusrapport.get(0).getStatuser().get(0).getDetaljert().get(0).getIdenter(), contains("IDENT_4"));

        assertThat(statusrapport.get(0).getStatuser().get(1).getMelding(), is(equalTo("FEIL: Data om innvandring var ikke utfylt")));
        assertThat(statusrapport.get(0).getStatuser().get(1).getDetaljert().get(0).getMiljo(), is(equalTo("u5")));
        assertThat(statusrapport.get(0).getStatuser().get(1).getDetaljert().get(0).getIdenter(), containsInAnyOrder("IDENT_1", "IDENT_2"));
        assertThat(statusrapport.get(0).getStatuser().get(1).getDetaljert().get(1).getMiljo(), is(equalTo("t0")));
        assertThat(statusrapport.get(0).getStatuser().get(1).getDetaljert().get(1).getIdenter(), containsInAnyOrder("IDENT_1", "IDENT_2"));
        assertThat(statusrapport.get(0).getStatuser().get(1).getDetaljert().get(2).getMiljo(), is(equalTo("t1")));
        assertThat(statusrapport.get(0).getStatuser().get(1).getDetaljert().get(2).getIdenter(), containsInAnyOrder("IDENT_1", "IDENT_2"));
        assertThat(statusrapport.get(0).getStatuser().get(1).getDetaljert().get(3).getMiljo(), is(equalTo("q0")));
        assertThat(statusrapport.get(0).getStatuser().get(1).getDetaljert().get(3).getIdenter(), containsInAnyOrder("IDENT_1", "IDENT_2"));

        assertThat(statusrapport.get(0).getStatuser().get(2).getMelding(), is(equalTo("FEIL: Ikke tilgang til endringsmelding")));
        assertThat(statusrapport.get(0).getStatuser().get(2).getDetaljert().get(0).getMiljo(), is(equalTo("t13")));
        assertThat(statusrapport.get(0).getStatuser().get(2).getDetaljert().get(0).getIdenter(), containsInAnyOrder("IDENT_3", "IDENT_4"));

        assertThat(statusrapport.get(0).getStatuser().get(3).getMelding(), is(equalTo("FEIL: UKJENT")));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(0).getMiljo(), is(equalTo("t4")));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(0).getIdenter(), contains("IDENT_3"));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(1).getMiljo(), is(equalTo("q1")));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(1).getIdenter(), contains("IDENT_4"));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(2).getMiljo(), is(equalTo("t5")));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(2).getIdenter(), contains("IDENT_3"));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(3).getMiljo(), is(equalTo("q10")));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(3).getIdenter(), contains("IDENT_4"));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(4).getMiljo(), is(equalTo("t6")));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(4).getIdenter(), contains("IDENT_3"));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(5).getMiljo(), is(equalTo("t10")));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(5).getIdenter(), contains("IDENT_3"));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(6).getMiljo(), is(equalTo("t8")));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(6).getIdenter(), contains("IDENT_3"));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(7).getMiljo(), is(equalTo("t0")));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(7).getIdenter(), contains("IDENT_5"));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(8).getMiljo(), is(equalTo("t1")));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(8).getIdenter(), contains("IDENT_5"));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(9).getMiljo(), is(equalTo("t2")));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(9).getIdenter(), contains("IDENT_3"));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(10).getMiljo(), is(equalTo("t3")));
        assertThat(statusrapport.get(0).getStatuser().get(3).getDetaljert().get(10).getIdenter(), contains("IDENT_3"));

    }
}