package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.organisasjonforvalter.consumer.EregServicesConsumer;
import no.nav.organisasjonforvalter.consumer.MiljoerServiceConsumer;
import no.nav.organisasjonforvalter.dto.responses.RsOrganisasjon;
import no.nav.organisasjonforvalter.mapper.MappingContextUtils;
import no.nav.testnav.libs.dto.ereg.v1.EregServicesResponse;
import no.nav.testnav.libs.dto.ereg.v1.InngaarIJuridiskEnhet;
import no.nav.testnav.libs.dto.ereg.v1.JuridiskEnhet;
import no.nav.testnav.libs.dto.ereg.v1.Organisasjon;
import no.nav.testnav.libs.dto.ereg.v1.OrganisasjonBase;
import no.nav.testnav.libs.dto.ereg.v1.Organisasjonsledd;
import no.nav.testnav.libs.dto.ereg.v1.Virksomhet;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class EregStatusesService {

    private static final int MAX_DEPTH = 100;

    private final EregServicesConsumer eregServicesConsumer;
    private final MiljoerServiceConsumer miljoerServiceConsumer;
    private final ObjectMapper objectMapper;
    private final MapperFacade mapperFacade;

    public Flux<Map<String, RsOrganisasjon>> getOrganisasjoner(String orgnummer, Set<String> miljoer) {

        return (nonNull(miljoer) && !miljoer.isEmpty() ?
                miljoerServiceConsumer.getOrgMiljoer()
                        .flatMapMany(Flux::fromIterable)
                        .filter(miljoer::contains)
                        .collect(Collectors.toSet()) :
                miljoerServiceConsumer.getOrgMiljoer())
                .flatMapMany(miljoerAaSjekke -> eregServicesConsumer.getStatus(orgnummer, miljoerAaSjekke)
                        .doOnNext(resultat -> log.info("Mottatt fra Ereg: {}", resultat))
                        .flatMap(eregVirksomhet -> {

                            if (nonNull(eregVirksomhet.getOrganisasjon())) {
                                return getOrganisasjon(eregVirksomhet.getOrganisasjon())
                                        .map(organisasjon -> {
                                            val opplysningspliktige = finnOpplysningspliktige(organisasjon);
                                            log.info("Fant opplysningspliktige: for miljoe: {}, {}", eregVirksomhet.getMiljoe(), opplysningspliktige);
                                            val context = MappingContextUtils.getMappingContext();
                                            context.setProperty("opplysningspliktige", opplysningspliktige);
                                            return Map.of(eregVirksomhet.getMiljoe(), mapperFacade.map(organisasjon, RsOrganisasjon.class, context));
                                        });
                            } else {
                                return getError(eregVirksomhet);
                            }
                        }));
    }

    private Mono<Map<String, RsOrganisasjon>> getError(EregServicesResponse eregVirksomhet) {

        if (isBlank(eregVirksomhet.getError()) || eregVirksomhet.getError().contains("Ingen organisasjon")) {

            return Mono.empty();

        } else {

            return Mono.just(Map.of(eregVirksomhet.getMiljoe(), RsOrganisasjon.builder()
                    .error(eregVirksomhet.getError())
                    .build()));
        }
    }

    private Mono<Organisasjon> getOrganisasjon(JsonNode organisasjon) {

        return switch (organisasjon.get("type").asText()) {
            case "Organisasjon" -> Mono.just(objectMapper.convertValue(organisasjon, Organisasjon.class));
            case "Virksomhet" -> Mono.just(objectMapper.convertValue(organisasjon, Virksomhet.class));
            case "Organisasjonsledd" -> Mono.just(objectMapper.convertValue(organisasjon, Organisasjonsledd.class));
            case "JuridiskEnhet" -> Mono.just(objectMapper.convertValue(organisasjon, JuridiskEnhet.class));
            default -> Mono.empty();
        };
    }

    private Set<String> finnOpplysningspliktige(Organisasjon eregVirksomhet) {

        var result = new HashSet<String>();

        OrganisasjonBase current = eregVirksomhet;

        var depth = 0;
        var traverse = true;
        do {
            if (nonNull(current) && current instanceof InngaarIJuridiskEnhet inngaarIJuridiskEnhet) {
                result.add(inngaarIJuridiskEnhet.getOrganisasjonsnummer());

                traverse = false;
            }

            if (nonNull(current) && current instanceof Organisasjonsledd orgledd) {
                result.add(orgledd.getOrganisasjonsnummer());

                var overOrdnedeJuridiskeEnheter = orgledd.getInngaarIJuridiskEnheter();
                var overOrdnedeOrganisasjonsledd = orgledd.getOrganisasjonsleddOver();
                if (!overOrdnedeJuridiskeEnheter.isEmpty()) {
                    current = overOrdnedeJuridiskeEnheter.getFirst();
                } else {
                    current = overOrdnedeOrganisasjonsledd.isEmpty() ? null :
                            overOrdnedeOrganisasjonsledd.getFirst().getOrganisasjonsledd();
                }
            }

            if (nonNull(current) && current instanceof Virksomhet virksomhet) {
                var overOrdnedeJuridiskeEnheter = virksomhet.getInngaarIJuridiskEnheter();
                var overOrdnedeOrganisasjonsledd = virksomhet.getBestaarAvOrganisasjonsledd();
                if (!overOrdnedeJuridiskeEnheter.isEmpty()) {
                    overOrdnedeJuridiskeEnheter.getFirst();
                    current = overOrdnedeJuridiskeEnheter.isEmpty() ? null :
                            overOrdnedeJuridiskeEnheter.getFirst();
                } else {
                    if (overOrdnedeOrganisasjonsledd.isEmpty()) {
                        log.info("Fant ikke organisasjonsledd for virksomhet: {}", virksomhet.getOrganisasjonsnummer());
                        traverse = false;
                    } else {
                        current = overOrdnedeOrganisasjonsledd.getFirst().getOrganisasjonsledd();
                    }
                }
            }
        } while (traverse && depth++ < MAX_DEPTH);

        return result;
    }
}
