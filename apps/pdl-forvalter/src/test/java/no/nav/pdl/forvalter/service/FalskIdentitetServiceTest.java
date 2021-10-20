package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FalskIdentitetDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;

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

        var request = FalskIdentitetDTO.builder()
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                falskIdentitetService.validate(request));

        assertThat(exception.getMessage(), containsString("Falskidentitet: attribute erFalsk må oppgis"));
    }

    @Test
    void whenUgyldigDatoInterval_thenThrowExecption() {

        var request = FalskIdentitetDTO.builder()
                        .erFalsk(true)
                        .gyldigFraOgMed(LocalDate.of(2012, 04, 05).atStartOfDay())
                        .gyldigTilOgMed(LocalDate.of(2012, 04, 04).atStartOfDay())
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                falskIdentitetService.validate(request));

        assertThat(exception.getMessage(), containsString("Ugyldig datointervall: gyldigFom må være før gyldigTom"));
    }

    @Test
    void whenTooManyRettIdenitetStated_thenThrowExecption() {

        var request = FalskIdentitetDTO.builder()
                        .erFalsk(true)
                        .rettIdentitetErUkjent(true)
                        .rettIdentitetVedOpplysninger(new FalskIdentitetDTO.IdentifiserendeInformasjonDTO())
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                falskIdentitetService.validate(request));

        assertThat(exception.getMessage(), containsString("Falsk identitet: Maksimalt en av disse skal være satt: " +
                "rettIdentitetVedOpplysninger, rettIdentitetErUkjent, rettIdentitetVedIdentifikasjonsnummer eller nyFalskIdentitet"));
    }

    @Test
    void whenStatedPersonDoesNotExist_thenThrowExecption() {

        when(personRepository.existsByIdent(IDENT)).thenReturn(false);

        var request = FalskIdentitetDTO.builder()
                        .erFalsk(true)
                        .rettIdentitetVedIdentifikasjonsnummer(IDENT)
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                falskIdentitetService.validate(request));

        assertThat(exception.getMessage(), containsString(format("Oppgitt person for falsk identitet %s ikke funnet i database", IDENT)));
    }

    @Test
    void whenInvalidNameGiven_thenThrowExecption() {

        when(genererNavnServiceConsumer.verifyNavn(any(no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO.class))).thenReturn(false);

        var request = FalskIdentitetDTO.builder()
                        .erFalsk(true)
                        .rettIdentitetVedOpplysninger(FalskIdentitetDTO.IdentifiserendeInformasjonDTO.builder()
                                .personnavn(FalskIdentitetDTO.FalsktNavnDTO.builder().etternavn(INVALID_NAME).build())
                                .build())
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                falskIdentitetService.validate(request));

        assertThat(exception.getMessage(), containsString("Falsik identitet: Navn er ikke i liste over gyldige verdier"));
    }
}