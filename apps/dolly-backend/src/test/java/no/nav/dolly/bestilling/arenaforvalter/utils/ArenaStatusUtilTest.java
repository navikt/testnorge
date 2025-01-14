package no.nav.dolly.bestilling.arenaforvalter.utils;

import no.nav.dolly.bestilling.arenaforvalter.dto.AapResponse;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeDagpengerResponse;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;

import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.core.Is.is;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ArenaStatusUtilTest {

    @MockitoBean
    private ErrorStatusDecoder errorStatusDecoder;

    @Test
    void getDagpengerStatus() {

        var response = ArenaStatusUtil.getDagpengerStatus(
                ArenaNyeDagpengerResponse.builder()
                        .status(HttpStatus.OK)
                        .nyeDagp(Collections.emptyList())
                        .miljoe("t1")
                        .nyeDagpFeilList(Collections.singletonList(ArenaNyeDagpengerResponse.NyDagpFeilV1.builder()
                                .miljoe("t1")
                                .personident("12345678901")
                                .melding("\"code\": \"UserDefinedResourceError\", \"title\": \"User Defined Resource Error\", \"message\": \"The request could not be processed due to an error in a user defined resource\", \"o:errorCode\": \"ORDS-00000\", \"cause\": \"An error occurred when evaluating the SQL statement associated with this resource. SQL Error Code 00000, Error Message: ORA-00000: Personen TESTESEN TEST har ingen meldeform\\nORA-00000: ved \\\"SIAMO.SYNT_DAGPENGER\\\", line 0000\\nORA-00000: ved \\\"SIAMO.VEDTAK_AS\\\", line 00\\nORA-00000: ved \\\"SIAMO.AU_FEIL\\\", line 000\\nORA-00000: ved \\\"SIAMO.AU_FEIL\\\", line 000\\nORA-00000: ved \\\"SIAMO.MK_MELDEKORT\\\", line 000\\nORA-00000: ingen data funnet\\nORA-00000: ved \\\"SIAMO.MK_MELDEKORT\\\", line 000\\nORA-00000: ved \\\"SIAMO.MK_MELDEKORT\\\", line 0000\\nORA-00000: ved \\\"SIAMO.MK_MELDESTATUS\\\", line 0000\\nORA-00000: ved \\\"SIAMO.VEDTAK_PAKKE\\\", line 0000\\nORA-00000: ved \\\"SIAMO.VEDTAK_AS\\\", line 00\\nORA-00000: feil ved utføring av triggeren 'SIAMO.VEDTAK_AS'\\nORA-00000: ved \\\"SIAMO.SYNT_VEDTAK\\\", line 000\\nORA-00000: ved \\\"SIAMO.SYNT_VEDTAK\\\", line 0000\\nORA-00000: ved \\\"SIAMO.SYNT_VEDTAK\\\", line 0000\\nORA-00000: ved \\\"SIAMO.SYNT_DAGPENGER\\\", line 0000\\nORA-00000: ved \\\"SYNT_REST.DAGPENGER\\\", line 00\\nORA-00000: ved line 0\\n\", \"action\": \"Ask the user defined resource author to check the SQL statement is correctly formed and executes without error\", \"type\": \"tag:oracle.com,0000:error/UserDefinedResourceError\", \"instance\": \"tag:oracle.com,0000:ecid/qwert12345\" }")
                                .build()))
                        .build()
                , errorStatusDecoder).block();

        assertThat(response, is("Feil: Personen TESTESEN TEST har ingen meldeform"));
    }

    @Test
    void getAapStatus() {

        var response = ArenaStatusUtil.getAapStatus(
                AapResponse.builder()
                        .status(HttpStatus.OK)
                        .nyeAap(Collections.emptyList())
                        .miljoe("t1")
                        .nyeAapFeilList(Collections.singletonList(AapResponse.NyAapFeilV1.builder()
                                .miljoe("t1")
                                .personident("12345678901")
                                .melding("\\\"code\\\": \\\"UserDefinedResourceError\\\", \\\"title\\\": \\\"User Defined Resource Error\\\", \\\"message\\\": \\\"The request could not be processed due to an error in a user defined resource\\\", \\\"o:errorCode\\\": \\\"ORDS-12345\\\", \\\"cause\\\": \\\"An error occurred when evaluating the SQL statement associated with this resource. SQL Error Code 12345, Error Message: ORA-12345: Tjenestekall for beregning av sats feilet. &2\\\\nORA-12345: ved \\\\\\\"SIAMO.SYNT_DAGPENGER\\\\\\\", line 1234\\\\nORA-12345: ved \\\\\\\"SIAMO.SYNT_DAGPENGER\\\\\\\", line 1234\\\\nORA-12345: ved \\\\\\\"SIAMO.SYNT_DAGPENGER\\\\\\\", line 1234\\\\nORA-12345: ved \\\\\\\"SIAMO.SYNT_DAGPENGER\\\\\\\", line 1234\\\\nORA-12345: ved \\\\\\\"SYNT_REST.DAGPENGER\\\\\\\", line 12\\\\nORA-12345: ved line 1\\\\n\\\", \\\"action\\\": \\\"Ask the user defined resource author to check the SQL statement is correctly formed and executes without error\\\", \\\"type\\\": \\\"tag:oracle.com,2020:error/UserDefinedResourceError\\\", \\\"instance\\\": \\\"tag:oracle.com,2020:ecid/qwert12345\\\"")
                                .build()))
                        .build()
                , errorStatusDecoder).block();

        assertThat(response, is("Feil: Tjenestekall for beregning av sats feilet."));
    }
}