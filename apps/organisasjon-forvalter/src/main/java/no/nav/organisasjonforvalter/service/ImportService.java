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
    public Map<String, RsOrganisasjon> getOrganisasjoner(String orgnummer, Set<String> miljoer) {

        var miljoerAaSjekke = nonNull(miljoer) && !miljoer.isEmpty() ?
                miljoerServiceConsumer.getOrgMiljoer().stream()
                        .filter(miljoer::contains)
                        .collect(Collectors.toSet()) :
                miljoerServiceConsumer.getOrgMiljoer();

        log.info("Sjekker organisasjon {} i miljøer: {}", orgnummer, miljoerAaSjekke);

          return organisasjonServiceConsumer.getStatus(Set.of(orgnummer), miljoerAaSjekke)
                  .doOnNext(orgMap -> log.info("Mottok organisasjon fra organisasjon-service: {}", orgMap))
                  .map(Map::entrySet)
                  .flatMap(Flux::fromIterable)
                  .filter(org -> {
                      boolean hasOrgnummer = isNotBlank(org.getValue().getOrgnummer());
                      log.info("Organisasjon {} fra miljø {} har orgnummer: {}", 
                              org.getValue().getOrgnummer(), org.getKey(), hasOrgnummer);
                      return hasOrgnummer;
                  })
                  .collect(Collectors.toMap(Map.Entry::getKey,
                          org -> {
                              RsOrganisasjon rsOrg = mapperFacade.map(org.getValue(), RsOrganisasjon.class);
                              log.info("Mappet til RsOrganisasjon: {}", rsOrg);
                              return rsOrg;
                          }))
                  .block();
    }
}
