package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskIdentifikasjonsnummerDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@ExtendWith(MockitoExtension.class)
class UtenlandsidentifikasjonsnummerServiceTest {

    @InjectMocks
    private UtenlandsidentifikasjonsnummerService utenlandsidentifikasjonsnummerService;

    @Test
    void whenIdentifikasjonsnummerIkkeIfylt_thenThrowExecption() {

        var request = UtenlandskIdentifikasjonsnummerDTO.builder()
                .isNew(true)
                .build();

        StepVerifier.create(
                        utenlandsidentifikasjonsnummerService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Utenlandsk identifikasjonsnummer må oppgis")));
    }

    @Test
    void whenOpphoertIkkeIfylt_thenThrowExecption() {

        var request = UtenlandskIdentifikasjonsnummerDTO.builder()
                .identifikasjonsnummer("1233123")
                .isNew(true)
                .build();

        StepVerifier.create(
                        utenlandsidentifikasjonsnummerService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Er utenlandsk identifikasjonsnummer opphørt? Må angis")));
    }

    @Test
    void whenUtstederlandIkkeIfylt_thenThrowExecption() {

        var request = UtenlandskIdentifikasjonsnummerDTO.builder()
                .identifikasjonsnummer("1233123")
                .opphoert(true)
                .isNew(true)
                .build();

        StepVerifier.create(
                        utenlandsidentifikasjonsnummerService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Utsteder land må oppgis")));
    }

    @Test
    void whenUtstederlandIllegalFormat_thenThrowExecption() {

        var request = UtenlandskIdentifikasjonsnummerDTO.builder()
                .identifikasjonsnummer("1233123")
                .opphoert(true)
                .utstederland("Uganda")
                .isNew(true)
                .build();

        StepVerifier.create(
                        utenlandsidentifikasjonsnummerService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Trebokstavers landkode er forventet på utstederland")));
    }
}