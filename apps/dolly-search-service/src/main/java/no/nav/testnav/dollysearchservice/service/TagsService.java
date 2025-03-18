package no.nav.testnav.dollysearchservice.service;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.dollysearchservice.consumer.PdlDataConsumer;
import no.nav.testnav.dollysearchservice.dto.TagsOpprettingResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagsService {

    private static final int BOLK_SIZE = 500;

    private final BestillingQueryService bestillingQueryService;
    private final PdlDataConsumer pdlDataConsumer;

    public Mono<String> setDollyTagAlleTestnorgeIdenter() {

        var identer = bestillingQueryService.execTestnorgeIdenterQuery();
        var bolker = Lists.partition(identer.stream().toList(), BOLK_SIZE);

        return Flux.fromIterable(bolker)
                .flatMap(pdlDataConsumer::getTags)
                .map(TagsService::filterTags)
                .reduce(new ArrayList<String>(), (l1, l2) -> {
                    l1.addAll(l2);
                    return l1;
                })
                .doOnNext(tags -> log.info("Identer som mangler Dolly-tags: {}", String.join(", ", tags)))
                .flatMap(pdlDataConsumer::setTags)
                .filter(response -> response.getStatus().is2xxSuccessful())
                .map(TagsOpprettingResponse::getIdenter)
                .map(resultat -> "Lagt til DOLLY-tag på %s".formatted(String.join(", ", resultat)))
    }
    private static List<String> filterTags(Map<String, List<String>> tags) {

        return tags.entrySet().stream()
                .filter(entry -> !entry.getValue().contains("DOLLY"))
                .map(Map.Entry::getKey)
                .toList();
    }
}
