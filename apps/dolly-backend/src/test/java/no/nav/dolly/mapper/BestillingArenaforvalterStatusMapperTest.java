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
class BestillingArenaforvalterStatusMapperTest {

    private static final List<BestillingProgress> RUN_STATUS = List.of(
            BestillingProgress.builder().ident("IDENT_1")
                    .arenaforvalterStatus("t4$OK,t3$Feil: Miljø ikke støttet")
                    .build()
    );

    //TODO: GJØRE FERDIG DENNE TESTEN
    private static final List<BestillingProgress> ORACLE_EXCEPTION = List.of(
            BestillingProgress.builder().ident("IDENT_1")
                    .arenaforvalterStatus("q1$AKTIVER_AAP: { \"code\"= \"UserDefinedResourceError\"; \"title\"= \"User Defined Resource Error\"; \"message\"= \"The request could not be processed due to an error in a user defined resource\"; \"o=errorCode\"= \"ORDS-25001\"; \"cause\"= \"An error occurred when evaluating the SQL statement associated with this resource. SQL Error Code 20999; Error Message= ORA-20999= Det finnes et overlappende vedtak om livsoppholdsytelse for denne perioden.\\nORA-06512= ved \\\"DUMMY.DUMMY_DUMMY\\\"; line 1234\\nORA-06512= ved \\\"DUMMY.DUMMY_DUMMY\\\"; line 1234\\nORA-06512= ved \\\"DUMMY.DUMMY_DUMMY\\\"; line 1234\\nORA-06512= ved \\\"DUMMY.DUMMY_DUMMY\\\"; line 1234\\nORA-06512= ved \\\"DUMMY.DUMMY_DUMMY\\\"; line 1234\\nORA-06512= ved \\\"DUMMY.DUMMYDUMMY\\\"; line 15\\nORA-06512= ved line 1\\n\"; \"action\"= \"Ask the user defined resource author to check the SQL statement is correctly formed and executes without error\"; \"type\"= \"tag=oracle.com;2020=error/UserDefinedResourceError\"; \"instance\"= \"tag=oracle.com;2020=ecid/adawd\" }")
                    .build()
    );

    @Test
    void buildArenaForvalterStatusMap_OK() {

        List<RsStatusRapport> identStatuses = BestillingArenaforvalterStatusMapper.buildArenaStatusMap(RUN_STATUS);

        assertThat(identStatuses.get(0).getStatuser().get(0).getMelding(), is(equalTo("Feil: Miljø ikke støttet")));
        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getMiljo(), is(equalTo("t3")));
        assertThat(identStatuses.get(0).getStatuser().get(0).getDetaljert().get(0).getIdenter(), containsInAnyOrder("IDENT_1"));

        assertThat(identStatuses.get(0).getStatuser().get(1).getMelding(), is(equalTo("OK")));
        assertThat(identStatuses.get(0).getStatuser().get(1).getDetaljert().get(0).getMiljo(), is(equalTo("t4")));
        assertThat(identStatuses.get(0).getStatuser().get(1).getDetaljert().get(0).getIdenter(), containsInAnyOrder("IDENT_1"));
    }
}