package no.nav.organisasjonforvalter.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import no.nav.organisasjonforvalter.consumer.MiljoerServiceConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonConsumer;
import no.nav.organisasjonforvalter.provider.rs.responses.RsOrganisasjon;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final MiljoerServiceConsumer miljoerServiceConsumer;
    private final OrganisasjonConsumer organisasjonApiConsumer;
    private final MapperFacade mapperFacade;

    public Map<String, RsOrganisasjon> getOrganisasjoner(String orgnummer, Set<String> miljoer) {

        Set<String> miljoerAaSjekke = nonNull(miljoer) &&
                miljoer.stream().anyMatch(miljoe ->
                                miljoerServiceConsumer.getOrgMiljoer().stream().anyMatch(miljoe2 -> miljoe2.equals(miljoe))) ?

                miljoer.stream().filter(miljoe ->
                        miljoerServiceConsumer.getOrgMiljoer().stream().anyMatch(miljoe2 -> miljoe2.equals(miljoe)))
                        .collect(Collectors.toSet()) :

                miljoerServiceConsumer.getOrgMiljoer();

        return miljoerAaSjekke.parallelStream()
                .filter(StringUtils::isNotBlank)
                .map(env -> OrganisasjonMiljoe.builder()
                        .organisasjon(acquireOrganisasjon(orgnummer, env))
                        .miljoe(env)
                        .build())
                .filter(org -> nonNull(org.getOrganisasjon()))
                .collect(Collectors.toMap(OrganisasjonMiljoe::getMiljoe, OrganisasjonMiljoe::getOrganisasjon));
    }

    private RsOrganisasjon acquireOrganisasjon(String orgnummer, String environment) {

        OrganisasjonDTO organisasjonDto = organisasjonApiConsumer.getStatus(orgnummer, environment);
        if (isBlank(organisasjonDto.getOrgnummer())) {
            return null;
        }
        RsOrganisasjon organisasjon = mapperFacade.map(organisasjonDto, RsOrganisasjon.class);
        organisasjon.setUnderenheter(organisasjonDto.getDriverVirksomheter().parallelStream()
                .map(orgnr -> acquireOrganisasjon(orgnr, environment))
                .collect(Collectors.toList()));
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
