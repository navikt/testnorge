package no.nav.registre.testnorge.generersyntameldingservice.service;

import static no.nav.registre.testnorge.generersyntameldingservice.ResourceUtils.getResourceFileContent;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.testnorge.generersyntameldingservice.consumer.SyntAmeldingConsumer;
import no.nav.registre.testnorge.generersyntameldingservice.domain.ArbeidsforholdType;
import no.nav.testnav.libs.domain.dto.aareg.amelding.Arbeidsforhold;
import no.nav.testnav.libs.domain.dto.aareg.amelding.ArbeidsforholdPeriode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenererServiceTest {

    @Mock
    private SyntAmeldingConsumer syntAmeldingConsumer;

    @InjectMocks
    private GenererService service;

    private ObjectMapper objectMapper = new ObjectMapper();

    private TypeReference<List<Arbeidsforhold>> HISTORIKK_RESPONSE = new TypeReference<>() {
    };

    @Test
    void shouldGetCorrectAntallMeldinger() {
        assertThat(service.getAntallMeldinger(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 7))).isEqualTo(1);
        assertThat(service.getAntallMeldinger(LocalDate.of(2020, 1, 1), LocalDate.of(2021, 1, 1))).isEqualTo(13);
        assertThat(service.getAntallMeldinger(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 10, 15))).isEqualTo(10);
        assertThat(service.getAntallMeldinger(LocalDate.of(2020, 1, 1), LocalDate.of(2021, 7, 23))).isEqualTo(19);
        assertThat(service.getAntallMeldinger(LocalDate.of(2020, 1, 3), LocalDate.of(2020, 3, 1))).isEqualTo(3);
        assertThat(service.getAntallMeldinger(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 1))).isEqualTo(1);
    }

    @Test
    void shouldThrowExceptionWithIncorrectDates() {
        var startDato = LocalDate.of(2020, 3, 1);
        var sluttDato = LocalDate.of(2020, 2, 1);
        Assertions.assertThrows(
                ResponseStatusException.class,
                () -> service.getAntallMeldinger(startDato, sluttDato)
        );
    }

    @Test
    void shouldGenerateSingleAmelding() throws JsonProcessingException {
        var startDato = LocalDate.of(2020, 1, 1);
        var sluttDato = LocalDate.of(2020, 1, 20);

        var arbeidsforholdString = getResourceFileContent("files/synt_arbeidsforhold.json");
        var arbeidsforhold = objectMapper.readValue(arbeidsforholdString, Arbeidsforhold.class);

        when(syntAmeldingConsumer.getEnkeltArbeidsforhold(any(ArbeidsforholdPeriode.class), any(ArbeidsforholdType.class))).thenReturn(arbeidsforhold);

        var response = service.generateAmeldinger(startDato, sluttDato, ArbeidsforholdType.ordinaertArbeidsforhold);

        assertThat(response).hasSize(1);
    }

    @Test
    void shouldGenerateMultipleAmelding() throws JsonProcessingException {
        var startDato = LocalDate.of(2020, 1, 1);
        var sluttDato = LocalDate.of(2020, 7, 20);

        var arbeidsforholdString = getResourceFileContent("files/synt_arbeidsforhold.json");
        var arbeidsforhold = objectMapper.readValue(arbeidsforholdString, Arbeidsforhold.class);

        var historikkString = getResourceFileContent("files/synt_historikk.json");
        var historikk = objectMapper.readValue(historikkString, HISTORIKK_RESPONSE);

        when(syntAmeldingConsumer.getEnkeltArbeidsforhold(any(ArbeidsforholdPeriode.class), any(ArbeidsforholdType.class))).thenReturn(arbeidsforhold);
        when(syntAmeldingConsumer.getHistorikk(arbeidsforhold)).thenReturn(historikk);

        var response = service.generateAmeldinger(startDato, sluttDato, ArbeidsforholdType.ordinaertArbeidsforhold);

        assertThat(response).hasSize(7);
    }
}
