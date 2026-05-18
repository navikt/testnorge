package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.database.model.DbAlias;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.AliasRepository;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.pdl.forvalter.dto.OpprettRequest;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedestedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO.PdlStatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdlOrdreServiceTest {

    private static final String HOVEDPERSON_IDENT = "12345678901";
    private static final String RELATERT_PERSON_IDENT = "98765432109";
    private static final String ALIAS_IDENT = "11111111111";
    private static final Long HOVEDPERSON_ID = 1L;
    private static final Long RELATERT_PERSON_ID = 2L;

    @Mock
    private DeployService deployService;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private RelasjonRepository relasjonRepository;

    @Mock
    private AliasRepository aliasRepository;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private HendelseIdService hendelseIdService;

    @Mock
    private KodeverkConsumer kodeverkConsumer;

    @InjectMocks
    private PdlOrdreService pdlOrdreService;

    @Test
    void shouldThrowNotFoundExceptionWhenIdentDoesNotExist() {

        when(aliasRepository.findByTidligereIdent(HOVEDPERSON_IDENT)).thenReturn(Mono.empty());
        when(personRepository.findByIdent(HOVEDPERSON_IDENT)).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .verifyErrorSatisfies(throwable -> {
                    assertThat(throwable instanceof NotFoundException, is(true));
                    assertThat(throwable.getMessage(), containsString(HOVEDPERSON_IDENT));
                });
    }

    @Test
    void shouldThrowInvalidRequestExceptionWhenIdentIsAlias() {

        var dbAlias = DbAlias.builder()
                .personId(HOVEDPERSON_ID)
                .tidligereIdent(ALIAS_IDENT)
                .build();

        var gjeldendeDbPerson = DbPerson.builder()
                .id(HOVEDPERSON_ID)
                .ident(HOVEDPERSON_IDENT)
                .build();

        when(aliasRepository.findByTidligereIdent(ALIAS_IDENT)).thenReturn(Mono.just(dbAlias));
        when(personRepository.findById(HOVEDPERSON_ID)).thenReturn(Mono.just(gjeldendeDbPerson));

        StepVerifier.create(pdlOrdreService.send(ALIAS_IDENT, false))
                .verifyErrorSatisfies(throwable -> {
                    assertThat(throwable instanceof InvalidRequestException, is(true));
                    assertThat(throwable.getMessage(), containsString(HOVEDPERSON_IDENT));
                });
    }

    @Test
    void shouldSendOrdreForPersonWithNoRelasjoner() {

        var personDTO = buildMinimalPersonDTO();
        var dbPerson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, personDTO);

        stubBasicSendFlow(dbPerson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.empty());
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response -> {
                    assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT)));
                    assertThat(response.getRelasjoner(), is(empty()));
                })
                .verifyComplete();
    }

    @Test
    void shouldIncludeRelatertePersonerInResponse() {

        var hovedPersonDTO = buildMinimalPersonDTO();
        var dbHovedperson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, hovedPersonDTO);

        var relatertPersonDTO = buildMinimalPersonDTO();
        var dbRelatertPerson = buildDbPerson(RELATERT_PERSON_ID, RELATERT_PERSON_IDENT, relatertPersonDTO);

        var dbRelasjon = DbRelasjon.builder()
                .personId(HOVEDPERSON_ID)
                .relatertPersonId(RELATERT_PERSON_ID)
                .build();

        stubBasicSendFlow(dbHovedperson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.just(dbRelasjon));
        when(personRepository.findById(RELATERT_PERSON_ID)).thenReturn(Mono.just(dbRelatertPerson));
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        when(aliasRepository.existsByPersonId(RELATERT_PERSON_ID)).thenReturn(Mono.just(false));
        when(aliasRepository.findByTidligereIdent(RELATERT_PERSON_IDENT)).thenReturn(Mono.empty());
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response -> {
                    assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT)));
                    assertThat(response.getRelasjoner(), hasSize(1));
                    assertThat(response.getRelasjoner().getFirst().getIdent(), is(equalTo(RELATERT_PERSON_IDENT)));
                })
                .verifyComplete();
    }

    @Test
    void shouldExcludeEksternePersonerWhenFlagIsTrue() {

        var sivilstand = SivilstandDTO.builder()
                .relatertVedSivilstand(RELATERT_PERSON_IDENT)
                .eksisterendePerson(true)
                .build();

        var hovedPersonDTO = buildMinimalPersonDTO();
        hovedPersonDTO.setSivilstand(new ArrayList<>(List.of(sivilstand)));

        var dbHovedperson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, hovedPersonDTO);

        var relatertPersonDTO = buildMinimalPersonDTO();
        var dbRelatertPerson = buildDbPerson(RELATERT_PERSON_ID, RELATERT_PERSON_IDENT, relatertPersonDTO);

        var dbRelasjon = DbRelasjon.builder()
                .personId(HOVEDPERSON_ID)
                .relatertPersonId(RELATERT_PERSON_ID)
                .build();

        stubBasicSendFlow(dbHovedperson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.just(dbRelasjon));
        when(personRepository.findById(RELATERT_PERSON_ID)).thenReturn(Mono.just(dbRelatertPerson));
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, true))
                .assertNext(response -> {
                    assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT)));
                    assertThat(response.getRelasjoner(), is(empty()));
                })
                .verifyComplete();
    }

    @Test
    void shouldIncludeEksternePersonerWhenFlagIsFalse() {

        var sivilstand = SivilstandDTO.builder()
                .relatertVedSivilstand(RELATERT_PERSON_IDENT)
                .eksisterendePerson(true)
                .build();

        var hovedPersonDTO = buildMinimalPersonDTO();
        hovedPersonDTO.setSivilstand(new ArrayList<>(List.of(sivilstand)));

        var dbHovedperson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, hovedPersonDTO);

        var relatertPersonDTO = buildMinimalPersonDTO();
        var dbRelatertPerson = buildDbPerson(RELATERT_PERSON_ID, RELATERT_PERSON_IDENT, relatertPersonDTO);

        var dbRelasjon = DbRelasjon.builder()
                .personId(HOVEDPERSON_ID)
                .relatertPersonId(RELATERT_PERSON_ID)
                .build();

        stubBasicSendFlow(dbHovedperson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.just(dbRelasjon));
        when(personRepository.findById(RELATERT_PERSON_ID)).thenReturn(Mono.just(dbRelatertPerson));
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        when(aliasRepository.existsByPersonId(RELATERT_PERSON_ID)).thenReturn(Mono.just(false));
        when(aliasRepository.findByTidligereIdent(RELATERT_PERSON_IDENT)).thenReturn(Mono.empty());
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response -> {
                    assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT)));
                    assertThat(response.getRelasjoner(), hasSize(1));
                    assertThat(response.getRelasjoner().getFirst().getIdent(), is(equalTo(RELATERT_PERSON_IDENT)));
                })
                .verifyComplete();
    }

    @Test
    void shouldCollectEksternePersonerFromVergemaal() {

        var vergemaal = VergemaalDTO.builder()
                .vergeIdent(RELATERT_PERSON_IDENT)
                .eksisterendePerson(true)
                .build();

        var hovedPersonDTO = buildMinimalPersonDTO();
        hovedPersonDTO.setVergemaal(new ArrayList<>(List.of(vergemaal)));

        var dbHovedperson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, hovedPersonDTO);

        var dbRelasjon = DbRelasjon.builder()
                .personId(HOVEDPERSON_ID)
                .relatertPersonId(RELATERT_PERSON_ID)
                .build();

        var relatertPersonDTO = buildMinimalPersonDTO();
        var dbRelatertPerson = buildDbPerson(RELATERT_PERSON_ID, RELATERT_PERSON_IDENT, relatertPersonDTO);

        stubBasicSendFlow(dbHovedperson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.just(dbRelasjon));
        when(personRepository.findById(RELATERT_PERSON_ID)).thenReturn(Mono.just(dbRelatertPerson));
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, true))
                .assertNext(response -> {
                    assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT)));
                    assertThat(response.getRelasjoner(), is(empty()));
                })
                .verifyComplete();
    }

    @Test
    void shouldCollectEksternePersonerFromFullmakt() {

        var fullmakt = FullmaktDTO.builder()
                .motpartsPersonident(RELATERT_PERSON_IDENT)
                .eksisterendePerson(true)
                .build();

        var hovedPersonDTO = buildMinimalPersonDTO();
        hovedPersonDTO.setFullmakt(new ArrayList<>(List.of(fullmakt)));

        var dbHovedperson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, hovedPersonDTO);

        var dbRelasjon = DbRelasjon.builder()
                .personId(HOVEDPERSON_ID)
                .relatertPersonId(RELATERT_PERSON_ID)
                .build();

        var relatertPersonDTO = buildMinimalPersonDTO();
        var dbRelatertPerson = buildDbPerson(RELATERT_PERSON_ID, RELATERT_PERSON_IDENT, relatertPersonDTO);

        stubBasicSendFlow(dbHovedperson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.just(dbRelasjon));
        when(personRepository.findById(RELATERT_PERSON_ID)).thenReturn(Mono.just(dbRelatertPerson));
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, true))
                .assertNext(response -> {
                    assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT)));
                    assertThat(response.getRelasjoner(), is(empty()));
                })
                .verifyComplete();
    }

    @Test
    void shouldCollectEksternePersonerFromForelderBarnRelasjon() {

        var forelderBarnRelasjon = ForelderBarnRelasjonDTO.builder()
                .relatertPerson(RELATERT_PERSON_IDENT)
                .eksisterendePerson(true)
                .build();

        var hovedPersonDTO = buildMinimalPersonDTO();
        hovedPersonDTO.setForelderBarnRelasjon(new ArrayList<>(List.of(forelderBarnRelasjon)));

        var dbHovedperson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, hovedPersonDTO);

        var dbRelasjon = DbRelasjon.builder()
                .personId(HOVEDPERSON_ID)
                .relatertPersonId(RELATERT_PERSON_ID)
                .build();

        var relatertPersonDTO = buildMinimalPersonDTO();
        var dbRelatertPerson = buildDbPerson(RELATERT_PERSON_ID, RELATERT_PERSON_IDENT, relatertPersonDTO);

        stubBasicSendFlow(dbHovedperson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.just(dbRelasjon));
        when(personRepository.findById(RELATERT_PERSON_ID)).thenReturn(Mono.just(dbRelatertPerson));
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, true))
                .assertNext(response -> {
                    assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT)));
                    assertThat(response.getRelasjoner(), is(empty()));
                })
                .verifyComplete();
    }

    @Test
    void shouldNotFilterRelatertePersonerWhenEkskluderEksternePersonerIsNull() {

        var hovedPersonDTO = buildMinimalPersonDTO();
        var dbHovedperson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, hovedPersonDTO);

        var relatertPersonDTO = buildMinimalPersonDTO();
        var dbRelatertPerson = buildDbPerson(RELATERT_PERSON_ID, RELATERT_PERSON_IDENT, relatertPersonDTO);

        var dbRelasjon = DbRelasjon.builder()
                .personId(HOVEDPERSON_ID)
                .relatertPersonId(RELATERT_PERSON_ID)
                .build();

        stubBasicSendFlow(dbHovedperson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.just(dbRelasjon));
        when(personRepository.findById(RELATERT_PERSON_ID)).thenReturn(Mono.just(dbRelatertPerson));
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        when(aliasRepository.existsByPersonId(RELATERT_PERSON_ID)).thenReturn(Mono.just(false));
        when(aliasRepository.findByTidligereIdent(RELATERT_PERSON_IDENT)).thenReturn(Mono.empty());
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, null))
                .assertNext(response ->
                        assertThat(response.getRelasjoner(), hasSize(1)))
                .verifyComplete();
    }

    @Test
    void shouldCallHendelseIdServiceOppdaterPersonAfterSend() {

        var personDTO = buildMinimalPersonDTO();
        var dbPerson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, personDTO);

        stubBasicSendFlow(dbPerson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.empty());
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response ->
                        verify(hendelseIdService).oppdaterPerson(any(OrdreResponseDTO.class)))
                .verifyComplete();
    }

    @Test
    void shouldPassThroughWhenAliasDoesNotExist() {

        var personDTO = buildMinimalPersonDTO();
        var dbPerson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, personDTO);

        when(aliasRepository.findByTidligereIdent(HOVEDPERSON_IDENT)).thenReturn(Mono.empty());
        when(personRepository.findByIdent(HOVEDPERSON_IDENT)).thenReturn(Mono.just(dbPerson));
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.empty());
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response ->
                        assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT))))
                .verifyComplete();
    }

    @Test
    void shouldReturnCorrectResponseStructure() {

        var personDTO = buildMinimalPersonDTO();
        var dbPerson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, personDTO);

        var pdlStatus = PdlStatusDTO.builder()
                .ident(HOVEDPERSON_IDENT)
                .infoElement(PdlArtifact.PDL_NAVN)
                .hendelser(List.of(OrdreResponseDTO.HendelseDTO.builder()
                        .id(1)
                        .hendelseId("hendelse-1")
                        .build()))
                .build();

        stubBasicSendFlow(dbPerson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.empty());
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        stubDeployServiceForSlettingAndSendOrders(List.of(pdlStatus));
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response -> {
                    assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT)));
                    assertThat(response.getHovedperson().getOrdrer(), hasSize(1));
                    assertThat(response.getHovedperson().getOrdrer().getFirst().getInfoElement(), is(equalTo(PdlArtifact.PDL_NAVN)));
                })
                .verifyComplete();
    }

    private PersonDTO buildMinimalPersonDTO() {
        return PersonDTO.builder()
                .navn(new ArrayList<>())
                .foedsel(new ArrayList<>())
                .foedested(new ArrayList<>())
                .foedselsdato(new ArrayList<>())
                .kjoenn(new ArrayList<>())
                .adressebeskyttelse(new ArrayList<>())
                .bostedsadresse(new ArrayList<>())
                .kontaktadresse(new ArrayList<>())
                .oppholdsadresse(new ArrayList<>())
                .innflytting(new ArrayList<>())
                .utflytting(new ArrayList<>())
                .deltBosted(new ArrayList<>())
                .foreldreansvar(new ArrayList<>())
                .forelderBarnRelasjon(new ArrayList<>())
                .sivilstand(new ArrayList<>())
                .statsborgerskap(new ArrayList<>())
                .doedsfall(new ArrayList<>())
                .telefonnummer(new ArrayList<>())
                .opphold(new ArrayList<>())
                .kontaktinformasjonForDoedsbo(new ArrayList<>())
                .utenlandskIdentifikasjonsnummer(new ArrayList<>())
                .falskIdentitet(new ArrayList<>())
                .tilrettelagtKommunikasjon(new ArrayList<>())
                .doedfoedtBarn(new ArrayList<>())
                .sikkerhetstiltak(new ArrayList<>())
                .navPersonIdentifikator(new ArrayList<>())
                .folkeregisterPersonstatus(new ArrayList<>())
                .vergemaal(new ArrayList<>())
                .fullmakt(new ArrayList<>())
                .build();
    }

    private DbPerson buildDbPerson(Long id, String ident, PersonDTO personDTO) {
        return DbPerson.builder()
                .id(id)
                .ident(ident)
                .person(personDTO)
                .build();
    }

    private void stubBasicSendFlow(DbPerson dbPerson) {
        when(aliasRepository.findByTidligereIdent(dbPerson.getIdent())).thenReturn(Mono.empty());
        when(personRepository.findByIdent(dbPerson.getIdent())).thenReturn(Mono.just(dbPerson));
    }

    private void stubDeployServiceDefaults() {
        when(deployService.createOrdre(any(PdlArtifact.class), anyString(), anyList())).thenReturn(Flux.empty());
        when(deployService.sendOrders(any())).thenReturn(Flux.empty());
        when(aliasRepository.findByPersonId(any(Long.class))).thenReturn(Flux.empty());
        when(personRepository.findByIdentIn(anyList())).thenReturn(Flux.empty());
    }

    private void stubDeployServiceForSlettingAndSendOrders(List<PdlStatusDTO> statuses) {
        when(deployService.createOrdre(any(PdlArtifact.class), anyString(), anyList())).thenReturn(Flux.empty());
        when(deployService.sendOrders(any())).thenReturn(Flux.fromIterable(statuses));
        when(aliasRepository.findByPersonId(any(Long.class))).thenReturn(Flux.empty());
        when(personRepository.findByIdentIn(anyList())).thenReturn(Flux.empty());
    }

    @Test
    void shouldCollectEksternePersonerFromKontaktinformasjonForDoedsbo() {

        var kontaktperson = KontaktinformasjonForDoedsboDTO.KontaktpersonDTO.builder()
                .identifikasjonsnummer(RELATERT_PERSON_IDENT)
                .eksisterendePerson(true)
                .build();

        var kontaktinfo = KontaktinformasjonForDoedsboDTO.builder()
                .personSomKontakt(kontaktperson)
                .build();

        var hovedPersonDTO = buildMinimalPersonDTO();
        hovedPersonDTO.setKontaktinformasjonForDoedsbo(new ArrayList<>(List.of(kontaktinfo)));

        var dbHovedperson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, hovedPersonDTO);

        var dbRelasjon = DbRelasjon.builder()
                .personId(HOVEDPERSON_ID)
                .relatertPersonId(RELATERT_PERSON_ID)
                .build();

        var relatertPersonDTO = buildMinimalPersonDTO();
        var dbRelatertPerson = buildDbPerson(RELATERT_PERSON_ID, RELATERT_PERSON_IDENT, relatertPersonDTO);

        stubBasicSendFlow(dbHovedperson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.just(dbRelasjon));
        when(personRepository.findById(RELATERT_PERSON_ID)).thenReturn(Mono.just(dbRelatertPerson));
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, true))
                .assertNext(response -> {
                    assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT)));
                    assertThat(response.getRelasjoner(), is(empty()));
                })
                .verifyComplete();
    }

    @Test
    void shouldCollectEksternePersonerFromChildrenForeldreansvarAndre() {

        var barnIdent = "11223344556";

        var forelderBarnRelasjon = ForelderBarnRelasjonDTO.builder()
                .relatertPerson(barnIdent)
                .relatertPersonsRolle(ForelderBarnRelasjonDTO.Rolle.BARN)
                .build();

        var hovedPersonDTO = buildMinimalPersonDTO();
        hovedPersonDTO.setForelderBarnRelasjon(new ArrayList<>(List.of(forelderBarnRelasjon)));

        var dbHovedperson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, hovedPersonDTO);

        var barnForeldreansvar = ForeldreansvarDTO.builder()
                .ansvar(ForeldreansvarDTO.Ansvar.ANDRE)
                .ansvarlig(RELATERT_PERSON_IDENT)
                .eksisterendePerson(true)
                .build();

        var barnPersonDTO = buildMinimalPersonDTO();
        barnPersonDTO.setForeldreansvar(new ArrayList<>(List.of(barnForeldreansvar)));

        var dbBarn = buildDbPerson(3L, barnIdent, barnPersonDTO);

        var dbRelasjon = DbRelasjon.builder()
                .personId(HOVEDPERSON_ID)
                .relatertPersonId(RELATERT_PERSON_ID)
                .build();

        var relatertPersonDTO = buildMinimalPersonDTO();
        var dbRelatertPerson = buildDbPerson(RELATERT_PERSON_ID, RELATERT_PERSON_IDENT, relatertPersonDTO);

        stubBasicSendFlow(dbHovedperson);
        when(personRepository.findByIdent(barnIdent)).thenReturn(Mono.just(dbBarn));
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.just(dbRelasjon));
        when(personRepository.findById(RELATERT_PERSON_ID)).thenReturn(Mono.just(dbRelatertPerson));
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, true))
                .assertNext(response -> {
                    assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT)));
                    assertThat(response.getRelasjoner(), is(empty()));
                })
                .verifyComplete();
    }

    @Test
    void shouldSendOrdreWithNamesUppercased() {

        var navn = NavnDTO.builder()
                .id(1)
                .fornavn("ola")
                .mellomnavn("mellom")
                .etternavn("nordmann")
                .build();

        var personDTO = buildMinimalPersonDTO();
        personDTO.setNavn(new ArrayList<>(List.of(navn)));

        var dbPerson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, personDTO);

        stubBasicSendFlow(dbPerson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.empty());
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(true));
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response ->
                        assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT))))
                .verifyComplete();

        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_NAVN),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT),
                argThat(list -> {
                    if (list.isEmpty()) return false;
                    var n = (NavnDTO) list.getFirst();
                    return "OLA".equals(n.getFornavn()) &&
                           "MELLOM".equals(n.getMellomnavn()) &&
                           "NORDMANN".equals(n.getEtternavn());
                }));
    }

    @Test
    void shouldSendOrdreUsingFoedestedWhenPresent() {

        var foedested = FoedestedDTO.builder()
                .id(1)
                .foedeland("NOR")
                .build();

        var foedsel = FoedselDTO.builder()
                .id(1)
                .foedeland("SWE")
                .build();

        var personDTO = buildMinimalPersonDTO();
        personDTO.setFoedested(new ArrayList<>(List.of(foedested)));
        personDTO.setFoedsel(new ArrayList<>(List.of(foedsel)));

        var dbPerson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, personDTO);

        stubBasicSendFlow(dbPerson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.empty());
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(true));
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response ->
                        assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT))))
                .verifyComplete();

        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_FOEDESTED),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT),
                argThat(list -> !list.isEmpty() && list.getFirst() instanceof FoedestedDTO));
    }

    @Test
    void shouldFallbackToFoedselWhenFoedestedIsEmpty() {

        var foedsel = FoedselDTO.builder()
                .id(1)
                .foedeland("SWE")
                .build();

        var personDTO = buildMinimalPersonDTO();
        personDTO.setFoedsel(new ArrayList<>(List.of(foedsel)));

        var dbPerson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, personDTO);
        var mappedFoedested = FoedestedDTO.builder().foedeland("SWE").build();

        stubBasicSendFlow(dbPerson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.empty());
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(true));
        when(mapperFacade.mapAsList(any(Iterable.class), any(Class.class))).thenReturn(new ArrayList<>());
        when(mapperFacade.mapAsList(anyList(), org.mockito.ArgumentMatchers.eq(FoedestedDTO.class)))
                .thenReturn(List.of(mappedFoedested));
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response ->
                        assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT))))
                .verifyComplete();

        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_FOEDESTED),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT),
                argThat(list -> !list.isEmpty() && list.getFirst() instanceof FoedestedDTO));
    }

    @Test
    void shouldFallbackToFoedselForFoedselsdatoWhenFoedselsdatoIsEmpty() {

        var foedsel = FoedselDTO.builder()
                .id(1)
                .foedeland("NOR")
                .build();

        var personDTO = buildMinimalPersonDTO();
        personDTO.setFoedsel(new ArrayList<>(List.of(foedsel)));

        var dbPerson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, personDTO);
        var mappedFoedselsdato = FoedselDTO.builder().foedeland("NOR").build();

        stubBasicSendFlow(dbPerson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.empty());
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(true));
        when(mapperFacade.mapAsList(any(Iterable.class), any(Class.class))).thenReturn(new ArrayList<>());
        when(mapperFacade.mapAsList(anyList(), org.mockito.ArgumentMatchers.eq(FoedselDTO.class)))
                .thenReturn(List.of(mappedFoedselsdato));
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response ->
                        assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT))))
                .verifyComplete();

        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_FOEDSELSDATO),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT),
                argThat(list -> !list.isEmpty() && list.getFirst() instanceof FoedselDTO));
    }

    @Test
    void shouldFilterForeldreansvarAnsvarssubjektInGetOrdrer() {

        var foreldreansvarMedSubjekt = ForeldreansvarDTO.builder()
                .id(1)
                .ansvar(ForeldreansvarDTO.Ansvar.FELLES)
                .ansvarssubjekt("99988877766")
                .build();

        var foreldreansvarUtenSubjekt = ForeldreansvarDTO.builder()
                .id(2)
                .ansvar(ForeldreansvarDTO.Ansvar.MOR)
                .build();

        var personDTO = buildMinimalPersonDTO();
        personDTO.setForeldreansvar(new ArrayList<>(List.of(foreldreansvarMedSubjekt, foreldreansvarUtenSubjekt)));

        var dbPerson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, personDTO);

        stubBasicSendFlow(dbPerson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.empty());
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(true));
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response ->
                        assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT))))
                .verifyComplete();

        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_FORELDREANSVAR),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT),
                argThat(list -> list.size() == 1 &&
                        ((ForeldreansvarDTO) list.getFirst()).getAnsvar() == ForeldreansvarDTO.Ansvar.MOR));
    }

    @Test
    void shouldFilterSivilstandSamboerInGetOrdrer() {

        var sivilstandEkteskap = SivilstandDTO.builder()
                .id(1)
                .type(SivilstandDTO.Sivilstand.GIFT)
                .build();

        var sivilstandSamboer = SivilstandDTO.builder()
                .id(2)
                .type(SivilstandDTO.Sivilstand.SAMBOER)
                .build();

        var personDTO = buildMinimalPersonDTO();
        personDTO.setSivilstand(new ArrayList<>(List.of(sivilstandEkteskap, sivilstandSamboer)));

        var dbPerson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, personDTO);

        stubBasicSendFlow(dbPerson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.empty());
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(true));
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response ->
                        assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT))))
                .verifyComplete();

        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_SIVILSTAND),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT),
                argThat(list -> list.size() == 1 &&
                        ((SivilstandDTO) list.getFirst()).getType() == SivilstandDTO.Sivilstand.GIFT));
    }

    @Test
    void shouldReturnPersonHendelserForHovedpersonOnly() {

        var hovedpersonStatus = PdlStatusDTO.builder()
                .ident(HOVEDPERSON_IDENT)
                .infoElement(PdlArtifact.PDL_NAVN)
                .hendelser(List.of(OrdreResponseDTO.HendelseDTO.builder()
                        .id(1)
                        .hendelseId("h1")
                        .build()))
                .build();

        var relatertStatus = PdlStatusDTO.builder()
                .ident(RELATERT_PERSON_IDENT)
                .infoElement(PdlArtifact.PDL_KJOENN)
                .hendelser(List.of(OrdreResponseDTO.HendelseDTO.builder()
                        .id(2)
                        .hendelseId("h2")
                        .build()))
                .build();

        var personDTO = buildMinimalPersonDTO();
        var dbPerson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, personDTO);

        stubBasicSendFlow(dbPerson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.empty());
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        stubDeployServiceForSlettingAndSendOrders(List.of(hovedpersonStatus, relatertStatus));
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response -> {
                    assertThat(response.getHovedperson().getOrdrer(), hasSize(1));
                    assertThat(response.getHovedperson().getOrdrer().getFirst().getInfoElement(),
                            is(equalTo(PdlArtifact.PDL_NAVN)));
                })
                .verifyComplete();
    }

    @Test
    void shouldDistributeHendelserToCorrectRelasjon() {

        var hovedPersonDTO = buildMinimalPersonDTO();
        var dbHovedperson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, hovedPersonDTO);

        var relatertPersonDTO = buildMinimalPersonDTO();
        var dbRelatertPerson = buildDbPerson(RELATERT_PERSON_ID, RELATERT_PERSON_IDENT, relatertPersonDTO);

        var dbRelasjon = DbRelasjon.builder()
                .personId(HOVEDPERSON_ID)
                .relatertPersonId(RELATERT_PERSON_ID)
                .build();

        var relatertStatus = PdlStatusDTO.builder()
                .ident(RELATERT_PERSON_IDENT)
                .infoElement(PdlArtifact.PDL_KJOENN)
                .hendelser(List.of(OrdreResponseDTO.HendelseDTO.builder()
                        .id(2)
                        .hendelseId("h2")
                        .build()))
                .build();

        stubBasicSendFlow(dbHovedperson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.just(dbRelasjon));
        when(personRepository.findById(RELATERT_PERSON_ID)).thenReturn(Mono.just(dbRelatertPerson));
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        when(aliasRepository.existsByPersonId(RELATERT_PERSON_ID)).thenReturn(Mono.just(false));
        when(aliasRepository.findByTidligereIdent(RELATERT_PERSON_IDENT)).thenReturn(Mono.empty());
        stubDeployServiceForSlettingAndSendOrders(List.of(relatertStatus));
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response -> {
                    assertThat(response.getHovedperson().getOrdrer(), is(empty()));
                    assertThat(response.getRelasjoner(), hasSize(1));
                    assertThat(response.getRelasjoner().getFirst().getOrdrer(), hasSize(1));
                    assertThat(response.getRelasjoner().getFirst().getOrdrer().getFirst().getInfoElement(),
                            is(equalTo(PdlArtifact.PDL_KJOENN)));
                })
                .verifyComplete();
    }

    @Test
    void shouldSendAllArtifactTypesInGetOrdrer() {

        var personDTO = buildMinimalPersonDTO();
        var dbPerson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, personDTO);

        stubBasicSendFlow(dbPerson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.empty());
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(true));
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response ->
                        assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT))))
                .verifyComplete();

        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_FOLKEREGISTER_PERSONSTATUS),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_ADRESSEBESKYTTELSE),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_DOEDSFALL),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_NAVN),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_KJOENN),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_STATSBORGERSKAP),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_BOSTEDADRESSE),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_KONTAKTADRESSE),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_OPPHOLDSADRESSE),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_INNFLYTTING),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_UTFLYTTING),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_DELTBOSTED),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_FORELDREANSVAR),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_FORELDRE_BARN_RELASJON),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_SIVILSTAND),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_TELEFONUMMER),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_OPPHOLD),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_KONTAKTINFORMASJON_FOR_DODESDBO),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_UTENLANDS_IDENTIFIKASJON_NUMMER),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_DOEDFOEDT_BARN),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_SIKKERHETSTILTAK),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
    }

    @Test
    void shouldSendSlettingBeforeOppretting() {

        var personDTO = buildMinimalPersonDTO();
        var dbPerson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, personDTO);

        stubBasicSendFlow(dbPerson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.empty());
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response ->
                        assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT))))
                .verifyComplete();

        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_SLETTING),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).createOrdre(
                org.mockito.ArgumentMatchers.eq(PdlArtifact.PDL_OPPRETT_PERSON),
                org.mockito.ArgumentMatchers.eq(HOVEDPERSON_IDENT), anyList());
        verify(deployService).sendOrders(any());
    }

    @Test
    void shouldHandleMultipleRelatertePersoner() {

        var thirdPersonIdent = "33344455567";
        var thirdPersonId = 3L;

        var hovedPersonDTO = buildMinimalPersonDTO();
        var dbHovedperson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, hovedPersonDTO);

        var relatert1DTO = buildMinimalPersonDTO();
        var dbRelatertPerson1 = buildDbPerson(RELATERT_PERSON_ID, RELATERT_PERSON_IDENT, relatert1DTO);

        var relatert2DTO = buildMinimalPersonDTO();
        var dbRelatertPerson2 = buildDbPerson(thirdPersonId, thirdPersonIdent, relatert2DTO);

        var dbRelasjon1 = DbRelasjon.builder()
                .personId(HOVEDPERSON_ID)
                .relatertPersonId(RELATERT_PERSON_ID)
                .build();

        var dbRelasjon2 = DbRelasjon.builder()
                .personId(HOVEDPERSON_ID)
                .relatertPersonId(thirdPersonId)
                .build();

        stubBasicSendFlow(dbHovedperson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.just(dbRelasjon1, dbRelasjon2));
        when(personRepository.findById(RELATERT_PERSON_ID)).thenReturn(Mono.just(dbRelatertPerson1));
        when(personRepository.findById(thirdPersonId)).thenReturn(Mono.just(dbRelatertPerson2));
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        when(aliasRepository.existsByPersonId(RELATERT_PERSON_ID)).thenReturn(Mono.just(false));
        when(aliasRepository.existsByPersonId(thirdPersonId)).thenReturn(Mono.just(false));
        when(aliasRepository.findByTidligereIdent(RELATERT_PERSON_IDENT)).thenReturn(Mono.empty());
        when(aliasRepository.findByTidligereIdent(thirdPersonIdent)).thenReturn(Mono.empty());
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response -> {
                    assertThat(response.getHovedperson().getIdent(), is(equalTo(HOVEDPERSON_IDENT)));
                    assertThat(response.getRelasjoner(), hasSize(2));
                })
                .verifyComplete();
    }

    @Test
    void shouldNotDuplicateRelatertPersonInResponse() {

        var hovedPersonDTO = buildMinimalPersonDTO();
        var dbHovedperson = buildDbPerson(HOVEDPERSON_ID, HOVEDPERSON_IDENT, hovedPersonDTO);

        var relatertPersonDTO = buildMinimalPersonDTO();
        var dbRelatertPerson = buildDbPerson(RELATERT_PERSON_ID, RELATERT_PERSON_IDENT, relatertPersonDTO);

        var dbRelasjon1 = DbRelasjon.builder()
                .personId(HOVEDPERSON_ID)
                .relatertPersonId(RELATERT_PERSON_ID)
                .build();

        var dbRelasjon2 = DbRelasjon.builder()
                .personId(HOVEDPERSON_ID)
                .relatertPersonId(RELATERT_PERSON_ID)
                .build();

        stubBasicSendFlow(dbHovedperson);
        when(relasjonRepository.findByPersonId(HOVEDPERSON_ID)).thenReturn(Flux.just(dbRelasjon1, dbRelasjon2));
        when(personRepository.findById(RELATERT_PERSON_ID)).thenReturn(Mono.just(dbRelatertPerson));
        when(aliasRepository.existsByPersonId(HOVEDPERSON_ID)).thenReturn(Mono.just(false));
        when(aliasRepository.existsByPersonId(RELATERT_PERSON_ID)).thenReturn(Mono.just(false));
        when(aliasRepository.findByTidligereIdent(RELATERT_PERSON_IDENT)).thenReturn(Mono.empty());
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, false))
                .assertNext(response ->
                        assertThat(response.getRelasjoner(), hasSize(1)))
                .verifyComplete();
    }

    @Nested
    class IdentComparatorTest {

        private static final String FNR_IDENT = "12445678901";
        private static final String FNR_IDENT_2 = "10445678901";
        private static final String DNR_IDENT = "52445678901";
        private static final String DNR_IDENT_2 = "60445678901";
        private static final String NPID_IDENT = "12645678901";
        private static final String NPID_IDENT_2 = "12745678901";
        private static final String HOVEDPERSON_FNR = "10445678902";

        private final PdlOrdreService.IdentComparator comparator =
                new PdlOrdreService.IdentComparator(HOVEDPERSON_FNR);

        private OpprettRequest buildOpprettRequest(String ident) {
            return OpprettRequest.builder()
                    .person(DbPerson.builder().ident(ident).build())
                    .build();
        }

        @Test
        void shouldReturnZeroForSameIdenttypeWhenNeitherIsHovedperson_FNR() {
            var result = comparator.compare(
                    buildOpprettRequest(FNR_IDENT),
                    buildOpprettRequest(FNR_IDENT_2));

            assertThat(result).isZero();
        }

        @Test
        void shouldReturnZeroForSameIdenttypeWhenNeitherIsHovedperson_DNR() {
            var result = comparator.compare(
                    buildOpprettRequest(DNR_IDENT),
                    buildOpprettRequest(DNR_IDENT_2));

            assertThat(result).isZero();
        }

        @Test
        void shouldReturnZeroForSameIdenttypeWhenNeitherIsHovedperson_NPID() {
            var result = comparator.compare(
                    buildOpprettRequest(NPID_IDENT),
                    buildOpprettRequest(NPID_IDENT_2));

            assertThat(result).isZero();
        }

        @Test
        void shouldSortHovedpersonLastWhenO1IsHovedperson() {
            var result = comparator.compare(
                    buildOpprettRequest(HOVEDPERSON_FNR),
                    buildOpprettRequest(FNR_IDENT));

            assertThat(result).isPositive();
        }

        @Test
        void shouldSortHovedpersonLastWhenO2IsHovedperson() {
            var result = comparator.compare(
                    buildOpprettRequest(FNR_IDENT),
                    buildOpprettRequest(HOVEDPERSON_FNR));

            assertThat(result).isNegative();
        }

        @Test
        void shouldSortDnrBeforeFnr() {
            var result = comparator.compare(
                    buildOpprettRequest(DNR_IDENT),
                    buildOpprettRequest(FNR_IDENT));

            assertThat(result).isNegative();
        }

        @Test
        void shouldSortFnrAfterDnr() {
            var result = comparator.compare(
                    buildOpprettRequest(FNR_IDENT),
                    buildOpprettRequest(DNR_IDENT));

            assertThat(result).isPositive();
        }

        @Test
        void shouldSortNpidBeforeFnr() {
            var result = comparator.compare(
                    buildOpprettRequest(NPID_IDENT),
                    buildOpprettRequest(FNR_IDENT));

            assertThat(result).isNegative();
        }

        @Test
        void shouldSortFnrAfterNpid() {
            var result = comparator.compare(
                    buildOpprettRequest(FNR_IDENT),
                    buildOpprettRequest(NPID_IDENT));

            assertThat(result).isPositive();
        }

        @Test
        void shouldSortNpidBeforeDnr() {
            var result = comparator.compare(
                    buildOpprettRequest(NPID_IDENT),
                    buildOpprettRequest(DNR_IDENT));

            assertThat(result).isNegative();
        }

        @Test
        void shouldSortDnrAfterNpid() {
            var result = comparator.compare(
                    buildOpprettRequest(DNR_IDENT),
                    buildOpprettRequest(NPID_IDENT));

            assertThat(result).isPositive();
        }

        @Test
        void shouldSortListInCorrectOrder() {
            var fnr = buildOpprettRequest(FNR_IDENT);
            var dnr = buildOpprettRequest(DNR_IDENT);
            var npid = buildOpprettRequest(NPID_IDENT);

            var list = new ArrayList<>(List.of(fnr, npid, dnr));
            list.sort(comparator);

            assertThat(list.get(0).getPerson().getIdent()).isEqualTo(NPID_IDENT);
            assertThat(list.get(1).getPerson().getIdent()).isEqualTo(DNR_IDENT);
            assertThat(list.get(2).getPerson().getIdent()).isEqualTo(FNR_IDENT);
        }

        @Test
        void shouldSortHovedpersonLastAmongSameIdenttype() {
            var hovedperson = buildOpprettRequest(HOVEDPERSON_FNR);
            var fnr = buildOpprettRequest(FNR_IDENT);
            var fnr2 = buildOpprettRequest(FNR_IDENT_2);

            var list = new ArrayList<>(List.of(hovedperson, fnr, fnr2));
            list.sort(comparator);

            assertThat(list.get(2).getPerson().getIdent()).isEqualTo(HOVEDPERSON_FNR);
        }

        @Test
        void shouldSortFullMixWithHovedpersonLast() {
            var hovedperson = buildOpprettRequest(HOVEDPERSON_FNR);
            var fnr = buildOpprettRequest(FNR_IDENT);
            var dnr = buildOpprettRequest(DNR_IDENT);
            var npid = buildOpprettRequest(NPID_IDENT);

            var list = new ArrayList<>(List.of(hovedperson, fnr, dnr, npid));
            list.sort(comparator);

            assertThat(list.get(0).getPerson().getIdent()).isEqualTo(NPID_IDENT);
            assertThat(list.get(1).getPerson().getIdent()).isEqualTo(DNR_IDENT);
            assertThat(list.get(3).getPerson().getIdent()).isEqualTo(HOVEDPERSON_FNR);
        }
    }
}
