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
    void getBrukerStatus() {
        var response = arenaBrukerService.getBrukerStatus(
                ArenaNyeBrukereResponse.builder()
                        .status(HttpStatus.OK)
                        .arbeidsokerList(Collections.emptyList())
                        .miljoe("t1")
                        .nyBrukerFeilList(Collections.singletonList(ArenaNyeBrukereResponse.NyBrukerFeilV1.builder()
                                .miljoe("t1")
                                .personident("12345678901")
                                .nyBrukerFeilstatus(ArenaNyeBrukereResponse.BrukerFeilstatus.AKTIVER_BRUKER)
                                .melding("exception type: class org.springframework.web.client.HttpServerErrorException, message: 555 User Defined Resource Error: \"<EOL>{<EOL>    \"code\": \"UserDefinedResourceError\",<EOL>    \"title\": \"User Defined Resource Error\",<EOL>    \"message\": \"The request could not be processed due to an error in a user defined resource\",<EOL>    \"o:errorCode\": \"ORDS-25001\",<EOL>    \"cause\": \"An error occurred when evaluating the SQL statement associated with this resource. SQL Error Code 20999, Error Message: ORA-20999: -20171: Tjeneste HentPerson returnerte feil: I/O error on POST request for \\\"https://pdl-api-q1.dev.intern.nav.no/graphql\\\": Read timed out; nested exception is java.net.SocketTimeoutException: Read timed out ved forsøk på aktivering med iverksatt 14a-vedtak.\\nORA-06512: ved \\\"SIAMO.SYNT_PERSON\\\", line 206\\nORA-06512: ved \\\"SIAMO.SYNT_PERSON\\\", line 188\\nORA-06512: ved \\\"SYNT_REST.BRUKEROPPFOLGING\\\", line 5\\nORA-06512: ved line 1\\n\",<EOL>    \"action\": \"Ask the user defined resource author to check the SQL statement is correctly formed and executes without error\",<EOL>    \"type\": \"tag:oracle.com,2020:error/UserDefinedResourceError\",<EOL>    \"instance\": \"tag:oracle.com,2020:ecid/bIoCc_o8otfJRKbXGXhLdA\"<EOL>}\"")
                                .build()))
                        .build()
        ).block();

        assertThat(response, is("Feil: I/O error on POST request"));
    }
}