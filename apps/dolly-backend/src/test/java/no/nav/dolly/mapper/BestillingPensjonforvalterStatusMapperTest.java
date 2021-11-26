package no.nav.dolly.mapper;

import static java.util.Collections.singletonList;
import static no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterClient.PENSJON_FORVALTER;
import static no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterClient.POPP_INNTEKTSREGISTER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.Test;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import no.nav.dolly.domain.resultset.SystemTyper;

class BestillingPensjonforvalterStatusMapperTest {

    private static final String IDENT = "12345678901";

    private static final String STATUS_MELDING = "PensjonForvalter#q2:Feil= PreparedStatementCallback; bad SQL grammar "
            + "[INSERT INTO PEN.T_PERSON (FNR_FK, DATO_FODSEL, DATO_DOD, DATO_UTVANDRET, BOSTEDSLAND, DATO_OPPRETTET, OPPRETTET_AV, "
            + "DATO_ENDRET, ENDRET_AV, VERSJON) VALUES (?,?,?,?,?,?,?,?,?,?)]; "
            + "nested exception is java.sql.SQLSyntaxErrorException: ORA-01031: insufficient privileges\n"
            + ",t4:Feil= PreparedStatementCallback; bad SQL grammar "
            + "[INSERT INTO PEN.T_PERSON (FNR_FK, DATO_FODSEL, DATO_DOD, DATO_UTVANDRET, BOSTEDSLAND, "
            + "DATO_OPPRETTET, OPPRETTET_AV, DATO_ENDRET, ENDRET_AV, VERSJON) VALUES (?,?,?,?,?,?,?,?,?,?)]; "
            + "nested exception is java.sql.SQLSyntaxErrorException: ORA-01031: insufficient privileges\n"
            + ",q4:Feil= PreparedStatementCallback; bad SQL grammar "
            + "[INSERT INTO PEN.T_PERSON (FNR_FK, DATO_FODSEL, DATO_DOD, DATO_UTVANDRET, BOSTEDSLAND, DATO_OPPRETTET, "
            + "OPPRETTET_AV, DATO_ENDRET, ENDRET_AV, VERSJON) VALUES (?,?,?,?,?,?,?,?,?,?)]; "
            + "nested exception is java.sql.SQLSyntaxErrorException: ORA-01031: insufficient privileges\n"
            + ",";

    @Test
    void buildPensjonforvalterStatusMap() {

        BestillingProgress progress = BestillingProgress.builder()
                .pensjonforvalterStatus(PENSJON_FORVALTER + "#q2:OK,")
                .ident(IDENT)
                .build();

        RsStatusRapport statusRapport = BestillingPensjonforvalterStatusMapper.buildPensjonforvalterStatusMap(singletonList(progress)).get(0);

        assertThat(statusRapport.getId(), is(equalTo(SystemTyper.PEN_FORVALTER)));
        assertThat(statusRapport.getNavn(), is(equalTo(SystemTyper.PEN_FORVALTER.getBeskrivelse())));
        assertThat(statusRapport.getStatuser(), hasSize(1));
        assertThat(statusRapport.getStatuser().get(0).getMelding(), is(equalTo("OK")));
        assertThat(statusRapport.getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("q2")));
        assertThat(statusRapport.getStatuser().get(0).getDetaljert().get(0).getIdenter(), containsInAnyOrder(IDENT));
    }

    @Test
    void buildPoppInntektsStatusMap() {

        BestillingProgress progress = BestillingProgress.builder()
                .pensjonforvalterStatus(PENSJON_FORVALTER + "#q2:OK,$" + POPP_INNTEKTSREGISTER + "#q1:Feil i system,q2:OK,")
                .ident(IDENT)
                .build();

        RsStatusRapport statusRapport = BestillingPensjonforvalterStatusMapper.buildPensjonforvalterStatusMap(singletonList(progress)).get(0);

        assertThat(statusRapport.getId(), is(equalTo(SystemTyper.PEN_INNTEKT)));
        assertThat(statusRapport.getNavn(), is(equalTo(SystemTyper.PEN_INNTEKT.getBeskrivelse())));
        assertThat(statusRapport.getStatuser(), hasSize(2));
        assertThat(statusRapport.getStatuser().get(0).getMelding(), is(equalTo("Feil i system")));
        assertThat(statusRapport.getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("q1")));
        assertThat(statusRapport.getStatuser().get(0).getDetaljert().get(0).getIdenter(), containsInAnyOrder(IDENT));
        assertThat(statusRapport.getStatuser().get(1).getMelding(), is(equalTo("OK")));
        assertThat(statusRapport.getStatuser().get(1).getDetaljert().get(0).getMiljo(), is(equalTo("q2")));
        assertThat(statusRapport.getStatuser().get(1).getDetaljert().get(0).getIdenter(), containsInAnyOrder(IDENT));
    }

    @Test
    void parseComplexStatus() {

        BestillingProgress progress = BestillingProgress.builder()
                .pensjonforvalterStatus(STATUS_MELDING)
                .ident(IDENT)
                .build();

        RsStatusRapport statusRapport = BestillingPensjonforvalterStatusMapper.buildPensjonforvalterStatusMap(singletonList(progress)).get(0);

        assertThat(statusRapport.getStatuser().get(0).getMelding(), is(equalTo("Feil: PreparedStatementCallback; bad SQL grammar [INSERT INTO PEN.T_PERSON (FNR_FK")));
        assertThat(statusRapport.getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("t4")));
        assertThat(statusRapport.getStatuser().get(0).getDetaljert().get(0).getIdenter(), contains(IDENT));
        assertThat(statusRapport.getStatuser().get(0).getDetaljert().get(1).getMiljo(), is(equalTo("q2")));
        assertThat(statusRapport.getStatuser().get(0).getDetaljert().get(1).getIdenter(), contains(IDENT));
        assertThat(statusRapport.getStatuser().get(0).getDetaljert().get(2).getMiljo(), is(equalTo("q4")));
        assertThat(statusRapport.getStatuser().get(0).getDetaljert().get(2).getIdenter(), contains(IDENT));
    }
}