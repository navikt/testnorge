package no.nav.organisasjonforvalter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.organisasjonforvalter.consumer.EregServicesConsumer;
import no.nav.organisasjonforvalter.consumer.MiljoerServiceConsumer;
import no.nav.organisasjonforvalter.dto.responses.RsOrganisasjon;
import no.nav.organisasjonforvalter.mapper.MappingContextUtils;
import no.nav.testnav.libs.dto.ereg.v1.InngaarIJuridiskEnhet;
import no.nav.testnav.libs.dto.ereg.v1.JuridiskEnhet;
import no.nav.testnav.libs.dto.ereg.v1.Organisasjon;
import no.nav.testnav.libs.dto.ereg.v1.OrganisasjonBase;
import no.nav.testnav.libs.dto.ereg.v1.Organisasjonsledd;
import no.nav.testnav.libs.dto.ereg.v1.Virksomhet;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class EregStatusesService {

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
                        .map(eregVirksomhet -> {
                            if (nonNull(eregVirksomhet.getOrganisasjon())) {

                                val organisasjon = getObject(eregVirksomhet.getOrganisasjon());
                                val opplysningspliktige = finnOpplysningspliktige(organisasjon);
                                log.info("Fant opplysningspliktige: for miljoe: {}, {}", eregVirksomhet.getMiljoe(), opplysningspliktige);
                                val context = MappingContextUtils.getMappingContext();
                                context.setProperty("opplysningspliktige", opplysningspliktige);
                                return Map.of(eregVirksomhet.getMiljoe(), mapperFacade.map(organisasjon, RsOrganisasjon.class, context));
                            } else {
                                return Map.of(eregVirksomhet.getMiljoe(), RsOrganisasjon.builder()
                                        .error(eregVirksomhet.getError())
                                        .build());
                            }
                        }));
    }

    private Organisasjon getObject(JsonNode organisasjon) {

        return switch (organisasjon.get("type").asText()) {
            case "Organisasjon" -> objectMapper.convertValue(organisasjon, Organisasjon.class);
            case "Virksomhet" -> objectMapper.convertValue(organisasjon, Virksomhet.class);
            case "Organisasjonsledd" -> objectMapper.convertValue(organisasjon, Organisasjonsledd.class);
            case "JuridiskEnhet" -> objectMapper.convertValue(organisasjon, JuridiskEnhet.class);
            default -> throw new RuntimeException("Ukjent type: " + organisasjon.get("type").asText());
        };
    }

    private Set<String> finnOpplysningspliktige(Organisasjon eregVirksomhet) {
        var result = new HashSet<String>();

        OrganisasjonBase current = eregVirksomhet;

        var traverse = true;
        while (traverse) {
            if (current instanceof InngaarIJuridiskEnhet inngaarIJuridiskEnhet) {
                result.add(inngaarIJuridiskEnhet.getOrganisasjonsnummer());

                traverse = false;
            }

            if (current instanceof Organisasjonsledd orgledd) {
                result.add(orgledd.getOrganisasjonsnummer());

                var overOrdnedeJuridiskeEnheter = orgledd.getInngaarIJuridiskEnheter();
                var overOrdnedeOrganisasjonsledd = orgledd.getOrganisasjonsleddOver();
                if (!overOrdnedeJuridiskeEnheter.isEmpty()) {
                    current = overOrdnedeJuridiskeEnheter.getFirst();
                } else {
                    current = overOrdnedeOrganisasjonsledd.getFirst().getOrganisasjonsledd();
                }
            }

            if (current instanceof Virksomhet virksomhet) {
                var overOrdnedeJuridiskeEnheter = virksomhet.getInngaarIJuridiskEnheter();
                var overOrdnedeOrganisasjonsledd = virksomhet.getBestaarAvOrganisasjonsledd();
                if (!overOrdnedeJuridiskeEnheter.isEmpty()) {
                    current = overOrdnedeJuridiskeEnheter.getFirst();
                } else {
                    if (overOrdnedeOrganisasjonsledd == null || overOrdnedeOrganisasjonsledd.isEmpty()) {
                        log.info("Fant ikke organisasjonsledd for virksomhet: {}", virksomhet.getOrganisasjonsnummer());
                        traverse = false;
                    } else {
                        current = overOrdnedeOrganisasjonsledd.getFirst().getOrganisasjonsledd();
                    }
                }
            }
        }

        return result;
    }
}
