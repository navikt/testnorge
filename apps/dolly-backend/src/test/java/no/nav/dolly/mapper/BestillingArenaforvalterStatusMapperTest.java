package no.nav.dolly.mapper;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsStatusRapport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

@ExtendWith(MockitoExtension.class)
class BestillingArenaforvalterStatusMapperTest {

    private static final String IDENT = "IDENT_1";
    private static final List<BestillingProgress> RUN_STATUS = singletonList(
            BestillingProgress.builder().ident(IDENT)
                    .arenaforvalterStatus("t4$OK,t3$Feil: Miljø ikke støttet")
                    .build()
    );

    private static final List<BestillingProgress> ARENA_ORACLE_EXCEPTION = singletonList(
            BestillingProgress.builder().ident(IDENT)
                    .arenaforvalterStatus("q1$AKTIVER_AAP: { \"code\"= \"UserDefinedResourceError\"; \"title\"= \"User Defined Resource Error\"; \"message\"= \"The request could not be processed due to an error in a user defined resource\"; \"o=errorCode\"= \"ORDS-1234\"; \"cause\"= \"An error occurred when evaluating the SQL statement associated with this resource. SQL Error Code 1234; Error Message= ORA-1234= Det finnes et overlappende vedtak om livsoppholdsytelse for denne perioden.\\nORA-1234= ved \\\"DUMMY.DUMMY_DUMMY\\\"; line 1234\\nORA-1234= ved \\\"DUMMY.DUMMY_DUMMY\\\"; line 1234\\nORA-1234= ved \\\"DUMMY.DUMMY_DUMMY\\\"; line 1234\\nORA-1234= ved \\\"DUMMY.DUMMY_DUMMY\\\"; line 1234\\nORA-1234= ved \\\"DUMMY.DUMMY_DUMMY\\\"; line 1234\\nORA-1234= ved \\\"DUMMY.DUMMYDUMMY\\\"; line 15\\nORA-1234= ved line 1\\n\"; \"action\"= \"Ask the user defined resource author to check the SQL statement is correctly formed and executes without error\"; \"type\"= \"tag=oracle.com;2020=error/UserDefinedResourceError\"; \"instance\"= \"tag=oracle.com;2020=ecid/adawd\" }")
                    .build()
    );

    private static final List<BestillingProgress> ARENA_ORACLE_EXCEPTION_DOED = singletonList(
            BestillingProgress.builder().ident(IDENT)
                    .arenaforvalterStatus("q2$BRUKER Oppretting= AKTIVER_BRUKER=exception type= class org.springframework.web.client.HttpServerErrorException; message= 555 User Defined Resource Error= <EOL>{<EOL>    code= UserDefinedResourceError;<EOL>    title= User Defined Resource Error;<EOL>    message= The request could not be processed due to an error in a user defined resource;<EOL>    o=errorCode= ORDS-1234;<EOL>    cause= An error occurred when evaluating the SQL statement associated with this resource. SQL Error Code 1234; Error Message= ORA-1234= -1234= Person med fødselsnr 12345678912 kan ikke aktiveres fordi denne er død ved forsøk på aktivering med iverksatt 14a-vedtak.\\nORA-1234= ved \\DUMMY_DUMMY.DUMMY_DUMMY\\; line 123\\nORA-1234= ved \\DUMMY_DUMMY.DUMMY_DUMMY\\; line 123\\nORA-1234= ved \\DUMMY_DUMMY.DUMMY_DUMMY\\; line 5\\nORA-1234= ved line 1\\n;<EOL>    action= Ask the user defined resource author to check the SQL statement is correctly formed and executes without error;<EOL>    type= tag=oracle.com;2020=error/UserDefinedResourceError;<EOL>    instance= tag=oracle.com;2020=ecid/dawdawdawda<EOL>}")
                    .build()
    );

