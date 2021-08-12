package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.consumer.OrganisasjonForvalterConsumer;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.AdressatDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.OrganisasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.KontaktpersonMedIdNummerDTO;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.KontaktpersonUtenIdNummerDTO;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.PdlSkifteform.OFFENTLIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.PersonNavnDTO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KontaktinformasjonForDoedsboServiceTest {

    private static final String IDENT = "12345678901";

    @Mock
    private PersonRepository personRepository;

    @Mock
    private GenererNavnServiceConsumer genererNavnServiceConsumer;

    @Mock
    private OrganisasjonForvalterConsumer organisasjonForvalterConsumer;

    @InjectMocks
    private KontaktinformasjonForDoedsboService kontaktinformasjonForDoedsboService;

    @Test
    void whenSkifteformIsMissing_thenThrowExecption() {

        var request = PersonDTO.builder()
                .kontaktinformasjonForDoedsbo(List.of(KontaktinformasjonForDoedsboDTO.builder()
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.convert(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: Skifteform må angis"));
    }

    @Test
    void whenAdressatIsMissing_thenThrowExecption() {

        var request = PersonDTO.builder()
                .kontaktinformasjonForDoedsbo(List.of(KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.convert(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: addressat må oppgis"));
    }

    @Test
    void whenMultipleAdressatExist_thenThrowExecption() {

        var request = PersonDTO.builder()
                .kontaktinformasjonForDoedsbo(List.of(KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .adressat(AdressatDTO.builder()
                                .advokatSomAdressat(new OrganisasjonDTO())
                                .organisasjonSomAdressat(new OrganisasjonDTO())
                                .build())
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.convert(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: kun en av disse " +
                "adressater skal oppgis: advokatSomAdressat, kontaktpersonMedIdNummerSomAdressat, " +
                "kontaktpersonUtenIdNummerSomAdressat eller organisasjonSomAdressat"));
    }

    @Test
    void whenAdressatMedIdnumberDoesNotExist_thenThrowExecption() {

        when(personRepository.existsByIdent(IDENT)).thenReturn(false);

        var request = PersonDTO.builder()
                .kontaktinformasjonForDoedsbo(List.of(KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .adressat(AdressatDTO.builder()
                                .kontaktpersonMedIdNummerSomAdressat(KontaktpersonMedIdNummerDTO.builder()
                                        .identifikasjonsnummer(IDENT)
                                        .build())
                                .build())
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.convert(request));

        assertThat(exception.getMessage(), containsString(format("KontaktinformasjonForDoedsbo: adressat med idnummer %s " +
                "ikke funnet i database", IDENT)));
    }

    @Test
    void whenAdressatUtenIdnumberLacksFoedselsdato_thenThrowExecption() {

        var request = PersonDTO.builder()
                .kontaktinformasjonForDoedsbo(List.of(KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .adressat(AdressatDTO.builder()
                                .kontaktpersonUtenIdNummerSomAdressat(new KontaktpersonUtenIdNummerDTO())
                                .build())
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.convert(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: adressat uten idnummer " +
                "behøver fødselsdato"));
    }

    @Test
    void whenAdressatUtenIdnumberHasInvalidPersonnavn_thenThrowExecption() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(false);

        var request = PersonDTO.builder()
                .kontaktinformasjonForDoedsbo(List.of(KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .adressat(AdressatDTO.builder()
                                .kontaktpersonUtenIdNummerSomAdressat(KontaktpersonUtenIdNummerDTO.builder()
                                        .foedselsdato(LocalDate.of(1984, 1, 1).atStartOfDay())
                                        .navn(PersonNavnDTO.builder().etternavn("Blæh").build())
                                        .build())
                                .build())
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.convert(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: adressat har ugyldig personnavn"));
    }

    @Test
    void whenAdressatAdvokatSomKontaktHasInvalidPersonnavn_thenThrowExecption() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(false);

        var request = PersonDTO.builder()
                .kontaktinformasjonForDoedsbo(List.of(KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .adressat(AdressatDTO.builder()
                                .advokatSomAdressat(OrganisasjonDTO.builder()
                                        .kontaktperson(PersonNavnDTO.builder().etternavn("Blæh").build())
                                        .build())
                                .build())
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.convert(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: adressat har ugyldig personnavn"));
    }

    @Test
    void whenOrganisasjonSomAddressatHasInvalidPersonnavn_thenThrowExecption() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(false);

        var request = PersonDTO.builder()
                .kontaktinformasjonForDoedsbo(List.of(KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .adressat(AdressatDTO.builder()
                                .organisasjonSomAdressat(OrganisasjonDTO.builder()
                                        .kontaktperson(PersonNavnDTO.builder().etternavn("Blæh").build())
                                        .build())
                                .build())
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.convert(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: adressat har ugyldig personnavn"));
    }

    @Test
    void whenOrganisasjonSomAddressatHasOrgNavnWithoutOrgNumber_thenThrowExecption() {

        var request = PersonDTO.builder()
                .kontaktinformasjonForDoedsbo(List.of(KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .adressat(AdressatDTO.builder()
                                .organisasjonSomAdressat(OrganisasjonDTO.builder()
                                        .organisasjonsnavn("Tada")
                                        .build())
                                .build())
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.convert(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: organisajonsnavn kan " +
                "ikke oppgis uten at organisasjonsnummer finnes"));
    }

    @Test
    void whenAdvokatSomAddressatHasNonExistingOrgNumber_thenThrowExecption() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(true);
        when(organisasjonForvalterConsumer.get(anyString())).thenReturn(new HashMap<>());

        var request = PersonDTO.builder()
                .kontaktinformasjonForDoedsbo(List.of(KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .adressat(AdressatDTO.builder()
                                .advokatSomAdressat(OrganisasjonDTO.builder()
                                        .kontaktperson(PersonNavnDTO.builder().etternavn("Blæh").build())
                                        .organisasjonsnummer("123456789")
                                        .build())
                                .build())
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.convert(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: organisajonsnummer er " +
                "tomt og/eller angitt organisasjon finnes ikke i miljø"));
    }

    @Test
    void whenAdvokatSomAddressatHasNonExistingOrgNavn_thenThrowExecption() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(true);
        when(organisasjonForvalterConsumer.get(anyString())).thenReturn(Map.of("q1",
                Map.of("organisasjonsnavn", "Toys")));

        var request = PersonDTO.builder()
                .kontaktinformasjonForDoedsbo(List.of(KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .adressat(AdressatDTO.builder()
                                .advokatSomAdressat(OrganisasjonDTO.builder()
                                        .kontaktperson(PersonNavnDTO.builder().etternavn("Blæh").build())
                                        .organisasjonsnummer("123456789")
                                        .organisasjonsnavn("Tull")
                                        .build())
                                .build())
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.convert(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: organisajonsnummer er tomt " +
                "og/eller angitt organisasjon finnes ikke i miljø"));
    }
}