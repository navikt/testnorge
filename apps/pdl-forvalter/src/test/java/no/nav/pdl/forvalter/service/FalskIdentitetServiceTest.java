package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.domain.PdlPerson;
import no.nav.pdl.forvalter.dto.RsFalskIdentitet;
import no.nav.pdl.forvalter.dto.RsNavn;
import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FalskIdentitetServiceTest {

    private static final String IDENT = "12345678901";
    private static final String INVALID_NAME = "UGYLDIG";

    @Mock
    private GenererNavnServiceConsumer genererNavnServiceConsumer;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private FalskIdentitetService falskIdentitetService;

    @Test
    void whenAttributeErFalskIsMissing_thenThrowExecption() {

        var request = List.of(RsFalskIdentitet.builder()
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                falskIdentitetService.convert(PdlPerson.builder()
                        .falskIdentitet((List<RsFalskIdentitet>) request)
                        .build()));

        assertThat(exception.getMessage(), containsString("Falskidentitet: attribute erFalsk må oppgis"));
    }

    @Test
    void whenUgyldigDatoInterval_thenThrowExecption() {

        var request = List.of(RsFalskIdentitet.builder()
                .erFalsk(true)
                .gyldigFom(LocalDate.of(2012, 04, 05).atStartOfDay())
                .gyldigTom(LocalDate.of(2012, 04, 04).atStartOfDay())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                falskIdentitetService.convert(PdlPerson.builder()
                        .falskIdentitet((List<RsFalskIdentitet>) request)
                        .build()));

        assertThat(exception.getMessage(), containsString("Ugyldig datointervall: gyldigFom må være før gyldigTom"));
    }

    @Test
    void whenTooManyRettIdenitetStated_thenThrowExecption() {

        var request = List.of(RsFalskIdentitet.builder()
                .erFalsk(true)
                .rettIdentitetErUkjent(true)
                .rettIdentitetVedOpplysninger(new RsFalskIdentitet.IdentifiserendeInformasjon())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                falskIdentitetService.convert(PdlPerson.builder()
                        .falskIdentitet((List<RsFalskIdentitet>) request)
                        .build()));

        assertThat(exception.getMessage(), containsString("Falsk identitet: Maksimalt en av disse skal være satt: " +
                "rettIdentitetVedOpplysninger, rettIdentitetErUkjent, rettIdentitetVedIdentifikasjonsnummer eller nyFalskIdentitet"));
    }

    @Test
    void whenStatedPersonDoesNotExist_thenThrowExecption() {

        when(personRepository.existsByIdent(IDENT)).thenReturn(false);

        var request = List.of(RsFalskIdentitet.builder()
                .erFalsk(true)
                .rettIdentitetVedIdentifikasjonsnummer(IDENT)
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                falskIdentitetService.convert(PdlPerson.builder()
                        .falskIdentitet((List<RsFalskIdentitet>) request)
                        .build()));

        assertThat(exception.getMessage(), containsString(format("Oppgitt person for falsk identitet %s ikke funnet i database", IDENT)));
    }

    @Test
    void whenStatsborgerskapDoesNotExist_thenThrowExecption() {

        var request = List.of(RsFalskIdentitet.builder()
                .erFalsk(true)
                .rettIdentitetVedOpplysninger(new RsFalskIdentitet.IdentifiserendeInformasjon())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                falskIdentitetService.convert(PdlPerson.builder()
                        .falskIdentitet((List<RsFalskIdentitet>) request)
                        .build()));

        assertThat(exception.getMessage(), containsString("Falsk identitet: statborgerskap må oppgis"));
    }

    @Test
    void whenInvalidNameGiven_thenThrowExecption() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(false);

        var request = List.of(RsFalskIdentitet.builder()
                .erFalsk(true)
                .rettIdentitetVedOpplysninger(RsFalskIdentitet.IdentifiserendeInformasjon.builder()
                        .personnavn(RsNavn.builder().etternavn(INVALID_NAME).build())
                        .build())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                falskIdentitetService.convert(PdlPerson.builder()
                        .falskIdentitet((List<RsFalskIdentitet>) request)
                        .build()));

        assertThat(exception.getMessage(), containsString("Falsik identitet: Navn er ikke i liste over gyldige verdier"));
    }
}