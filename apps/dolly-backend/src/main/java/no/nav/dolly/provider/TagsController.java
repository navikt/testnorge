package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.tagshendelseslager.TagsHendelseslagerConsumer;
import no.nav.dolly.bestilling.tagshendelseslager.dto.TagsOpprettingResponse;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.dolly.domain.resultset.Tags.TagBeskrivelse;
import no.nav.dolly.service.TagsService;
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
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.util.List;

import static no.nav.dolly.config.CachingConfig.CACHE_GRUPPE;

@Slf4j
@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/tags", produces = MediaType.APPLICATION_JSON_VALUE)
public class TagsController {

    private final TagsHendelseslagerConsumer tagsHendelseslagerConsumer;
    private final TagsService tagsService;

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
    public Mono<TagsOpprettingResponse> sendTagsPaaGruppe(@RequestBody List<String> tags,
                                                          @PathVariable("gruppeId") Long gruppeId) {

        return tagsService.sendTags(gruppeId, tags);
    }
}