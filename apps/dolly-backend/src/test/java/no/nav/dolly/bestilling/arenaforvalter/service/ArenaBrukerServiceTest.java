package no.nav.dolly.bestilling.arenaforvalter.service;

import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;

import java.util.Collections;

import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.core.Is.is;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ArenaBrukerServiceTest {

    @InjectMocks
    ArenaBrukerService arenaBrukerService;

    @Test
    void getBrukerStatus_URL_Exception() {
        var response = arenaBrukerService.getBrukerStatus(
                ArenaNyeBrukereResponse.builder()
                        .status(HttpStatus.OK)
                        .arbeidsokerList(Collections.emptyList())
                        .miljoe("t1")
                        .nyBrukerFeilList(Collections.singletonList(ArenaNyeBrukereResponse.NyBrukerFeilV1.builder()
                                .miljoe("t1")
                                .personident("12345678901")
                                .nyBrukerFeilstatus(ArenaNyeBrukereResponse.BrukerFeilstatus.AKTIVER_BRUKER)
                                .melding("exception type: class org.springframework.web.client.HttpServerErrorException, message: 555 User Defined Resource Error: \"<EOL>{<EOL>    \"code\": \"UserDefinedResourceError\",<EOL>    \"title\": \"User Defined Resource Error\",<EOL>    \"message\": \"The request could not be processed due to an error in a user defined resource\",<EOL>    \"o:errorCode\": \"ORDS-12345\",<EOL>    \"cause\": \"An error occurred when evaluating the SQL statement associated with this resource. SQL Error Code 12345, Error Message: ORA-12345: -20171: Tjeneste HentPerson returnerte feil: I/O error on POST request for \\\"https://pdl-api-q1.dev.intern.nav.no/graphql\\\": Read timed out; nested exception is java.net.SocketTimeoutException: Read timed out ved forsøk på aktivering med iverksatt 14a-vedtak.\\nORA-12345: ved \\\"SIAMO.SYNT_PERSON\\\", line 123\\nORA-12345: ved \\\"SIAMO.SYNT_PERSON\\\", line 123\\nORA-12345: ved \\\"SYNT_REST.BRUKEROPPFOLGING\\\", line 5\\nORA-12345: ved line 1\\n\",<EOL>    \"action\": \"Ask the user defined resource author to check the SQL statement is correctly formed and executes without error\",<EOL>    \"type\": \"tag:oracle.com,2020:error/UserDefinedResourceError\",<EOL>    \"instance\": \"tag:oracle.com,2020:ecid/1234567qwerty\"<EOL>}\"")
                                .build()))
                        .build()
        ).block();

        assertThat(response, is("Feil: IO error on POST request"));
    }

    @Test
    void getBrukerStatus_Date_Exception() {
        var response = arenaBrukerService.getBrukerStatus(
                ArenaNyeBrukereResponse.builder()
                        .status(HttpStatus.OK)
                        .arbeidsokerList(Collections.emptyList())
                        .miljoe("t1")
                        .nyBrukerFeilList(Collections.singletonList(ArenaNyeBrukereResponse.NyBrukerFeilV1.builder()
                                .miljoe("t1")
                                .personident("12345678901")
                                .nyBrukerFeilstatus(ArenaNyeBrukereResponse.BrukerFeilstatus.AKTIVER_BRUKER)
                                .melding("exception type: class org.springframework.web.client.HttpServerErrorException, message: 555 User Defined Resource Error: \"<EOL>{<EOL>    \"code\": \"UserDefinedResourceError\",<EOL>    \"title\": \"User Defined Resource Error\",<EOL>    \"message\": \"The request could not be processed due to an error in a user defined resource\",<EOL>    \"o:errorCode\": \"ORDS-12345\",<EOL>    \"cause\": \"An error occurred when evaluating the SQL statement associated with this resource. SQL Error Code 12345, Error Message: ORA-12345: Bruker kan ikke aktiveres fra dato 01.01.2023 da bruker tidligere har vært aktivert eller inaktivert fra en nyere dato.\\nORA-12345: ved \\\"SIAMO.SYNT_PERSON\\\", line 123\\nORA-12345: ved \\\"SIAMO.SYNT_PERSON\\\", line 12\\nORA-12345: ved \\\"SYNT_REST.BRUKEROPPFOLGING\\\", line 1\\nORA-12345: ved line 1\\n\",<EOL>    \"action\": \"Ask the user defined resource author to check the SQL statement is correctly formed and executes without error\",<EOL>    \"type\": \"tag:oracle.com,2020:error/UserDefinedResourceError\",<EOL>    \"instance\": \"tag:oracle.com,2020:ecid/qwertyu12345\"<EOL>}\"")
                                .build()))
                        .build()
        ).block();

        assertThat(response, is("Feil: Bruker kan ikke aktiveres fra dato 01.01.2023 da bruker tidligere har vært aktivert eller inaktivert fra en nyere dato."));
    }

    @Test
    void getBrukerStatus_Tjenestekall_Sats_Exception() {
        var response = arenaBrukerService.getBrukerStatus(
                ArenaNyeBrukereResponse.builder()
                        .status(HttpStatus.OK)
                        .arbeidsokerList(Collections.emptyList())
                        .miljoe("t1")
                        .nyBrukerFeilList(Collections.singletonList(ArenaNyeBrukereResponse.NyBrukerFeilV1.builder()
                                .miljoe("t1")
                                .personident("12345678901")
                                .nyBrukerFeilstatus(ArenaNyeBrukereResponse.BrukerFeilstatus.AKTIVER_BRUKER)
                                .melding("\"code\": \"UserDefinedResourceError\", \"title\": \"User Defined Resource Error\", \"message\": \"The request could not be processed due to an error in a user defined resource\", \"o:errorCode\": \"ORDS-12345\", \"cause\": \"An error occurred when evaluating the SQL statement associated with this resource. SQL Error Code 12345, Error Message: ORA-12345: Tjenestekall for beregning av sats feilet. &2\\nORA-12345: ved \\\"SIAMO.SYNT_DAGPENGER\\\", line 1234\\nORA-12345: ved \\\"SIAMO.SYNT_DAGPENGER\\\", line 1234\\nORA-12345: ved \\\"SIAMO.SYNT_DAGPENGER\\\", line 1234\\nORA-12345: ved \\\"SIAMO.SYNT_DAGPENGER\\\", line 1234\\nORA-12345: ved \\\"SYNT_REST.DAGPENGER\\\", line 12\\nORA-12345: ved line 1\\n\", \"action\": \"Ask the user defined resource author to check the SQL statement is correctly formed and executes without error\", \"type\": \"tag:oracle.com,2020:error/UserDefinedResourceError\", \"instance\": \"tag:oracle.com,2020:ecid/qwert12345\"")
                                .build()))
                        .build()
        ).block();

        assertThat(response, is("Feil: Tjenestekall for beregning av sats feilet."));
    }

    @Test
    void getBrukerStatus_Dagpenger_Overlapp_Exception() {
        var response = arenaBrukerService.getBrukerStatus(
                ArenaNyeBrukereResponse.builder()
                        .status(HttpStatus.OK)
                        .arbeidsokerList(Collections.emptyList())
                        .miljoe("t1")
                        .nyBrukerFeilList(Collections.singletonList(ArenaNyeBrukereResponse.NyBrukerFeilV1.builder()
                                .miljoe("t1")
                                .personident("12345678901")
                                .nyBrukerFeilstatus(ArenaNyeBrukereResponse.BrukerFeilstatus.AKTIVER_AAP)
                                .melding("code: UserDefinedResourceError; title: User Defined Resource Error; message: The request could not be processed due to an error in a user defined resource; o:errorCode: ORDS-12345; cause: An error occurred when evaluating the SQL statement associated with this resource. SQL Error Code 12345; Error Message: ORA-12345: Det finnes et overlappende vedtak om livsoppholdsytelse for denne perioden.\\\\nORA-12345: ved \\\\SIAMO.SYNT_DAGPENGER\\\\; line 1234\\\\nORA-12345: ved \\\\SIAMO.SYNT_DAGPENGER\\\\; line 1234\\\\nORA-12345: ved \\\\SIAMO.SYNT_DAGPENGER\\\\; line 1234\\\\nORA-12345: ved \\\\SYNT_REST.DAGPENGER\\\\; line 12\\\\nORA-12345: ved line 1\\\\n; action: Ask the user defined resource author to check the SQL statement is correctly formed and executes without error; type: tag:oracle.com;2020:error/UserDefinedResourceError; instance: tag:oracle.com;2020:ecid/qwert123")
                                .build()))
                        .build()
        ).block();

        assertThat(response, is("Feil: Det finnes et overlappende vedtak om livsoppholdsytelse for denne perioden."));
    }
}