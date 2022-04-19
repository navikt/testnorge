package no.nav.dolly.provider.api;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.tagshendelseslager.TagsHendelseslagerConsumer;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForeldreansvarDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktinformasjonForDoedsboDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
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
    private final PdlPersonConsumer pdlPersonConsumer;

    @GetMapping()
    @Transactional
    @Operation(description = "Hent alle gyldige Tags")
    public Set<Tags.TagBeskrivelse> hentAlleTags() {

        return Arrays.stream(Tags.values())
                .filter(tags -> !tags.name().equals(Tags.DOLLY.name()))
                .map(tag -> Tags.TagBeskrivelse.builder().tag(tag.name()).beskrivelse(tag.getBeskrivelse()).build())
                .collect(Collectors.toSet());
    }

    @GetMapping("/ident/{ident}")
    @Transactional
    @Operation(description = "Hent tags på ident")
    public JsonNode hentTagsPaaIdent(@PathVariable("ident") String ident) {

        return tagsHendelseslagerConsumer.getTag(ident);
    }

    @CacheEvict(value = CACHE_GRUPPE, allEntries = true)
    @PostMapping("/gruppe/{gruppeId}")
    @Transactional
    @Operation(description = "Send tags på gruppe")
    public String sendTagsPaaGruppe(@RequestBody List<Tags> tags,
                                    @PathVariable("gruppeId") Long gruppeId) {

        var testgruppe = testgruppeRepository.findById(gruppeId)
                .orElseThrow(() -> new NotFoundException(String.format("Fant ikke gruppe på id: %s", gruppeId)));

        var gruppeIdenter = testgruppe.getTestidenter()
                .stream()
                .map(Testident::getIdent)
                .toList();

        var pdlPersonBolk = pdlPersonConsumer.getPdlPersoner(gruppeIdenter)
                .filter(pdlBolk -> nonNull(pdlBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()))
                .map(person -> Stream.of(List.of(person.getIdent()),
                                person.getPerson().getSivilstand().stream()
                                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getPerson().getForelderBarnRelasjon().stream()
                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                        .toList(),
                                person.getPerson().getForeldreansvar().stream()
                                        .map(ForeldreansvarDTO::getAnsvarlig)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getPerson().getFullmakt().stream()
                                        .map(FullmaktDTO::getMotpartsPersonident)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getPerson().getVergemaalEllerFremtidsfullmakt().stream()
                                        .map(PdlPerson.Vergemaal::getVergeEllerFullmektig)
                                        .map(PdlPerson.VergeEllerFullmektig::getMotpartsPersonident)
                                        .filter(Objects::nonNull)
                                        .toList(),
                                person.getPerson().getKontaktinformasjonForDoedsbo().stream()
                                        .map(KontaktinformasjonForDoedsboDTO::getPersonSomKontakt)
                                        .filter(Objects::nonNull)
                                        .map(KontaktinformasjonForDoedsboDTO.KontaktpersonDTO::getIdentifikasjonsnummer)
                                        .filter(Objects::nonNull)
                                        .toList())
                        .flatMap(Collection::stream)
                        .distinct()
                        .toList())
                .filter(Objects::nonNull)
                .flatMap(Flux::fromIterable)
                .collectList()
                .block();

        var tagsTilSletting = testgruppe.getTags().stream()
                .filter(eksisterendeTag -> tags.stream()
                        .noneMatch(nyTag -> eksisterendeTag.name().equals(nyTag.name())))
                .toList();

        testgruppe.setTags(tags.isEmpty() ? null
                : tags.stream()
                .map(Tags::name)
                .collect(Collectors.joining(",")));

        if (!tagsTilSletting.isEmpty()) {
            tagsHendelseslagerConsumer.deleteTags(pdlPersonBolk, tagsTilSletting)
                    .collectList()
                    .subscribe();
        }

        return tagsHendelseslagerConsumer.createTags(pdlPersonBolk, tags)
                .collect(Collectors.joining(", "))
                .block();
    }
}