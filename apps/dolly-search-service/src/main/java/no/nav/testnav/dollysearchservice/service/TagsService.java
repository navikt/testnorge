package no.nav.testnav.dollysearchservice.service;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.consumer.PdlProxyConsumer;
import no.nav.testnav.dollysearchservice.dto.TagsOpprettingResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagsService {

    private static final int BOLK_SIZE = 1000;

    private final BestillingQueryService bestillingQueryService;
    private final PdlProxyConsumer pdlProxyConsumer;

    public Mono<TagsOpprettingResponse> setDollyTagAlleTestnorgeIdenter() {

        var identer = bestillingQueryService.execTestnorgeIdenterQuery();
        log.info("Hentet alle testnorge identer: {}", identer.size());
        var bolker = Lists.partition(identer.stream().toList(), BOLK_SIZE);
        log.info("Antall bolker: {}", bolker.size());

        return Flux.fromIterable(bolker)
                .flatMap(pdlProxyConsumer::getTags)
                .map(TagsService::filterTags)
                .reduce(new ArrayList<String>(), (l1, l2) -> {
                    l1.addAll(l2);
                    return l1;
                })
                .filter(tags -> !tags.isEmpty())
                .doOnNext(tags -> log.info("Identer som mangler Dolly-tags: {}", String.join(", ", tags)))
                .flatMap(pdlProxyConsumer::setTags)
                .switchIfEmpty(Mono.just(TagsOpprettingResponse.builder()
                        .message("Fant ingen personer som mangler Dolly-tag")
                        .build()));
    }

    private static List<String> filterTags(Map<String, List<String>> tags) {

        return tags.entrySet().stream()
                .filter(entry -> !entry.getValue().contains("DOLLY"))
                .map(Map.Entry::getKey)
                .toList();
    }
}