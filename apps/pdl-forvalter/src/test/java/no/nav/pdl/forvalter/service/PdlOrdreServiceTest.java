package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.KodeverkConsumer;
import no.nav.pdl.forvalter.database.model.DbAlias;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.pdl.forvalter.database.repository.AliasRepository;
import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.pdl.forvalter.database.repository.RelasjonRepository;
import no.nav.pdl.forvalter.exception.InvalidRequestException;
import no.nav.pdl.forvalter.exception.NotFoundException;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OrdreResponseDTO.PdlStatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PdlArtifact;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
        when(personRepository.findByIdentIn(anyList(), eq(Pageable.unpaged())))
                .thenReturn(Flux.just(dbRelatertPerson));
        when(kodeverkConsumer.getFylkesmannsembeter()).thenReturn(Mono.just(new HashMap<String, String>()));

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
        stubDeployServiceDefaults();
        when(hendelseIdService.oppdaterPerson(any(OrdreResponseDTO.class))).thenReturn(Mono.empty());

        StepVerifier.create(pdlOrdreService.send(HOVEDPERSON_IDENT, null))
                .assertNext(response -> {
                    assertThat(response.getRelasjoner(), hasSize(1));
                })
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
    }

    private void stubDeployServiceForSlettingAndSendOrders(List<PdlStatusDTO> statuses) {
        when(deployService.createOrdre(any(PdlArtifact.class), anyString(), anyList())).thenReturn(Flux.empty());
        when(deployService.sendOrders(any())).thenReturn(Flux.fromIterable(statuses));
    }
}
