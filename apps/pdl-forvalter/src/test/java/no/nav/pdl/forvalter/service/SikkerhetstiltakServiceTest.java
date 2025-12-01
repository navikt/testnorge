package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO.Tiltakstype;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SikkerhetstiltakServiceTest {

    @InjectMocks
    private SikkerhetstiltakService sikkerhetstiltakService;

    @Test
    void whenTiltakstypeIsMissing_thenThrowExecption() {

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                sikkerhetstiltakService.validate(request));

        assertThat(exception.getMessage(), containsString("Sikkerhetstiltak: Tiltakstype må angis"));
    }

    @Test
    void whenBeskrivelseIsMissing_thenThrowExecption() {

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TOAN)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                sikkerhetstiltakService.validate(request));

        assertThat(exception.getMessage(), containsString("Sikkerhetstiltak: Beskrivelse må angis"));
    }

    @Test
    void whenGyldigFomIsMissing_thenThrowExecption() {

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TOAN)
                .beskrivelse("To ansatte ved samtale")
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                sikkerhetstiltakService.validate(request));

        assertThat(exception.getMessage(), containsString("Sikkerhetstiltak: GyldigFom må angis"));
    }

    @Test
    void whenGyldigTomIsMissing_thenThrowExecption() {

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TOAN)
                .beskrivelse("To ansatte ved samtale")
                .gyldigFraOgMed(LocalDateTime.now())
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                sikkerhetstiltakService.validate(request));

        assertThat(exception.getMessage(), containsString("Sikkerhetstiltak: GyldigTom må angis"));
    }

    @Test
    void whenKontaktpersonIsMissing_thenThrowExecption() {

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TOAN)
                .beskrivelse("To ansatte ved samtale")
                .gyldigFraOgMed(LocalDateTime.now())
                .gyldigTilOgMed(LocalDateTime.now().plusYears(1))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                sikkerhetstiltakService.validate(request));

        assertThat(exception.getMessage(), containsString("Sikkerhetstiltak: Personident og enhet må angis"));
    }

    @Test
    void whenPersonidentIsMissing_thenThrowExecption() {

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TOAN)
                .beskrivelse("To ansatte ved samtale")
                .gyldigFraOgMed(LocalDateTime.now())
                .gyldigTilOgMed(LocalDateTime.now().plusYears(1))
                .kontaktperson(new SikkerhetstiltakDTO.Kontaktperson())
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                sikkerhetstiltakService.validate(request));

        assertThat(exception.getMessage(), containsString("Sikkerhetstiltak: NAV personident må angis"));
    }

    @Test
    void whenEnhetIsMissing_thenThrowExecption() {

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TOAN)
                .beskrivelse("To ansatte ved samtale")
                .gyldigFraOgMed(LocalDateTime.now())
                .gyldigTilOgMed(LocalDateTime.now().plusYears(1))
                .kontaktperson(SikkerhetstiltakDTO.Kontaktperson.builder()
                        .personident("Z999999")
                        .build())
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                sikkerhetstiltakService.validate(request));

        assertThat(exception.getMessage(), containsString("Sikkerhetstiltak: Enhet må angis"));
    }

    @Test
    void whenUgyldigDatoIntervall_thenThrowExecption() {

        var request = SikkerhetstiltakDTO.builder()
                .isNew(true)
                .tiltakstype(Tiltakstype.TOAN)
                .beskrivelse("To ansatte ved samtale")
                .gyldigFraOgMed(LocalDateTime.now())
                .gyldigTilOgMed(LocalDateTime.now().minusDays(1))
                .kontaktperson(SikkerhetstiltakDTO.Kontaktperson.builder()
                        .personident("Z999999")
                        .enhet("0218")
                        .build())
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                sikkerhetstiltakService.validate(request));

        assertThat(exception.getMessage(), containsString("Sikkerhetstiltak: Ugyldig datointervall: gyldigFom må være før gyldigTom"));
    }
}