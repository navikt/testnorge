package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.consumer.MiljoerServiceConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonMottakConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonServiceConsumer;
import no.nav.organisasjonforvalter.dto.requests.DeployRequest;
import no.nav.organisasjonforvalter.dto.responses.DeployResponse;
import no.nav.organisasjonforvalter.dto.responses.DeployResponse.EnvStatus;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.jpa.entity.Status;
import no.nav.organisasjonforvalter.jpa.repository.OrganisasjonRepository;
import no.nav.organisasjonforvalter.jpa.repository.StatusRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static no.nav.organisasjonforvalter.dto.responses.DeployResponse.Status.ERROR;
import static no.nav.organisasjonforvalter.dto.responses.DeployResponse.Status.OK;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrdreService {

    private final OrganisasjonRepository organisasjonRepository;
    private final OrganisasjonMottakConsumer organisasjonMottakConsumer;
    private final OrganisasjonServiceConsumer organisasjonServiceConsumer;
    private final StatusRepository statusRepository;
    private final MiljoerServiceConsumer miljoerServiceConsumer;

    public DeployResponse deploy(DeployRequest request) {

        var tilgjengeligeMiljoer = miljoerServiceConsumer.getOrgMiljoer();
        request.getEnvironments().forEach(miljoe -> {
            if (tilgjengeligeMiljoer.stream().noneMatch(tilgjengelig -> tilgjengelig.equals(miljoe))) {
                throw new HttpClientErrorException(BAD_REQUEST, format("Miljoe %s eksisterer ikke", miljoe));
            }
        });

        List<Organisasjon> organisasjoner = organisasjonRepository.findAllByOrganisasjonsnummerIn(request.getOrgnumre());
        if (organisasjoner.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, format("Ingen organisasjoner %s funnet!",
                    String.join(",", request.getOrgnumre())));
        }

        return DeployResponse.builder()
                .orgStatus(organisasjoner.stream()
                        .collect(Collectors.toMap(Organisasjon::getOrganisasjonsnummer,
                                organisasjon -> request.getEnvironments().stream().map(env -> {
                                            String uuid = UUID.randomUUID().toString();
                                            try {
                                                deployOrganisasjon(uuid, organisasjon, env);
                                                statusRepository.save(Status.builder()
                                                        .uuid(uuid)
                                                        .organisasjonsnummer(organisasjon.getOrganisasjonsnummer())
                                                        .miljoe(env)
                                                        .build());
                                                return EnvStatus.builder()
                                                        .status(OK)
                                                        .environment(env)
                                                        .build();
                                            } catch (RuntimeException e) {
                                                log.error(e.getMessage(), e);
                                                return EnvStatus.builder()
                                                        .status(ERROR)
                                                        .details(e.getMessage())
                                                        .environment(env)
                                                        .build();
                                            }
                                        })
                                        .toList())))
                .build();
    }

    private void deployOrganisasjon(String uuid, Organisasjon organisasjon, String env) {

        var orgStatus = organisasjonServiceConsumer.getStatus(organisasjon.getOrganisasjonsnummer(), env)
                        .collectList()
                        .block();

        if (isNull(orgStatus) || orgStatus.stream()
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .allMatch(org -> isBlank(org.getValue().getOrgnummer()))) {

            organisasjonMottakConsumer.opprettOrganisasjon(uuid, organisasjon, env);

        } else {

            organisasjonMottakConsumer.endreOrganisasjon(uuid, organisasjon, env);
        }
    }
}
