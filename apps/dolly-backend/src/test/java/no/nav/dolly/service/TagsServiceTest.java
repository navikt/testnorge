package no.nav.dolly.service;

import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.bestilling.tagshendelseslager.TagsHendelseslagerConsumer;
import no.nav.dolly.bestilling.tagshendelseslager.dto.TagsOpprettingResponse;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagsServiceTest {

    @Mock
    private TagsHendelseslagerConsumer tagsHendelseslagerConsumer;

    @Mock
    private TestgruppeRepository testgruppeRepository;

    @Mock
    private PersonServiceConsumer personServiceConsumer;

    @Mock
    private IdentRepository identRepository;

    @InjectMocks
    private TagsService tagsService;

    @Test
    void shouldThrowNotFoundExceptionWhenGroupDoesNotExist() {
        when(testgruppeRepository.findById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(tagsService.sendTags(1L, List.of("SALESFORCE")))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void shouldReturnOkWhenGroupHasNoIdenter() {
        var gruppe = Testgruppe.builder().id(1L).build();

        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(gruppe));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(gruppe));
        when(identRepository.findByGruppeId(1L, Pageable.unpaged())).thenReturn(Flux.empty());

        StepVerifier.create(tagsService.sendTags(1L, List.of("SALESFORCE")))
                .expectNextMatches(response -> response.getStatus() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void shouldCreateTagsForIdenterWithNoExistingTags() {
        var gruppe = Testgruppe.builder().id(1L).build();
        var testident = Testident.builder().ident("12345678901").build();

        var person = buildPersonWithNoRelations();
        var personBolk = PdlPersonBolk.PersonBolk.builder()
                .ident("12345678901")
                .person(person)
                .build();
        var pdlPersonBolk = PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(personBolk))
                        .build())
                .build();

        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(gruppe));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(gruppe));
        when(identRepository.findByGruppeId(1L, Pageable.unpaged())).thenReturn(Flux.just(testident));
        when(personServiceConsumer.getPdlPersoner(List.of("12345678901"))).thenReturn(Flux.just(pdlPersonBolk));
        when(tagsHendelseslagerConsumer.getTagsBolk(anyList()))
                .thenReturn(Mono.just(Map.of("12345678901", Collections.emptyList())));
        when(tagsHendelseslagerConsumer.createTags(anyList(), anyList()))
                .thenReturn(Mono.just(TagsOpprettingResponse.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(tagsService.sendTags(1L, List.of("SALESFORCE")))
                .expectNextMatches(response -> response.getStatus() == HttpStatus.OK)
                .verifyComplete();

        verify(tagsHendelseslagerConsumer).createTags(anyList(), anyList());
        verify(tagsHendelseslagerConsumer, never()).deleteTags(anyList(), anyList());
    }

    @Test
    void shouldDeleteTagsNoLongerInRequest() {
        var gruppe = Testgruppe.builder().id(1L).build();
        var testident = Testident.builder().ident("12345678901").build();

        var person = buildPersonWithNoRelations();
        var personBolk = PdlPersonBolk.PersonBolk.builder()
                .ident("12345678901")
                .person(person)
                .build();
        var pdlPersonBolk = PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(personBolk))
                        .build())
                .build();

        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(gruppe));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(gruppe));
        when(identRepository.findByGruppeId(1L, Pageable.unpaged())).thenReturn(Flux.just(testident));
        when(personServiceConsumer.getPdlPersoner(List.of("12345678901"))).thenReturn(Flux.just(pdlPersonBolk));
        when(tagsHendelseslagerConsumer.getTagsBolk(anyList()))
                .thenReturn(Mono.just(Map.of("12345678901", List.of("SALESFORCE", "UTBETALING"))));
        when(tagsHendelseslagerConsumer.deleteTags(anyList(), anyList())).thenReturn(Flux.just("deleted"));

        StepVerifier.create(tagsService.sendTags(1L, List.of("SALESFORCE")))
                .expectNextMatches(response -> response.getStatus() == HttpStatus.OK)
                .verifyComplete();

        verify(tagsHendelseslagerConsumer).deleteTags(anyList(), anyList());
    }

    @Test
    void shouldExcludeDollyTagFromProcessing() {
        var gruppe = Testgruppe.builder().id(1L).build();

        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(gruppe));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(gruppe));
        when(identRepository.findByGruppeId(1L, Pageable.unpaged())).thenReturn(Flux.empty());

        StepVerifier.create(tagsService.sendTags(1L, List.of("DOLLY", "SALESFORCE")))
                .expectNextMatches(response -> response.getStatus() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void shouldCollectRelatedPersonsFromSivilstand() {
        var gruppe = Testgruppe.builder().id(1L).build();
        var testident = Testident.builder().ident("12345678901").build();

        var sivilstand = new PdlPerson.Sivilstand();
        sivilstand.setRelatertVedSivilstand("98765432109");

        var person = buildPersonWithNoRelations();
        person.setSivilstand(List.of(sivilstand));

        var personBolk = PdlPersonBolk.PersonBolk.builder()
                .ident("12345678901")
                .person(person)
                .build();
        var pdlPersonBolk = PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(personBolk))
                        .build())
                .build();

        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(gruppe));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(gruppe));
        when(identRepository.findByGruppeId(1L, Pageable.unpaged())).thenReturn(Flux.just(testident));
        when(personServiceConsumer.getPdlPersoner(List.of("12345678901"))).thenReturn(Flux.just(pdlPersonBolk));
        when(tagsHendelseslagerConsumer.getTagsBolk(anyList()))
                .thenReturn(Mono.just(Map.of(
                        "12345678901", Collections.emptyList(),
                        "98765432109", Collections.emptyList())));
        when(tagsHendelseslagerConsumer.createTags(anyList(), anyList()))
                .thenReturn(Mono.just(TagsOpprettingResponse.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(tagsService.sendTags(1L, List.of("SALESFORCE")))
                .expectNextMatches(response -> response.getStatus() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void shouldCollectRelatedPersonsFromForelderBarnRelasjon() {
        var gruppe = Testgruppe.builder().id(1L).build();
        var testident = Testident.builder().ident("12345678901").build();

        var relasjon = new PdlPerson.ForelderBarnRelasjon();
        relasjon.setRelatertPersonsIdent("11111111111");

        var person = buildPersonWithNoRelations();
        person.setForelderBarnRelasjon(List.of(relasjon));

        var personBolk = PdlPersonBolk.PersonBolk.builder()
                .ident("12345678901")
                .person(person)
                .build();
        var pdlPersonBolk = PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(personBolk))
                        .build())
                .build();

        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(gruppe));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(gruppe));
        when(identRepository.findByGruppeId(1L, Pageable.unpaged())).thenReturn(Flux.just(testident));
        when(personServiceConsumer.getPdlPersoner(List.of("12345678901"))).thenReturn(Flux.just(pdlPersonBolk));
        when(tagsHendelseslagerConsumer.getTagsBolk(anyList()))
                .thenReturn(Mono.just(Map.of(
                        "12345678901", Collections.emptyList(),
                        "11111111111", Collections.emptyList())));
        when(tagsHendelseslagerConsumer.createTags(anyList(), anyList()))
                .thenReturn(Mono.just(TagsOpprettingResponse.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(tagsService.sendTags(1L, List.of("SALESFORCE")))
                .expectNextMatches(response -> response.getStatus() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void shouldCollectRelatedPersonsFromForeldreansvar() {
        var gruppe = Testgruppe.builder().id(1L).build();
        var testident = Testident.builder().ident("12345678901").build();

        var foreldreansvar = new ForeldreansvarDTO();
        foreldreansvar.setAnsvarlig("22222222222");

        var person = buildPersonWithNoRelations();
        person.setForeldreansvar(List.of(foreldreansvar));

        var personBolk = PdlPersonBolk.PersonBolk.builder()
                .ident("12345678901")
                .person(person)
                .build();
        var pdlPersonBolk = PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(personBolk))
                        .build())
                .build();

        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(gruppe));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(gruppe));
        when(identRepository.findByGruppeId(1L, Pageable.unpaged())).thenReturn(Flux.just(testident));
        when(personServiceConsumer.getPdlPersoner(List.of("12345678901"))).thenReturn(Flux.just(pdlPersonBolk));
        when(tagsHendelseslagerConsumer.getTagsBolk(anyList()))
                .thenReturn(Mono.just(Map.of(
                        "12345678901", Collections.emptyList(),
                        "22222222222", Collections.emptyList())));
        when(tagsHendelseslagerConsumer.createTags(anyList(), anyList()))
                .thenReturn(Mono.just(TagsOpprettingResponse.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(tagsService.sendTags(1L, List.of("SALESFORCE")))
                .expectNextMatches(response -> response.getStatus() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void shouldCollectRelatedPersonsFromFullmakt() {
        var gruppe = Testgruppe.builder().id(1L).build();
        var testident = Testident.builder().ident("12345678901").build();

        var fullmakt = new FullmaktDTO();
        fullmakt.setMotpartsPersonident("33333333333");

        var person = buildPersonWithNoRelations();
        person.setFullmakt(List.of(fullmakt));

        var personBolk = PdlPersonBolk.PersonBolk.builder()
                .ident("12345678901")
                .person(person)
                .build();
        var pdlPersonBolk = PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(personBolk))
                        .build())
                .build();

        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(gruppe));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(gruppe));
        when(identRepository.findByGruppeId(1L, Pageable.unpaged())).thenReturn(Flux.just(testident));
        when(personServiceConsumer.getPdlPersoner(List.of("12345678901"))).thenReturn(Flux.just(pdlPersonBolk));
        when(tagsHendelseslagerConsumer.getTagsBolk(anyList()))
                .thenReturn(Mono.just(Map.of(
                        "12345678901", Collections.emptyList(),
                        "33333333333", Collections.emptyList())));
        when(tagsHendelseslagerConsumer.createTags(anyList(), anyList()))
                .thenReturn(Mono.just(TagsOpprettingResponse.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(tagsService.sendTags(1L, List.of("SALESFORCE")))
                .expectNextMatches(response -> response.getStatus() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void shouldCollectRelatedPersonsFromVergemaal() {
        var gruppe = Testgruppe.builder().id(1L).build();
        var testident = Testident.builder().ident("12345678901").build();

        var vergeEllerFullmektig = new PdlPerson.VergeEllerFullmektig();
        vergeEllerFullmektig.setMotpartsPersonident("44444444444");
        var vergemaal = new PdlPerson.Vergemaal();
        vergemaal.setVergeEllerFullmektig(vergeEllerFullmektig);

        var person = buildPersonWithNoRelations();
        person.setVergemaalEllerFremtidsfullmakt(List.of(vergemaal));

        var personBolk = PdlPersonBolk.PersonBolk.builder()
                .ident("12345678901")
                .person(person)
                .build();
        var pdlPersonBolk = PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(personBolk))
                        .build())
                .build();

        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(gruppe));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(gruppe));
        when(identRepository.findByGruppeId(1L, Pageable.unpaged())).thenReturn(Flux.just(testident));
        when(personServiceConsumer.getPdlPersoner(List.of("12345678901"))).thenReturn(Flux.just(pdlPersonBolk));
        when(tagsHendelseslagerConsumer.getTagsBolk(anyList()))
                .thenReturn(Mono.just(Map.of(
                        "12345678901", Collections.emptyList(),
                        "44444444444", Collections.emptyList())));
        when(tagsHendelseslagerConsumer.createTags(anyList(), anyList()))
                .thenReturn(Mono.just(TagsOpprettingResponse.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(tagsService.sendTags(1L, List.of("SALESFORCE")))
                .expectNextMatches(response -> response.getStatus() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void shouldCollectRelatedPersonsFromKontaktinformasjonForDoedsbo() {
        var gruppe = Testgruppe.builder().id(1L).build();
        var testident = Testident.builder().ident("12345678901").build();

        var kontaktperson = new KontaktinformasjonForDoedsboDTO.KontaktpersonDTO();
        kontaktperson.setIdentifikasjonsnummer("55555555555");
        var kontakt = new KontaktinformasjonForDoedsboDTO();
        kontakt.setPersonSomKontakt(kontaktperson);

        var person = buildPersonWithNoRelations();
        person.setKontaktinformasjonForDoedsbo(List.of(kontakt));

        var personBolk = PdlPersonBolk.PersonBolk.builder()
                .ident("12345678901")
                .person(person)
                .build();
        var pdlPersonBolk = PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(personBolk))
                        .build())
                .build();

        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(gruppe));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(gruppe));
        when(identRepository.findByGruppeId(1L, Pageable.unpaged())).thenReturn(Flux.just(testident));
        when(personServiceConsumer.getPdlPersoner(List.of("12345678901"))).thenReturn(Flux.just(pdlPersonBolk));
        when(tagsHendelseslagerConsumer.getTagsBolk(anyList()))
                .thenReturn(Mono.just(Map.of(
                        "12345678901", Collections.emptyList(),
                        "55555555555", Collections.emptyList())));
        when(tagsHendelseslagerConsumer.createTags(anyList(), anyList()))
                .thenReturn(Mono.just(TagsOpprettingResponse.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(tagsService.sendTags(1L, List.of("SALESFORCE")))
                .expectNextMatches(response -> response.getStatus() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void shouldFilterNullSivilstandRelation() {
        var gruppe = Testgruppe.builder().id(1L).build();
        var testident = Testident.builder().ident("12345678901").build();

        var sivilstand = new PdlPerson.Sivilstand();
        sivilstand.setRelatertVedSivilstand(null);

        var person = buildPersonWithNoRelations();
        person.setSivilstand(List.of(sivilstand));

        var personBolk = PdlPersonBolk.PersonBolk.builder()
                .ident("12345678901")
                .person(person)
                .build();
        var pdlPersonBolk = PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(personBolk))
                        .build())
                .build();

        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(gruppe));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(gruppe));
        when(identRepository.findByGruppeId(1L, Pageable.unpaged())).thenReturn(Flux.just(testident));
        when(personServiceConsumer.getPdlPersoner(List.of("12345678901"))).thenReturn(Flux.just(pdlPersonBolk));
        when(tagsHendelseslagerConsumer.getTagsBolk(anyList()))
                .thenReturn(Mono.just(Map.of("12345678901", Collections.emptyList())));
        when(tagsHendelseslagerConsumer.createTags(anyList(), anyList()))
                .thenReturn(Mono.just(TagsOpprettingResponse.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(tagsService.sendTags(1L, List.of("SALESFORCE")))
                .expectNextMatches(response -> response.getStatus() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void shouldFilterNullPersonBolk() {
        var gruppe = Testgruppe.builder().id(1L).build();
        var testident = Testident.builder().ident("12345678901").build();

        var personBolk = PdlPersonBolk.PersonBolk.builder()
                .ident("12345678901")
                .person(null)
                .build();
        var pdlPersonBolk = PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(personBolk))
                        .build())
                .build();

        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(gruppe));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(gruppe));
        when(identRepository.findByGruppeId(1L, Pageable.unpaged())).thenReturn(Flux.just(testident));
        when(personServiceConsumer.getPdlPersoner(List.of("12345678901"))).thenReturn(Flux.just(pdlPersonBolk));
        when(tagsHendelseslagerConsumer.getTagsBolk(anyList()))
                .thenReturn(Mono.just(Map.of("12345678901", Collections.emptyList())));

        StepVerifier.create(tagsService.sendTags(1L, List.of("SALESFORCE")))
                .expectNextMatches(response -> response.getStatus() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void shouldHandleBothCreateAndDeleteTags() {
        var gruppe = Testgruppe.builder().id(1L).build();
        var testident = Testident.builder().ident("12345678901").build();

        var person = buildPersonWithNoRelations();
        var personBolk = PdlPersonBolk.PersonBolk.builder()
                .ident("12345678901")
                .person(person)
                .build();
        var pdlPersonBolk = PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(personBolk))
                        .build())
                .build();

        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(gruppe));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(gruppe));
        when(identRepository.findByGruppeId(1L, Pageable.unpaged())).thenReturn(Flux.just(testident));
        when(personServiceConsumer.getPdlPersoner(List.of("12345678901"))).thenReturn(Flux.just(pdlPersonBolk));
        when(tagsHendelseslagerConsumer.getTagsBolk(anyList()))
                .thenReturn(Mono.just(Map.of("12345678901", List.of("SALESFORCE"))));
        when(tagsHendelseslagerConsumer.deleteTags(anyList(), anyList())).thenReturn(Flux.just("deleted"));
        when(tagsHendelseslagerConsumer.createTags(anyList(), anyList()))
                .thenReturn(Mono.just(TagsOpprettingResponse.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(tagsService.sendTags(1L, List.of("UTBETALING")))
                .expectNextMatches(response -> response.getStatus() == HttpStatus.OK)
                .verifyComplete();

        verify(tagsHendelseslagerConsumer).deleteTags(anyList(), anyList());
        verify(tagsHendelseslagerConsumer).createTags(anyList(), anyList());
    }

    @Test
    void shouldNotCreateOrDeleteWhenTagsAlreadyMatch() {
        var gruppe = Testgruppe.builder().id(1L).build();
        var testident = Testident.builder().ident("12345678901").build();

        var person = buildPersonWithNoRelations();
        var personBolk = PdlPersonBolk.PersonBolk.builder()
                .ident("12345678901")
                .person(person)
                .build();
        var pdlPersonBolk = PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(personBolk))
                        .build())
                .build();

        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(gruppe));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(gruppe));
        when(identRepository.findByGruppeId(1L, Pageable.unpaged())).thenReturn(Flux.just(testident));
        when(personServiceConsumer.getPdlPersoner(List.of("12345678901"))).thenReturn(Flux.just(pdlPersonBolk));
        when(tagsHendelseslagerConsumer.getTagsBolk(anyList()))
                .thenReturn(Mono.just(Map.of("12345678901", List.of("SALESFORCE"))));

        StepVerifier.create(tagsService.sendTags(1L, List.of("SALESFORCE")))
                .expectNextMatches(response -> response.getStatus() == HttpStatus.OK)
                .verifyComplete();

        verify(tagsHendelseslagerConsumer, never()).deleteTags(anyList(), anyList());
        verify(tagsHendelseslagerConsumer, never()).createTags(anyList(), anyList());
    }

    @Test
    void shouldFilterNullPdlBolkData() {
        var gruppe = Testgruppe.builder().id(1L).build();
        var testident = Testident.builder().ident("12345678901").build();

        var pdlPersonBolkNull = PdlPersonBolk.builder().data(null).build();

        var person = buildPersonWithNoRelations();
        var validPersonBolk = PdlPersonBolk.PersonBolk.builder()
                .ident("12345678901")
                .person(person)
                .build();
        var pdlPersonBolkValid = PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(validPersonBolk))
                        .build())
                .build();

        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(gruppe));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(gruppe));
        when(identRepository.findByGruppeId(1L, Pageable.unpaged())).thenReturn(Flux.just(testident));
        when(personServiceConsumer.getPdlPersoner(List.of("12345678901")))
                .thenReturn(Flux.just(pdlPersonBolkNull, pdlPersonBolkValid));
        when(tagsHendelseslagerConsumer.getTagsBolk(anyList()))
                .thenReturn(Mono.just(Map.of("12345678901", Collections.emptyList())));
        when(tagsHendelseslagerConsumer.createTags(anyList(), anyList()))
                .thenReturn(Mono.just(TagsOpprettingResponse.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(tagsService.sendTags(1L, List.of("SALESFORCE")))
                .expectNextMatches(response -> response.getStatus() == HttpStatus.OK)
                .verifyComplete();
    }

    @Test
    void shouldHandleMultipleIdenter() {
        var gruppe = Testgruppe.builder().id(1L).build();
        var testident1 = Testident.builder().ident("12345678901").build();
        var testident2 = Testident.builder().ident("98765432109").build();

        var person1 = buildPersonWithNoRelations();
        var person2 = buildPersonWithNoRelations();

        var personBolk1 = PdlPersonBolk.PersonBolk.builder()
                .ident("12345678901")
                .person(person1)
                .build();
        var personBolk2 = PdlPersonBolk.PersonBolk.builder()
                .ident("98765432109")
                .person(person2)
                .build();
        var pdlPersonBolk = PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(personBolk1, personBolk2))
                        .build())
                .build();

        when(testgruppeRepository.findById(1L)).thenReturn(Mono.just(gruppe));
        when(testgruppeRepository.save(any(Testgruppe.class))).thenReturn(Mono.just(gruppe));
        when(identRepository.findByGruppeId(1L, Pageable.unpaged()))
                .thenReturn(Flux.just(testident1, testident2));
        when(personServiceConsumer.getPdlPersoner(anyList())).thenReturn(Flux.just(pdlPersonBolk));
        when(tagsHendelseslagerConsumer.getTagsBolk(anyList()))
                .thenReturn(Mono.just(Map.of(
                        "12345678901", Collections.emptyList(),
                        "98765432109", Collections.emptyList())));
        when(tagsHendelseslagerConsumer.createTags(anyList(), anyList()))
                .thenReturn(Mono.just(TagsOpprettingResponse.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(tagsService.sendTags(1L, List.of("SALESFORCE")))
                .expectNextMatches(response -> response.getStatus() == HttpStatus.OK)
                .verifyComplete();
    }

    private PdlPerson.Person buildPersonWithNoRelations() {
        var person = new PdlPerson.Person();
        person.setSivilstand(new ArrayList<>());
        person.setForelderBarnRelasjon(new ArrayList<>());
        person.setForeldreansvar(new ArrayList<>());
        person.setFullmakt(new ArrayList<>());
        person.setVergemaalEllerFremtidsfullmakt(new ArrayList<>());
        person.setKontaktinformasjonForDoedsbo(new ArrayList<>());
        return person;
    }
}