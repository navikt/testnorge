package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.consumer.OrganisasjonForvalterConsumer;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.OrganisasjonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.KontaktpersonDTO;
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

        var request = KontaktinformasjonForDoedsboDTO.builder()
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.validate(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: Skifteform må angis"));
    }

    @Test
    void whenAdressatIsMissing_thenThrowExecption() {

        var request = KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.validate(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: kontakt må oppgis, enten advokatSomKontakt, personSomKontakt eller organisasjonSomKontakt"));
    }

    @Test
    void whenMultipleAdressatExist_thenThrowExecption() {

        var request = KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .advokatSomKontakt(new OrganisasjonDTO())
                        .organisasjonSomKontakt(new OrganisasjonDTO())
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.validate(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: kun en av disse kontakter skal oppgis: advokatSomKontakt, personSomKontakt eller organisasjonSomKontakt"));
    }

    @Test
    void whenAdressatMedIdnumberDoesNotExist_thenThrowExecption() {

        when(personRepository.existsByIdent(IDENT)).thenReturn(false);

        var request = KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .personSomKontakt(KontaktpersonDTO.builder()
                                .identifikasjonsnummer(IDENT)
                                .build())
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.validate(request));

        assertThat(exception.getMessage(), containsString(format("KontaktinformasjonForDoedsbo: personSomKontakt med identifikasjonsnummer %s " +
                "ikke funnet i database", IDENT)));
    }

    @Test
    void whenAdressatUtenIdnumberHasInvalidPersonnavn_thenThrowExecption() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(Mono.just(false));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .personSomKontakt(KontaktpersonDTO.builder()
                                .foedselsdato(LocalDate.of(1984, 1, 1).atStartOfDay())
                                .navn(PersonNavnDTO.builder().etternavn("Blæh").build())
                                .build())
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.validate(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: adressat har ugyldig personnavn"));
    }

    @Test
    void whenAdressatAdvokatSomKontaktHasInvalidPersonnavn_thenThrowExecption() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(Mono.just(false));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .advokatSomKontakt(OrganisasjonDTO.builder()
                                .kontaktperson(PersonNavnDTO.builder().etternavn("Blæh").build())
                                .build())
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.validate(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: adressat har ugyldig personnavn"));
    }

    @Test
    void whenOrganisasjonSomAddressatHasInvalidPersonnavn_thenThrowExecption() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(Mono.just(false));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .organisasjonSomKontakt(OrganisasjonDTO.builder()
                                .kontaktperson(PersonNavnDTO.builder().etternavn("Blæh").build())
                                .build())
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.validate(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: adressat har ugyldig personnavn"));
    }

    @Test
    void whenOrganisasjonSomAddressatHasOrgNavnWithoutOrgNumber_thenThrowExecption() {

        var request = KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .organisasjonSomKontakt(OrganisasjonDTO.builder()
                                .organisasjonsnavn("Tada")
                                .build())
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.validate(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: organisajonsnavn kan " +
                "ikke oppgis uten at organisasjonsnummer finnes"));
    }

    @Test
    void whenAdvokatSomAddressatHasNonExistingOrgNumber_thenThrowExecption() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(Mono.just(true));
        when(organisasjonForvalterConsumer.get(anyString())).thenReturn(Mono.just(new HashMap<>()));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .advokatSomKontakt(OrganisasjonDTO.builder()
                                .kontaktperson(PersonNavnDTO.builder().etternavn("Blæh").build())
                                .organisasjonsnummer("123456789")
                                .build())
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.validate(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: organisajonsnummer er " +
                "tomt og/eller angitt organisasjonsnummer/navn finnes ikke i miljø [q1|q2]"));
    }

    @Test
    void whenAdvokatSomAddressatHasNonExistingOrgNavn_thenThrowExecption() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(Mono.just(true));
        when(organisasjonForvalterConsumer.get(anyString())).thenReturn(Mono.just(Map.of("q1",
                Map.of("organisasjonsnavn", "Toys"))));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .advokatSomKontakt(OrganisasjonDTO.builder()
                                .kontaktperson(PersonNavnDTO.builder().etternavn("Blæh").build())
                                .organisasjonsnummer("123456789")
                                .organisasjonsnavn("Tull")
                                .build())
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                kontaktinformasjonForDoedsboService.validate(request));

        assertThat(exception.getMessage(), containsString("KontaktinformasjonForDoedsbo: organisajonsnummer er " +
                "tomt og/eller angitt organisasjonsnummer/navn finnes ikke i miljø [q1|q2]"));
    }
}