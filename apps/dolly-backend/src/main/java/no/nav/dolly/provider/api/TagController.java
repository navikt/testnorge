package no.nav.dolly.provider.api;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.tagshendelseslager.TagsHendelseslagerConsumer;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.TestgruppeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;

@Slf4j
@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/tags", produces = MediaType.APPLICATION_JSON_VALUE)
public class TagController {

    private final TestgruppeRepository testgruppeRepository;
    private final TagsHendelseslagerConsumer tagsHendelseslagerConsumer;

    @GetMapping()
    @Transactional
    @Operation(description = "Hent alle gyldige Tags")
    public Set<Tags.TagBeskrivelse> hentAlleTags() {

        return Arrays.stream(Tags.values())
                .map(tag -> Tags.TagBeskrivelse.builder().tag(tag.name()).beskrivelse(tag.getBeskrivelse()).build())
                .collect(Collectors.toSet());
    }

    @CacheEvict(value = CACHE_BESTILLING, allEntries = true)
    @PostMapping("/gruppe/{gruppeId}")
    @Transactional
    @Operation(description = "Send tags på gruppe")
    public Object sendTagsPaaGruppe(@RequestBody Set<Tags> tags, Long gruppeId) {
        Optional<Testgruppe> byId = testgruppeRepository.findById(gruppeId);
        Testgruppe testgruppe = byId
                .orElseThrow(() -> new NotFoundException(String.format("Fant ikke gruppe på id: %s", gruppeId)));
        log.info("gruppe: ", Json.pretty(byId.get()));
        byId.get().setTags(tags.stream().map(Tags::name).collect(Collectors.toSet()));
        return tagsHendelseslagerConsumer.createTags(testgruppe.getTestidenter()
                        .stream()
                        .map(Testident::getIdent)
                        .collect(Collectors.toList()),
                tags.stream().map(Tags::name)
                        .collect(Collectors.toList()));
    }
}