    private static final List<BestillingProgress> ARENA_ORACLE_EXCEPTION_TJENESTE = singletonList(
            BestillingProgress.builder().ident(IDENT)
                    .arenaforvalterStatus("q1$BRUKER Oppretting= AKTIVER_BRUKER=exception type= class org.springframework.web.client.HttpServerErrorException; message= 555 User Defined Resource Error= \"<EOL>{<EOL>    \"code\"= \"UserDefinedResourceError\";<EOL>    \"title\"= \"User Defined Resource Error\";<EOL>    \"message\"= \"The request could not be processed due to an error in a user defined resource\";<EOL>    \"o=errorCode\"= \"ORDS-12345\";<EOL>    \"cause\"= \"An error occurred when evaluating the SQL statement associated with this resource. SQL Error Code 20999; Error Message= ORA-12345= -20180= Tjeneste HentPerson returnerte feil= I/O error on POST request for \\\"https://pdl-api-q1.dev.intern.nav.no/graphql\\\": Read timed out; nested exception is java.net.SocketTimeoutException: Read timed out ved forsøk på aktivering med iverksatt 14a-vedtak.\\nORA-12345= ved \\\"SIAMO.SYNT_PERSON\\\"; line 123\\nORA-12345= ved \\\"SIAMO.SYNT_PERSON\\\"; line 123\\nORA-12345= ved \\\"SYNT_REST.BRUKEROPPFOLGING\\\"; line 1\\nORA-12345= ved line 1\\n\";<EOL>    \"action\"= \"Ask the user defined resource author to check the SQL statement is correctly formed and executes without error\";<EOL>    \"type\"= \"tag=oracle.com;2020=error/UserDefinedResourceError\";<EOL>    \"instance\"= \"tag=oracle.com;2020=ecid/123456qwerty")
                    .build()
    );

    @Test
    void buildArenaForvalterStatusMap_OK() {

        List<RsStatusRapport> identStatuses = BestillingArenaforvalterStatusMapper.buildArenaStatusMap(RUN_STATUS);

        assertThat(identStatuses.getFirst().getStatuser().getFirst().getMelding(), is(equalTo("Feil: Miljø ikke støttet")));
        assertThat(identStatuses.getFirst().getStatuser().getFirst().getDetaljert().getFirst().getMiljo(), is(equalTo("t3")));
        assertThat(identStatuses.getFirst().getStatuser().getFirst().getDetaljert().getFirst().getIdenter(), containsInAnyOrder(IDENT));

        assertThat(identStatuses.getFirst().getStatuser().get(1).getMelding(), is(equalTo("OK")));
        assertThat(identStatuses.getFirst().getStatuser().get(1).getDetaljert().getFirst().getMiljo(), is(equalTo("t4")));
        assertThat(identStatuses.getFirst().getStatuser().get(1).getDetaljert().getFirst().getIdenter(), containsInAnyOrder(IDENT));
    }

    @Test
    void formaterOracleException_OK() {

        List<RsStatusRapport> identStatuses = BestillingArenaforvalterStatusMapper.buildArenaStatusMap(ARENA_ORACLE_EXCEPTION);

        assertThat(identStatuses.getFirst().getStatuser().getFirst().getMelding(), is(equalTo("Feil: Det finnes et overlappende vedtak om livsoppholdsytelse for denne perioden.")));
        assertThat(identStatuses.getFirst().getStatuser().getFirst().getDetaljert().getFirst().getMiljo(), is(equalTo("q1")));
        assertThat(identStatuses.getFirst().getStatuser().getFirst().getDetaljert().getFirst().getIdenter(), containsInAnyOrder(IDENT));
    }

    @Test
    void formaterOracleExceptionDoed_OK() {

        List<RsStatusRapport> identStatuses = BestillingArenaforvalterStatusMapper.buildArenaStatusMap(ARENA_ORACLE_EXCEPTION_DOED);

        assertThat(identStatuses.getFirst().getStatuser().getFirst().getMelding(), is(equalTo("Feil: Person med fødselsnr 12345678912 kan ikke aktiveres fordi denne er død ved forsøk på aktivering med iverksatt 14a-vedtak.")));
        assertThat(identStatuses.getFirst().getStatuser().getFirst().getDetaljert().getFirst().getMiljo(), is(equalTo("q2")));
        assertThat(identStatuses.getFirst().getStatuser().getFirst().getDetaljert().getFirst().getIdenter(), containsInAnyOrder(IDENT));
    }

    @Test
    void formaterOracleExceptionTjeneste_OK() {

        List<RsStatusRapport> identStatuses = BestillingArenaforvalterStatusMapper.buildArenaStatusMap(ARENA_ORACLE_EXCEPTION_TJENESTE);

        assertThat(identStatuses.getFirst().getStatuser().getFirst().getMelding(), is(equalTo("Feil: IO error on POST request")));
        assertThat(identStatuses.getFirst().getStatuser().getFirst().getDetaljert().getFirst().getMiljo(), is(equalTo("q1")));
        assertThat(identStatuses.getFirst().getStatuser().getFirst().getDetaljert().getFirst().getIdenter(), containsInAnyOrder(IDENT));
    }
}