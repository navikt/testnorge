package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.consumer.OrganisasjonForvalterConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.KontaktinformasjonForDoedsboAdresse;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.OrganisasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.KontaktpersonDTO;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.PdlSkifteform.ANNET;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.PdlSkifteform.OFFENTLIG;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.PersonNavnDTO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KontaktinformasjonForDoedsboServiceTest {

    private static final String IDENT = "12345678901";
    private static final String HOVEDPERSON_IDENT = "98765432100";

    @Mock
    private PersonRepository personRepository;

    @Mock
    private GenererNavnServiceConsumer genererNavnServiceConsumer;

    @Mock
    private OrganisasjonForvalterConsumer organisasjonForvalterConsumer;

    @Mock
    private CreatePersonService createPersonService;

    @Mock
    private RelasjonService relasjonService;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private AdresseServiceConsumer adresseServiceConsumer;

    @Mock
    private EnkelAdresseService enkelAdresseService;

    @Mock
    private KodeverkConsumer kodeverkConsumer;

    @InjectMocks
    private KontaktinformasjonForDoedsboService kontaktinformasjonForDoedsboService;

    // --- validate: skifteform ---

    @Test
    void shouldRejectMissingSkifteform() {

        var request = KontaktinformasjonForDoedsboDTO.builder()
                        .isNew(true)
                        .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyErrorSatisfies(throwable -> {
                    assertThat(throwable instanceof InvalidRequestException, is(true));
                    assertThat(throwable.getMessage(), containsString("KontaktinformasjonForDoedsbo: Skifteform må angis"));
                });
    }

    // --- validate: kontakt count ---

    @Test
    void shouldRejectMissingAdressat() {

        var request = KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .isNew(true)
                        .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(),
                                containsString("KontaktinformasjonForDoedsbo: kontakt må oppgis, enten advokatSomKontakt, " +
                                               "personSomKontakt eller organisasjonSomKontakt")));
    }

    @Test
    void shouldRejectMultipleAdressater() {

        var request = KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .advokatSomKontakt(new OrganisasjonDTO())
                        .organisasjonSomKontakt(new OrganisasjonDTO())
                        .isNew(true)
                        .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("KontaktinformasjonForDoedsbo: kun en av disse kontakter skal oppgis: " +
                                                                          "advokatSomKontakt, personSomKontakt eller organisasjonSomKontakt")));
    }

    @Test
    void shouldRejectAllThreeAdressater() {

        var request = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .advokatSomKontakt(new OrganisasjonDTO())
                .personSomKontakt(KontaktpersonDTO.builder()
                        .identifikasjonsnummer(IDENT)
                        .build())
                .organisasjonSomKontakt(new OrganisasjonDTO())
                .isNew(true)
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("kun en av disse kontakter skal oppgis")));
    }

    @Test
    void shouldRejectAdvokatAndPersonSomKontakt() {

        var request = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .advokatSomKontakt(new OrganisasjonDTO())
                .personSomKontakt(KontaktpersonDTO.builder()
                        .identifikasjonsnummer(IDENT)
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("kun en av disse kontakter skal oppgis")));
    }

    // --- validate: ambiguous person ---

    @Test
    void shouldRejectPersonSomKontaktWithBothIdentAndFoedselsdato() {

        var request = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .personSomKontakt(KontaktpersonDTO.builder()
                        .identifikasjonsnummer(IDENT)
                        .foedselsdato(LocalDate.of(1984, 1, 1).atStartOfDay())
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyErrorSatisfies(throwable -> {
                    assertThat(throwable instanceof InvalidRequestException, is(true));
                    assertThat(throwable.getMessage(), containsString("personSomKontakt må angi kun én av identifikasjonsnummer eller fødselsdato"));
                });
    }

    // --- validate: person ident ---

    @Test
    void shouldRejectNonExistingPersonIdent() {

        when(personRepository.existsByIdent(IDENT)).thenReturn(Mono.just(false));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .personSomKontakt(KontaktpersonDTO.builder()
                                .identifikasjonsnummer(IDENT)
                                .build())
                        .isNew(true)
                        .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString(format("KontaktinformasjonForDoedsbo: personSomKontakt med identifikasjonsnummer %s " +
                                "ikke funnet i database", IDENT))));
    }

    @Test
    void shouldAcceptExistingPersonIdent() {

        when(personRepository.existsByIdent(IDENT)).thenReturn(Mono.just(true));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .personSomKontakt(KontaktpersonDTO.builder()
                        .identifikasjonsnummer(IDENT)
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyComplete();
    }

    // --- validate: personnavn ---

    @Test
    void shouldRejectInvalidPersonnavnOnPersonSomKontakt() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(Mono.just(false));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .personSomKontakt(KontaktpersonDTO.builder()
                        .foedselsdato(LocalDate.of(1984, 1, 1).atStartOfDay())
                        .navn(PersonNavnDTO.builder().etternavn("Blæh").build())
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("KontaktinformasjonForDoedsbo: adressat har ugyldig personnavn")));
    }

    @Test
    void shouldRejectInvalidPersonnavnOnAdvokatSomKontakt() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(Mono.just(false));
        when(organisasjonForvalterConsumer.getOrganisasjoner(anyString())).thenReturn(Mono.just(Map.of("q1",Map.of("123456789", "Toys"))));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .advokatSomKontakt(OrganisasjonDTO.builder()
                        .organisasjonsnummer("123456789")
                        .kontaktperson(PersonNavnDTO.builder().etternavn("Blæh").build())
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("KontaktinformasjonForDoedsbo: adressat har ugyldig personnavn")));
    }

    @Test
    void shouldRejectInvalidPersonnavnOnOrganisasjonSomKontakt() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(Mono.just(false));
        when(organisasjonForvalterConsumer.getOrganisasjoner(anyString())).thenReturn(Mono.just(Map.of("q1",Map.of("123456789", "Toys"))));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .organisasjonSomKontakt(OrganisasjonDTO.builder()
                                .organisasjonsnummer("123456789")
                                .kontaktperson(PersonNavnDTO.builder().etternavn("Blæh").build())
                                .build())
                        .isNew(true)
                        .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("KontaktinformasjonForDoedsbo: adressat har ugyldig personnavn")));
    }

    @Test
    void shouldAcceptValidPersonnavnOnPersonSomKontakt() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(Mono.just(true));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .personSomKontakt(KontaktpersonDTO.builder()
                        .foedselsdato(LocalDate.of(1984, 1, 1).atStartOfDay())
                        .navn(PersonNavnDTO.builder()
                                .fornavn("Gull")
                                .etternavn("Fjansen")
                                .build())
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyComplete();
    }

    @Test
    void shouldAcceptAdvokatSomKontaktWithNoKontaktperson() {

        when(organisasjonForvalterConsumer.getOrganisasjoner(anyString()))
                .thenReturn(Mono.just(Map.of("q1", Map.of("organisasjonsnavn", "Advokat AS"))));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .advokatSomKontakt(OrganisasjonDTO.builder()
                        .organisasjonsnummer("123456789")
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyComplete();
    }

    @Test
    void shouldAcceptOrganisasjonSomKontaktWithNoKontaktperson() {

        when(organisasjonForvalterConsumer.getOrganisasjoner(anyString()))
                .thenReturn(Mono.just(Map.of("q1", Map.of("organisasjonsnavn", "Firma AS"))));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .organisasjonSomKontakt(OrganisasjonDTO.builder()
                        .organisasjonsnummer("123456789")
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyComplete();
    }

    @Test
    void shouldAcceptPersonSomKontaktWithBlankNames() {

        var request = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .personSomKontakt(KontaktpersonDTO.builder()
                        .foedselsdato(LocalDate.of(1984, 1, 1).atStartOfDay())
                        .navn(PersonNavnDTO.builder().build())
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyComplete();
    }

    // --- validate: organisasjon ---

    @Test
    void shouldRejectOrgNavnWithoutOrgNumberOnOrganisasjon() {

        var request = KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .organisasjonSomKontakt(OrganisasjonDTO.builder()
                                .organisasjonsnavn("Tada")
                                .build())
                        .isNew(true)
                        .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("KontaktinformasjonForDoedsbo: organisajonsnavn kan " +
                                "ikke oppgis uten at organisasjonsnummer finnes")));
    }

    @Test
    void shouldRejectOrgNavnWithoutOrgNumberOnAdvokat() {

        var request = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .advokatSomKontakt(OrganisasjonDTO.builder()
                        .organisasjonsnavn("Advokat Firma")
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("organisajonsnavn kan ikke oppgis uten at organisasjonsnummer finnes")));
    }

    @Test
    void shouldRejectAdvokatWithNonExistingOrgNumber() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(Mono.just(true));
        when(organisasjonForvalterConsumer.getOrganisasjoner(anyString())).thenReturn(Mono.just(new HashMap<>()));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                        .skifteform(OFFENTLIG)
                        .advokatSomKontakt(OrganisasjonDTO.builder()
                                .kontaktperson(PersonNavnDTO.builder().etternavn("Blæh").build())
                                .organisasjonsnummer("123456789")
                                .build())
                        .isNew(true)
                        .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("KontaktinformasjonForDoedsbo: organisajonsnummer er " +
                                "tomt og/eller angitt organisasjonsnummer/navn finnes ikke i miljø [q1|q2]")));
    }

    @Test
    void shouldRejectAdvokatWithNonMatchingOrgNavn() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(Mono.just(true));
        when(organisasjonForvalterConsumer.getOrganisasjoner(anyString())).thenReturn(Mono.just(Map.of("q1",
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

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("KontaktinformasjonForDoedsbo: organisajonsnummer er " +
                                "tomt og/eller angitt organisasjonsnummer/navn finnes ikke i miljø [q1|q2]")));
    }

    @Test
    void shouldRejectOrganisasjonWithNonExistingOrgNumber() {

        when(organisasjonForvalterConsumer.getOrganisasjoner(anyString())).thenReturn(Mono.just(new HashMap<>()));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .organisasjonSomKontakt(OrganisasjonDTO.builder()
                        .organisasjonsnummer("999999999")
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("organisajonsnummer er tomt og/eller angitt organisasjonsnummer/navn finnes ikke")));
    }

    @Test
    void shouldRejectAdvokatWithEmptyOrgNumber() {

        var request = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .advokatSomKontakt(OrganisasjonDTO.builder()
                        .organisasjonsnummer("")
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("organisajonsnummer er tomt og/eller angitt organisasjonsnummer/navn finnes ikke")));
    }

    @Test
    void shouldAcceptAdvokatWithMatchingOrgInMiljoe() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(Mono.just(true));
        when(organisasjonForvalterConsumer.getOrganisasjoner(anyString()))
                .thenReturn(Mono.just(Map.of("q1", Map.of("organisasjonsnavn", "Advokat AS"))));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .advokatSomKontakt(OrganisasjonDTO.builder()
                        .organisasjonsnummer("123456789")
                        .organisasjonsnavn("Advokat AS")
                        .kontaktperson(PersonNavnDTO.builder()
                                .fornavn("Ola")
                                .etternavn("Nordmann")
                                .build())
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyComplete();
    }

    @Test
    void shouldAcceptOrganisasjonWithMatchingOrgInMiljoe() {

        when(organisasjonForvalterConsumer.getOrganisasjoner(anyString()))
                .thenReturn(Mono.just(Map.of("q2", Map.of("organisasjonsnavn", "Firma AS"))));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(ANNET)
                .organisasjonSomKontakt(OrganisasjonDTO.builder()
                        .organisasjonsnummer("987654321")
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyComplete();
    }

    @Test
    void shouldRejectOrgFoundOnlyInNonQ1Q2Miljoe() {

        when(organisasjonForvalterConsumer.getOrganisasjoner(anyString()))
                .thenReturn(Mono.just(Map.of("t1", Map.of("organisasjonsnavn", "Firma AS"))));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .organisasjonSomKontakt(OrganisasjonDTO.builder()
                        .organisasjonsnummer("987654321")
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("organisajonsnummer er tomt og/eller angitt organisasjonsnummer/navn finnes ikke")));
    }

    // --- validate: skifteform ANNET ---

    @Test
    void shouldAcceptSkifteformAnnet() {

        when(personRepository.existsByIdent(IDENT)).thenReturn(Mono.just(true));

        var request = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(ANNET)
                .personSomKontakt(KontaktpersonDTO.builder()
                        .identifikasjonsnummer(IDENT)
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.validate(request))
                .verifyComplete();
    }

    // --- convert ---

    @Test
    void shouldSkipNonNewEntries() {

        var kontaktinfo = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .personSomKontakt(KontaktpersonDTO.builder()
                        .identifikasjonsnummer(IDENT)
                        .build())
                .isNew(false)
                .build();

        var dbPerson = DbPerson.builder()
                .ident(HOVEDPERSON_IDENT)
                .person(PersonDTO.builder()
                        .ident(HOVEDPERSON_IDENT)
                        .kontaktinformasjonForDoedsbo(new ArrayList<>(List.of(kontaktinfo)))
                        .build())
                .build();

        StepVerifier.create(kontaktinformasjonForDoedsboService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(result.getIdent(), is(equalTo(HOVEDPERSON_IDENT)));
                    verify(kodeverkConsumer, never()).getPoststedNavn();
                })
                .verifyComplete();
    }

    @Test
    void shouldSetAttestutstedelsesdatoWhenNull() {

        var kontaktinfo = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .personSomKontakt(KontaktpersonDTO.builder()
                        .identifikasjonsnummer(IDENT)
                        .build())
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .ident(HOVEDPERSON_IDENT)
                .person(PersonDTO.builder()
                        .ident(HOVEDPERSON_IDENT)
                        .kontaktinformasjonForDoedsbo(new ArrayList<>(List.of(kontaktinfo)))
                        .build())
                .build();

        when(kodeverkConsumer.getPoststedNavn()).thenReturn(Mono.just(new HashMap<>()));
        when(mapperFacade.map(any(KontaktinformasjonForDoedsboDTO.class), eq(KontaktinformasjonForDoedsboDTO.class)))
                .thenReturn(KontaktinformasjonForDoedsboDTO.builder().build());

        StepVerifier.create(kontaktinformasjonForDoedsboService.convert(dbPerson))
                .assertNext(result ->
                        assertThat(result.getPerson().getKontaktinformasjonForDoedsbo().getFirst().getAttestutstedelsesdato(),
                                is(notNullValue())))
                .verifyComplete();
    }

    @Test
    void shouldKeepExistingAttestutstedelsesdato() {

        var dato = LocalDateTime.of(2020, 3, 15, 0, 0);

        var kontaktinfo = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .attestutstedelsesdato(dato)
                .personSomKontakt(KontaktpersonDTO.builder()
                        .identifikasjonsnummer(IDENT)
                        .build())
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .ident(HOVEDPERSON_IDENT)
                .person(PersonDTO.builder()
                        .ident(HOVEDPERSON_IDENT)
                        .kontaktinformasjonForDoedsbo(new ArrayList<>(List.of(kontaktinfo)))
                        .build())
                .build();

        when(kodeverkConsumer.getPoststedNavn()).thenReturn(Mono.just(new HashMap<>()));
        when(mapperFacade.map(any(KontaktinformasjonForDoedsboDTO.class), eq(KontaktinformasjonForDoedsboDTO.class)))
                .thenReturn(KontaktinformasjonForDoedsboDTO.builder().build());

        StepVerifier.create(kontaktinformasjonForDoedsboService.convert(dbPerson))
                .assertNext(result ->
                        assertThat(result.getPerson().getKontaktinformasjonForDoedsbo().getFirst().getAttestutstedelsesdato(),
                                is(equalTo(dato))))
                .verifyComplete();
    }

    @Test
    void shouldSetEksisterendePersonTrueWhenIdentIsPresent() {

        var kontaktinfo = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .personSomKontakt(KontaktpersonDTO.builder()
                        .identifikasjonsnummer(IDENT)
                        .build())
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .ident(HOVEDPERSON_IDENT)
                .person(PersonDTO.builder()
                        .ident(HOVEDPERSON_IDENT)
                        .kontaktinformasjonForDoedsbo(new ArrayList<>(List.of(kontaktinfo)))
                        .build())
                .build();

        when(kodeverkConsumer.getPoststedNavn()).thenReturn(Mono.just(new HashMap<>()));
        when(mapperFacade.map(any(KontaktinformasjonForDoedsboDTO.class), eq(KontaktinformasjonForDoedsboDTO.class)))
                .thenReturn(KontaktinformasjonForDoedsboDTO.builder().build());

        StepVerifier.create(kontaktinformasjonForDoedsboService.convert(dbPerson))
                .assertNext(result ->
                        assertThat(result.getPerson().getKontaktinformasjonForDoedsbo().getFirst()
                                .getPersonSomKontakt().isEksisterendePerson(), is(true)))
                .verifyComplete();
    }

    @Test
    void shouldFetchPersonAdresseForPersonSomKontaktWithIdent() {

        var bostedadresse = BostedadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder().kommunenummer("0301").build())
                .id(1)
                .build();

        var kontaktPerson = DbPerson.builder()
                .ident(IDENT)
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .bostedsadresse(new ArrayList<>(List.of(bostedadresse)))
                        .build())
                .build();

        var kontaktinfo = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .personSomKontakt(KontaktpersonDTO.builder()
                        .identifikasjonsnummer(IDENT)
                        .build())
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .ident(HOVEDPERSON_IDENT)
                .person(PersonDTO.builder()
                        .ident(HOVEDPERSON_IDENT)
                        .kontaktinformasjonForDoedsbo(new ArrayList<>(List.of(kontaktinfo)))
                        .build())
                .build();

        var mappedAdresse = KontaktinformasjonForDoedsboAdresse.builder()
                .postnummer("0301")
                .build();

        when(kodeverkConsumer.getPoststedNavn()).thenReturn(Mono.just(new HashMap<>()));
        when(mapperFacade.map(any(KontaktinformasjonForDoedsboDTO.class), eq(KontaktinformasjonForDoedsboDTO.class)))
                .thenReturn(KontaktinformasjonForDoedsboDTO.builder().build());
        when(personRepository.findByIdent(IDENT)).thenReturn(Mono.just(kontaktPerson));
        when(mapperFacade.map(any(BostedadresseDTO.class), eq(KontaktinformasjonForDoedsboAdresse.class)))
                .thenReturn(mappedAdresse);

        StepVerifier.create(kontaktinformasjonForDoedsboService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(result.getPerson().getKontaktinformasjonForDoedsbo().getFirst().getAdresse(), is(notNullValue()));
                    verify(personRepository).findByIdent(IDENT);
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleAdvokatSomKontakt() {

        var kontaktinfo = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .advokatSomKontakt(OrganisasjonDTO.builder()
                        .organisasjonsnummer("123456789")
                        .kontaktperson(PersonNavnDTO.builder()
                                .fornavn("Ola")
                                .etternavn("Nordmann")
                                .build())
                        .build())
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .ident(HOVEDPERSON_IDENT)
                .person(PersonDTO.builder()
                        .ident(HOVEDPERSON_IDENT)
                        .kontaktinformasjonForDoedsbo(new ArrayList<>(List.of(kontaktinfo)))
                        .build())
                .build();

        when(kodeverkConsumer.getPoststedNavn()).thenReturn(Mono.just(new HashMap<>()));
        when(mapperFacade.map(any(KontaktinformasjonForDoedsboDTO.class), eq(KontaktinformasjonForDoedsboDTO.class)))
                .thenReturn(KontaktinformasjonForDoedsboDTO.builder().build());
        when(organisasjonForvalterConsumer.getOrganisasjoner("123456789"))
                .thenReturn(Mono.just(Map.of("q1", Map.of(
                        "organisasjonsnavn", "Advokat AS",
                        "adresser", List.of()))));

        StepVerifier.create(kontaktinformasjonForDoedsboService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(result.getPerson().getKontaktinformasjonForDoedsbo().getFirst()
                            .getAdvokatSomKontakt().getOrganisasjonsnavn(), is(equalTo("Advokat AS")));
                    verify(organisasjonForvalterConsumer).getOrganisasjoner("123456789");
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleOrganisasjonSomKontakt() {

        var kontaktinfo = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .organisasjonSomKontakt(OrganisasjonDTO.builder()
                        .organisasjonsnummer("987654321")
                        .kontaktperson(PersonNavnDTO.builder()
                                .fornavn("Kari")
                                .etternavn("Nordmann")
                                .build())
                        .build())
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .ident(HOVEDPERSON_IDENT)
                .person(PersonDTO.builder()
                        .ident(HOVEDPERSON_IDENT)
                        .kontaktinformasjonForDoedsbo(new ArrayList<>(List.of(kontaktinfo)))
                        .build())
                .build();

        when(kodeverkConsumer.getPoststedNavn()).thenReturn(Mono.just(new HashMap<>()));
        when(mapperFacade.map(any(KontaktinformasjonForDoedsboDTO.class), eq(KontaktinformasjonForDoedsboDTO.class)))
                .thenReturn(KontaktinformasjonForDoedsboDTO.builder().build());
        when(organisasjonForvalterConsumer.getOrganisasjoner("987654321"))
                .thenReturn(Mono.just(Map.of("q1", Map.of(
                        "organisasjonsnavn", "Firma AS",
                        "adresser", List.of()))));

        StepVerifier.create(kontaktinformasjonForDoedsboService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(result.getPerson().getKontaktinformasjonForDoedsbo().getFirst()
                            .getOrganisasjonSomKontakt().getOrganisasjonsnavn(), is(equalTo("Firma AS")));
                    verify(organisasjonForvalterConsumer).getOrganisasjoner("987654321");
                })
                .verifyComplete();
    }

    @Test
    void shouldGenerateNameWhenPersonnavnIsMissing() {

        var kontaktinfo = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .advokatSomKontakt(OrganisasjonDTO.builder()
                        .organisasjonsnummer("123456789")
                        .build())
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .ident(HOVEDPERSON_IDENT)
                .person(PersonDTO.builder()
                        .ident(HOVEDPERSON_IDENT)
                        .kontaktinformasjonForDoedsbo(new ArrayList<>(List.of(kontaktinfo)))
                        .build())
                .build();

        when(kodeverkConsumer.getPoststedNavn()).thenReturn(Mono.just(new HashMap<>()));
        when(mapperFacade.map(any(KontaktinformasjonForDoedsboDTO.class), eq(KontaktinformasjonForDoedsboDTO.class)))
                .thenReturn(KontaktinformasjonForDoedsboDTO.builder().build());
        when(organisasjonForvalterConsumer.getOrganisasjoner("123456789"))
                .thenReturn(Mono.just(Map.of("q1", Map.of(
                        "organisasjonsnavn", "Advokat AS",
                        "adresser", List.of()))));
        when(genererNavnServiceConsumer.getNavn()).thenReturn(Mono.just(NavnDTO.builder()
                .adjektiv("Generert")
                .substantiv("Navn")
                .build()));

        StepVerifier.create(kontaktinformasjonForDoedsboService.convert(dbPerson))
                .assertNext(result -> {
                    var kontaktperson = result.getPerson().getKontaktinformasjonForDoedsbo().getFirst()
                            .getAdvokatSomKontakt().getKontaktperson();
                    assertThat(kontaktperson.getFornavn(), is(equalTo("Generert")));
                    assertThat(kontaktperson.getEtternavn(), is(equalTo("Navn")));
                    assertThat(kontaktperson.getMellomnavn(), is(nullValue()));
                    verify(genererNavnServiceConsumer).getNavn();
                })
                .verifyComplete();
    }

    @Test
    void shouldGenerateNameWithMellomnavnWhenFlagIsSet() {

        var kontaktinfo = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .organisasjonSomKontakt(OrganisasjonDTO.builder()
                        .organisasjonsnummer("123456789")
                        .kontaktperson(PersonNavnDTO.builder()
                                .hasMellomnavn(true)
                                .build())
                        .build())
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .ident(HOVEDPERSON_IDENT)
                .person(PersonDTO.builder()
                        .ident(HOVEDPERSON_IDENT)
                        .kontaktinformasjonForDoedsbo(new ArrayList<>(List.of(kontaktinfo)))
                        .build())
                .build();

        when(kodeverkConsumer.getPoststedNavn()).thenReturn(Mono.just(new HashMap<>()));
        when(mapperFacade.map(any(KontaktinformasjonForDoedsboDTO.class), eq(KontaktinformasjonForDoedsboDTO.class)))
                .thenReturn(KontaktinformasjonForDoedsboDTO.builder().build());
        when(organisasjonForvalterConsumer.getOrganisasjoner("123456789"))
                .thenReturn(Mono.just(Map.of("q1", Map.of(
                        "organisasjonsnavn", "Firma AS",
                        "adresser", List.of()))));
        when(genererNavnServiceConsumer.getNavn()).thenReturn(Mono.just(NavnDTO.builder()
                .adjektiv("Fornavn")
                .adverb("Mellom")
                .substantiv("Etternavn")
                .build()));

        StepVerifier.create(kontaktinformasjonForDoedsboService.convert(dbPerson))
                .assertNext(result -> {
                    var kontaktperson = result.getPerson().getKontaktinformasjonForDoedsbo().getFirst()
                            .getOrganisasjonSomKontakt().getKontaktperson();
                    assertThat(kontaktperson.getFornavn(), is(equalTo("Fornavn")));
                    assertThat(kontaktperson.getMellomnavn(), is(equalTo("Mellom")));
                    assertThat(kontaktperson.getEtternavn(), is(equalTo("Etternavn")));
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnSameDbPersonInstance() {

        var kontaktinfo = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .personSomKontakt(KontaktpersonDTO.builder()
                        .identifikasjonsnummer(IDENT)
                        .build())
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .ident(HOVEDPERSON_IDENT)
                .person(PersonDTO.builder()
                        .ident(HOVEDPERSON_IDENT)
                        .kontaktinformasjonForDoedsbo(new ArrayList<>(List.of(kontaktinfo)))
                        .build())
                .build();

        when(kodeverkConsumer.getPoststedNavn()).thenReturn(Mono.just(new HashMap<>()));
        when(mapperFacade.map(any(KontaktinformasjonForDoedsboDTO.class), eq(KontaktinformasjonForDoedsboDTO.class)))
                .thenReturn(KontaktinformasjonForDoedsboDTO.builder().build());

        StepVerifier.create(kontaktinformasjonForDoedsboService.convert(dbPerson))
                .assertNext(result -> assertThat(result, is(dbPerson)))
                .verifyComplete();
    }

    @Test
    void shouldUseProvidedAdresseWhenAdresselinje1IsSet() {

        var providedAdresse = KontaktinformasjonForDoedsboAdresse.builder()
                .adresselinje1("Storgata 1")
                .postnummer("0101")
                .poststedsnavn("Oslo")
                .build();

        var kontaktinfo = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .adresse(providedAdresse)
                .personSomKontakt(KontaktpersonDTO.builder()
                        .identifikasjonsnummer(IDENT)
                        .build())
                .isNew(true)
                .build();

        var dbPerson = DbPerson.builder()
                .ident(HOVEDPERSON_IDENT)
                .person(PersonDTO.builder()
                        .ident(HOVEDPERSON_IDENT)
                        .kontaktinformasjonForDoedsbo(new ArrayList<>(List.of(kontaktinfo)))
                        .build())
                .build();

        when(kodeverkConsumer.getPoststedNavn()).thenReturn(Mono.just(new HashMap<>()));
        when(mapperFacade.map(any(KontaktinformasjonForDoedsboDTO.class), eq(KontaktinformasjonForDoedsboDTO.class)))
                .thenReturn(KontaktinformasjonForDoedsboDTO.builder()
                        .adresse(providedAdresse)
                        .build());

        StepVerifier.create(kontaktinformasjonForDoedsboService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(result.getPerson().getKontaktinformasjonForDoedsbo().getFirst().getAdresse().getAdresselinje1(),
                            is(equalTo("Storgata 1")));
                    verify(adresseServiceConsumer, never()).getVegadresse(any(), any());
                })
                .verifyComplete();
    }
}