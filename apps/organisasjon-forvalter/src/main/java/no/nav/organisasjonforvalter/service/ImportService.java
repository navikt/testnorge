package no.nav.organisasjonforvalter.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.organisasjonforvalter.consumer.MiljoerServiceConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonServiceConsumer;
import no.nav.organisasjonforvalter.dto.responses.RsOrganisasjon;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.jpa.repository.OrganisasjonRepository;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final MiljoerServiceConsumer miljoerServiceConsumer;
    private final OrganisasjonServiceConsumer organisasjonServiceConsumer;
    private final MapperFacade mapperFacade;
    private final OrganisasjonRepository organisasjonRepository;

    private static Set<String> getAllOrgnr(Organisasjon organisasjon, Set<String> orgnr) {

        orgnr.add(organisasjon.getOrganisasjonsnummer());
        organisasjon.getUnderenheter().forEach(org -> getAllOrgnr(org, orgnr));
        return orgnr;
    }

    @Transactional
    public Map<String, RsOrganisasjon> getOrganisasjoner(String orgnummer, Set<String> miljoer) {

        var miljoerAaSjekke = nonNull(miljoer) &&
                miljoer.stream().anyMatch(miljoe ->
                        miljoerServiceConsumer.getOrgMiljoer().stream().anyMatch(miljoe2 -> miljoe2.equals(miljoe))) ?

                miljoer.stream().filter(miljoe ->
                        miljoerServiceConsumer.getOrgMiljoer().stream().anyMatch(miljoe2 -> miljoe2.equals(miljoe)))
                        .collect(Collectors.toSet()) :

                miljoerServiceConsumer.getOrgMiljoer();

        var dbOrganisasjon = organisasjonRepository.findByOrganisasjonsnummer(orgnummer);

        var organisasjoner =
                organisasjonServiceConsumer.getStatus(dbOrganisasjon.isPresent() ?
                        getAllOrgnr(dbOrganisasjon.get(), new HashSet<>()) : Set.of(orgnummer), miljoerAaSjekke);

        return organisasjoner.keySet().stream()
                .filter(env -> !organisasjoner.get(env).entrySet().isEmpty())
                .map(env -> OrganisasjonMiljoe.builder()
                        .organisasjon(getOrganisasjon(env, orgnummer, organisasjoner))
                        .miljoe(env)
                        .build())
                .filter(org -> nonNull(org.getOrganisasjon()))
                .collect(Collectors.toMap(OrganisasjonMiljoe::getMiljoe, OrganisasjonMiljoe::getOrganisasjon));
    }

    private RsOrganisasjon getOrganisasjon(String env, String orgnummer,
                                           Map<String, Map<String, Optional<OrganisasjonDTO>>> organisasjoner) {

        OrganisasjonDTO organisasjonDto;
        if (organisasjoner.get(env).containsKey(orgnummer)) {
            if (organisasjoner.get(env).get(orgnummer).isPresent()) {
                organisasjonDto = organisasjoner.get(env).get(orgnummer).get();
            } else {
                return null;
            }
        } else {
            var organisasjon = organisasjonServiceConsumer.getStatus(orgnummer, env);
            if (organisasjon.isPresent()) {
                organisasjonDto = organisasjon.get();
            } else {
                return null;
            }
        }

        RsOrganisasjon organisasjon = mapperFacade.map(organisasjonDto, RsOrganisasjon.class);
        if (nonNull(organisasjonDto.getDriverVirksomheter())) {
            organisasjon.setUnderenheter(organisasjonDto.getDriverVirksomheter().stream()
                    .map(orgnr -> getOrganisasjon(env, orgnr, organisasjoner))
                    .collect(Collectors.toList()));
        }
        return organisasjon;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganisasjonMiljoe {

        private RsOrganisasjon organisasjon;
        private String miljoe;
    }
}
