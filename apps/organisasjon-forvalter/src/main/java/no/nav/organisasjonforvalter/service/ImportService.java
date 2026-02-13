package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.organisasjonforvalter.consumer.MiljoerServiceConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonServiceConsumer;
import no.nav.organisasjonforvalter.dto.responses.RsOrganisasjon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportService {

    private final MiljoerServiceConsumer miljoerServiceConsumer;
    private final OrganisasjonServiceConsumer organisasjonServiceConsumer;
    private final MapperFacade mapperFacade;

    @Transactional
    public Mono<Map<String, RsOrganisasjon>> getOrganisasjoner(String orgnummer, Set<String> miljoer) {

        return miljoerServiceConsumer.getOrgMiljoer()
                .map(tilgjengeligeMiljoer -> {
                    if (nonNull(miljoer) && !miljoer.isEmpty()) {
                        return tilgjengeligeMiljoer.stream()
                                .filter(miljoer::contains)
                                .collect(Collectors.toSet());
                    }
                    return tilgjengeligeMiljoer;
                })
                .flatMap(miljoerAaSjekke ->
                        organisasjonServiceConsumer.getStatus(Set.of(orgnummer), miljoerAaSjekke)
                                .map(Map::entrySet)
                                .flatMap(Flux::fromIterable)
                                .filter(org -> isNotBlank(org.getValue().getOrgnummer()))
                                .collect(Collectors.toMap(Map.Entry::getKey,
                                        org -> mapperFacade.map(org.getValue(), RsOrganisasjon.class))));
    }
}
