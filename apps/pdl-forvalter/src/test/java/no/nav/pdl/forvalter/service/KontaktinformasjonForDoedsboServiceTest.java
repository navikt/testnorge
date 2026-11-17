package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.consumer.GenererNavnServiceConsumer;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.consumer.OrganisasjonForvalterConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.dto.OrganisasjonAdresseDTO;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.KontaktinformasjonForDoedsboAdresse;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO.OrganisasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KontaktinformasjonForDoedsboServiceTest {

    private static final String IDENT = "12445678901";
    private static final String HOVEDPERSON_IDENT = "98765432100";
    private static final JsonMapper TEST_JSON_MAPPER = JsonMapper.builder().build();

    private static JsonNode toJson(String json) {
        return TEST_JSON_MAPPER.readTree(json);
    }

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

    @Mock
    private JsonMapper jsonMapper;

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
        when(organisasjonForvalterConsumer.getOrganisasjoner(anyString()))
                .thenReturn(Mono.just(toJson("""
                        {"q1": {"123456789": "Toys"}}""")));

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
        when(organisasjonForvalterConsumer.getOrganisasjoner(anyString()))
                .thenReturn(Mono.just(toJson("""
                        {"q1": {"123456789": "Toys"}}""")));

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
                .thenReturn(Mono.just(toJson("""
                        {"q1": {"organisasjonsnavn": "Advokat AS"}}""")));

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
                .thenReturn(Mono.just(toJson("""
                        {"q1": {"organisasjonsnavn": "Firma AS"}}""")));

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

        when(organisasjonForvalterConsumer.getOrganisasjoner(anyString()))
                .thenReturn(Mono.just(toJson("{}")));

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
    void shouldAcceptAdvokatWithNonMatchingOrgNavn() {

        when(genererNavnServiceConsumer.verifyNavn(any(NavnDTO.class))).thenReturn(Mono.just(true));
        when(organisasjonForvalterConsumer.getOrganisasjoner(anyString()))
                .thenReturn(Mono.just(toJson("""
                        {"q1": {"organisasjonsnavn": "Toys"}}""")));

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
                .verifyComplete();
    }

    @Test
    void shouldRejectOrganisasjonWithNonExistingOrgNumber() {

        when(organisasjonForvalterConsumer.getOrganisasjoner(anyString()))
                .thenReturn(Mono.just(toJson("{}")));

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
                .thenReturn(Mono.just(toJson("""
                        {"q1": {"organisasjonsnavn": "Advokat AS"}}""")));

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
                .thenReturn(Mono.just(toJson("""
                        {"q2": {"organisasjonsnavn": "Firma AS"}}""")));

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
                .thenReturn(Mono.just(toJson("""
                        {"t1": {"organisasjonsnavn": "Firma AS"}}""")));

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
        prepareExistingContact();

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
        prepareExistingContact();

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
        prepareExistingContact();

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
    void shouldCreateContactPersonAndCopyTheirAddress() {

        var bostedadresse = BostedadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder().kommunenummer("0301").build())
                .id(1)
                .build();

        var kontaktPerson = DbPerson.builder()
                .ident(IDENT)
                .person(PersonDTO.builder()
                        .ident(IDENT)
                        .foedselsdato(List.of(FoedselsdatoDTO.builder()
                                .foedselsdato(LocalDate.of(1956, 4, 12).atStartOfDay())
                                .build()))
                        .bostedsadresse(new ArrayList<>(List.of(bostedadresse)))
                        .build())
                .build();

        var kontaktinfo = KontaktinformasjonForDoedsboDTO.builder()
                .skifteform(OFFENTLIG)
                .personSomKontakt(KontaktpersonDTO.builder()
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
        when(mapperFacade.map(any(BostedadresseDTO.class), eq(KontaktinformasjonForDoedsboAdresse.class), any(MappingContext.class)))
                .thenReturn(mappedAdresse);
        when(createPersonService.execute(any(PersonRequestDTO.class))).thenReturn(Mono.just(kontaktPerson));
        when(relasjonService.setRelasjoner(anyString(), eq(RelasjonType.AVDOEDD_FOR_KONTAKT),
                anyString(), eq(RelasjonType.KONTAKT_FOR_DOEDSBO))).thenReturn(Mono.empty());

        StepVerifier.create(kontaktinformasjonForDoedsboService.convert(dbPerson))
                .assertNext(result -> {
                    verify(createPersonService).execute(any(PersonRequestDTO.class));
                    verifyNoInteractions(personRepository);
                    assertThat(result.getPerson().getKontaktinformasjonForDoedsbo().getFirst().getAdresse(), is(mappedAdresse));
                    assertThat(kontaktinfo.getPersonSomKontakt().getIdentifikasjonsnummer(), is(IDENT));
                    assertThat(kontaktinfo.getPersonSomKontakt().isEksisterendePerson(), is(false));
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
                .thenReturn(Mono.just(toJson("""
                        {"q1": {"organisasjonsnavn": "Advokat AS", "adresser": []}}""")));

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
                .thenReturn(Mono.just(toJson("""
                        {"q1": {"organisasjonsnavn": "Firma AS", "adresser": []}}""")));

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
                .thenReturn(Mono.just(toJson("""
                        {"q1": {"organisasjonsnavn": "Advokat AS", "adresser": []}}""")));
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
                .thenReturn(Mono.just(toJson("""
                        {"q1": {"organisasjonsnavn": "Firma AS", "adresser": []}}""")));
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
        prepareExistingContact();

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
                        .foedselsdato(LocalDateTime.of(1980, 1, 1, 0, 0))
                        .navn(PersonNavnDTO.builder().fornavn("Ola").etternavn("Nordmann").build())
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

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldPreserveExistingContactAddressInsteadOfGeneratingAnother(boolean suppliedAddress) {
        var bostedsadresse = BostedadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder().postnummer("0484").build())
                .build();
        var contactPerson = DbPerson.builder()
                .ident(IDENT)
                .person(PersonDTO.builder().bostedsadresse(List.of(bostedsadresse)).build())
                .build();
        var kontaktinfo = KontaktinformasjonForDoedsboDTO.builder()
                .isNew(true)
                .skifteform(ANNET)
                .adresse(suppliedAddress ? KontaktinformasjonForDoedsboAdresse.builder()
                        .adresselinje1("Annen adresse 1").postnummer("0101").landkode("NOR").build() : null)
                .personSomKontakt(KontaktpersonDTO.builder()
                        .identifikasjonsnummer(IDENT)
                        .navn(PersonNavnDTO.builder().fornavn("Ola").etternavn("Nordmann").build())
                        .build())
                .build();
        var dbPerson = prepareConversion(kontaktinfo);
        var poststeder = Map.of("0484", "OSLO");
        when(kodeverkConsumer.getPoststedNavn()).thenReturn(Mono.just(poststeder));
        when(personRepository.findByIdent(IDENT)).thenReturn(Mono.just(contactPerson));
        when(relasjonService.setRelasjoner(HOVEDPERSON_IDENT, RelasjonType.AVDOEDD_FOR_KONTAKT,
                IDENT, RelasjonType.KONTAKT_FOR_DOEDSBO)).thenReturn(Mono.empty());
        var expectedAddress = KontaktinformasjonForDoedsboAdresse.builder()
                .adresselinje1("Kontaktveien 2").postnummer("0484").poststedsnavn("OSLO").build();
        var contextCaptor = ArgumentCaptor.forClass(MappingContext.class);
        when(mapperFacade.map(eq(bostedsadresse), eq(KontaktinformasjonForDoedsboAdresse.class),
                any(MappingContext.class))).thenReturn(expectedAddress);

        StepVerifier.create(kontaktinformasjonForDoedsboService.convert(dbPerson))
                .assertNext(result -> {
                    assertThat(result, is(dbPerson));
                    assertThat(kontaktinfo.getAdresse(), is(expectedAddress));
                    assertThat(kontaktinfo.getPersonSomKontakt().getNavn(), is(nullValue()));
                    assertThat(kontaktinfo.getPersonSomKontakt().isEksisterendePerson(), is(true));
                })
                .verifyComplete();

        verify(mapperFacade).map(eq(bostedsadresse), eq(KontaktinformasjonForDoedsboAdresse.class),
                contextCaptor.capture());
        assertThat(contextCaptor.getValue().getProperty("poststedsnavn"), is(poststeder));
        verify(relasjonService).setRelasjoner(HOVEDPERSON_IDENT, RelasjonType.AVDOEDD_FOR_KONTAKT,
                IDENT, RelasjonType.KONTAKT_FOR_DOEDSBO);
        verifyNoInteractions(createPersonService, adresseServiceConsumer, enkelAdresseService, genererNavnServiceConsumer);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldReadOrganisationAddressAfterLookupCompletes(boolean lawyer) {
        var organisation = OrganisasjonDTO.builder()
                .organisasjonsnummer("123456789")
                .kontaktperson(PersonNavnDTO.builder().fornavn("Ola").etternavn("Nordmann").build())
                .build();
        var kontaktinfo = KontaktinformasjonForDoedsboDTO.builder()
                .isNew(true)
                .skifteform(OFFENTLIG)
                .advokatSomKontakt(lawyer ? organisation : null)
                .organisasjonSomKontakt(lawyer ? null : organisation)
                .build();
        var dbPerson = prepareConversion(kontaktinfo);
        var organisationJson = toJson("""
                {"q1": {"organisasjonsnavn": "Firma AS", "adresser": [{"postnummer": "0484"}]}}
                """);
        var organisationAddress = new OrganisasjonAdresseDTO();
        var expectedAddress = KontaktinformasjonForDoedsboAdresse.builder().postnummer("0484").build();
        when(organisasjonForvalterConsumer.getOrganisasjoner("123456789")).thenReturn(Mono.just(organisationJson));
        when(jsonMapper.treeToValue(organisationJson.findValue("adresser").get(0), OrganisasjonAdresseDTO.class))
                .thenReturn(organisationAddress);
        when(mapperFacade.map(organisationAddress, KontaktinformasjonForDoedsboAdresse.class)).thenReturn(expectedAddress);

        StepVerifier.create(kontaktinformasjonForDoedsboService.convert(dbPerson))
                .assertNext(_ -> {
                    assertThat(kontaktinfo.getAdresse(), is(expectedAddress));
                    assertThat(organisation.getOrganisasjonsnavn(), is("Firma AS"));
                })
                .verifyComplete();
    }

    @Test
    void shouldGenerateForeignAddressForContactWithoutIdent() {
        var kontaktinfo = KontaktinformasjonForDoedsboDTO.builder()
                .isNew(true)
                .skifteform(ANNET)
                .adresse(KontaktinformasjonForDoedsboAdresse.builder().landkode("SWE").build())
                .personSomKontakt(KontaktpersonDTO.builder()
                        .foedselsdato(LocalDateTime.of(1980, 1, 1, 0, 0))
                        .navn(PersonNavnDTO.builder().fornavn("Ola").etternavn("Nordmann").build())
                        .build())
                .build();
        var dbPerson = prepareConversion(kontaktinfo);
        var foreignAddress = new UtenlandskAdresseDTO();
        var expectedAddress = KontaktinformasjonForDoedsboAdresse.builder().landkode("SWE").adresselinje1("Gatan 1").build();
        when(enkelAdresseService.getUtenlandskAdresse(any(UtenlandskAdresseDTO.class), eq("SWE"), any()))
                .thenReturn(Mono.just(foreignAddress));
        when(mapperFacade.map(foreignAddress, KontaktinformasjonForDoedsboAdresse.class)).thenReturn(expectedAddress);

        StepVerifier.create(kontaktinformasjonForDoedsboService.convert(dbPerson))
                .assertNext(_ -> assertThat(kontaktinfo.getAdresse(), is(expectedAddress)))
                .verifyComplete();

        verifyNoInteractions(personRepository, createPersonService, relasjonService, adresseServiceConsumer);
    }

    @Test
    void shouldPropagateRelationFailureWithoutReplacingAddress() {
        var kontaktinfo = KontaktinformasjonForDoedsboDTO.builder()
                .isNew(true)
                .personSomKontakt(KontaktpersonDTO.builder().identifikasjonsnummer(IDENT).build())
                .build();
        var dbPerson = prepareConversion(kontaktinfo);
        when(personRepository.findByIdent(IDENT)).thenReturn(Mono.just(DbPerson.builder().ident(IDENT).build()));
        var error = new IllegalStateException("Relation failed");
        when(relasjonService.setRelasjoner(HOVEDPERSON_IDENT, RelasjonType.AVDOEDD_FOR_KONTAKT,
                IDENT, RelasjonType.KONTAKT_FOR_DOEDSBO)).thenReturn(Mono.error(error));

        StepVerifier.create(kontaktinformasjonForDoedsboService.convert(dbPerson))
                .verifyErrorSatisfies(actual -> assertThat(actual, is(error)));

        verifyNoInteractions(createPersonService, adresseServiceConsumer, enkelAdresseService);
    }

    private DbPerson prepareConversion(KontaktinformasjonForDoedsboDTO kontaktinfo) {
        when(kodeverkConsumer.getPoststedNavn()).thenReturn(Mono.just(new HashMap<>()));
        when(mapperFacade.map(kontaktinfo, KontaktinformasjonForDoedsboDTO.class)).thenReturn(kontaktinfo);
        return DbPerson.builder()
                .ident(HOVEDPERSON_IDENT)
                .person(PersonDTO.builder().ident(HOVEDPERSON_IDENT)
                        .kontaktinformasjonForDoedsbo(List.of(kontaktinfo)).build())
                .build();
    }

    private void prepareExistingContact() {
        when(personRepository.findByIdent(IDENT)).thenReturn(Mono.just(DbPerson.builder()
                .ident(IDENT).person(PersonDTO.builder().build()).build()));
        when(relasjonService.setRelasjoner(HOVEDPERSON_IDENT, RelasjonType.AVDOEDD_FOR_KONTAKT,
                IDENT, RelasjonType.KONTAKT_FOR_DOEDSBO)).thenReturn(Mono.empty());
    }
}