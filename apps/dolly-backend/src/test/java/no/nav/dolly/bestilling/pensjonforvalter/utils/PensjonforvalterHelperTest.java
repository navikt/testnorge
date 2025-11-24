package no.nav.dolly.bestilling.pensjonforvalter.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class PensjonforvalterHelperTest {

    @Spy
    private ErrorStatusDecoder errorStatusDecoder = new ErrorStatusDecoder(new ObjectMapper());

    @InjectMocks
    private PensjonforvalterHelper pensjonforvalterHelper;

    @ParameterizedTest
    @CsvSource(
            value = {
                    "{MESSAGE},REASON,Feil= MESSAGE",
                    "null,REASON,Feil= REASON",
                    "null,null,Feil= 500 INTERNAL_SERVER_ERROR"
            },
            nullValues = {
                    "null"
            }
    )
    void testGetError(String message, String reason, String expected) {

        var entry = PensjonforvalterResponse.ResponseEnvironment
                .builder()
                .miljo("ENV")
                .response(
                        PensjonforvalterResponse.Response
                                .builder()
                                .path("/test")
                                .message(message)
                                .httpStatus(
                                        PensjonforvalterResponse.HttpStatus
                                                .builder()
                                                .status(500)
                                                .reasonPhrase(reason)
                                                .build())
                                .build())
                .build();
        var error = pensjonforvalterHelper.getError(entry);
        assertThat(error, is(expected));
    }
}