package no.nav.dolly.provider;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.bestilling.tagshendelseslager.TagsHendelseslagerConsumer;
import no.nav.dolly.bestilling.tagshendelseslager.dto.TagsOpprettingResponse;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.domain.resultset.Tags.TagBeskrivelse;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

@Slf4j
@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/tags", produces = MediaType.APPLICATION_JSON_VALUE)
public class TagController {

    private final TestgruppeRepository testgruppeRepository;
    private final TagsHendelseslagerConsumer tagsHendelseslagerConsumer;
    private final PersonServiceConsumer personServiceConsumer;
    private final IdentRepository identRepository;

    @GetMapping()
    @Transactional
    @Operation(description = "Hent alle gyldige Tags")
    public Flux<TagBeskrivelse> hentAlleTags() {

        return Flux.fromArray(Tags.values())
                .filter(tags -> !tags.name().equals(Tags.DOLLY.name()))
                .map(tag -> TagBeskrivelse.builder().tag(tag.name()).beskrivelse(tag.getBeskrivelse()).build());
    }

    @GetMapping("/ident/{ident}")
    @Transactional
    @Operation(description = "Hent tags på ident")
    public Mono<JsonNode> hentTagsPaaIdent(@PathVariable("ident") String ident) {

        return tagsHendelseslagerConsumer.getTag(ident);
    }

    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @PostMapping("/gruppe/{gruppeId}")
    @Transactional
    @Operation(description = "Send tags på gruppe")
    public Mono<TagsOpprettingResponse> sendTagsPaaGruppe(@RequestBody List<Tags> tags,
                                                          @PathVariable("gruppeId") Long gruppeId) {

        return testgruppeRepository.findById(gruppeId)
                .flatMap(gruppe -> identRepository.findByGruppeId(gruppeId, Pageable.unpaged())
                        .map(Testident::getIdent)
                        .collectList()
                        .flatMap(identer -> personServiceConsumer.getPdlPersoner(identer)
                                .filter(pdlBolk -> nonNull(pdlBolk) && nonNull(pdlBolk.getData()))
                                .map(PdlPersonBolk::getData)
                                .map(PdlPersonBolk.Data::getHentPersonBolk)
                                .flatMap(Flux::fromIterable)
                                .filter(personBolk -> nonNull(personBolk.getPerson()))
                                .map(person -> Stream.of(Stream.of(person.getIdent()),
                                                person.getPerson().getSivilstand().stream()
                                                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                                        .filter(Objects::nonNull),
                                                person.getPerson().getForelderBarnRelasjon().stream()
                                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent),
                                                person.getPerson().getForeldreansvar().stream()
                                                        .map(ForeldreansvarDTO::getAnsvarlig)
                                                        .filter(Objects::nonNull),
                                                person.getPerson().getFullmakt().stream()
                                                        .map(FullmaktDTO::getMotpartsPersonident)
                                                        .filter(Objects::nonNull),
                                                person.getPerson().getVergemaalEllerFremtidsfullmakt().stream()
                                                        .map(PdlPerson.Vergemaal::getVergeEllerFullmektig)
                                                        .map(PdlPerson.VergeEllerFullmektig::getMotpartsPersonident)
                                                        .filter(Objects::nonNull),
                                                person.getPerson().getKontaktinformasjonForDoedsbo().stream()
                                                        .map(KontaktinformasjonForDoedsboDTO::getPersonSomKontakt)
                                                        .filter(Objects::nonNull)
                                                        .map(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::getIdentifikasjonsnummer)
                                                        .filter(Objects::nonNull))
                                        .flatMap(Function.identity())
                                        .distinct()
                                        .toList())
                                .flatMap(Flux::fromIterable)
                                .collectList()
                                .flatMap(personBolk -> tagsHendelseslagerConsumer.createTags(personBolk, tags))))
                .switchIfEmpty(Mono.error(new NotFoundException(String.format("Fant ikke gruppe på id: %s", gruppeId))));
    }
}