package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.organisasjonforvalter.consumer.OrganisasjonMottakConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonServiceConsumer;
import no.nav.organisasjonforvalter.dto.requests.DeployRequest;
import no.nav.organisasjonforvalter.dto.responses.DeployResponse;
import no.nav.organisasjonforvalter.dto.responses.DeployResponse.EnvStatus;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.jpa.repository.OrganisasjonRepository;
import no.nav.organisasjonforvalter.service.DeployStatusService.DeployEntry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static no.nav.organisasjonforvalter.dto.responses.DeployResponse.Status.ERROR;
import static no.nav.organisasjonforvalter.dto.responses.DeployResponse.Status.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeploymentService {

    private final OrganisasjonRepository organisasjonRepository;
    private final OrganisasjonMottakConsumer organisasjonMottakConsumer;
    private final DeployStatusService deployStatusService;
    private final OrganisasjonServiceConsumer organisasjonApiConsumer;
    private final MapperFacade mapperFacade;

    public DeployResponse deploy(DeployRequest request) {

        List<Organisasjon> organisasjoner = organisasjonRepository.findAllByOrganisasjonsnummerIn(request.getOrgnumre());
        if (organisasjoner.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, format("Ingen organisasjoner %s funnet!",
                    String.join(",", request.getOrgnumre())));
        }

        Map<String, List<EnvStatus>> status = organisasjoner.stream()
                .collect(Collectors.toMap(Organisasjon::getOrganisasjonsnummer,
                        organisasjon -> request.getEnvironments().stream().map(env -> {
                            String uuid = UUID.randomUUID().toString();
                            try {
                                deployOrganisasjon(uuid, organisasjon, env);
                                return EnvStatus.builder()
                                        .status(OK)
                                        .uuid(uuid)
                                        .environment(env)
                                        .build();
                            } catch (RuntimeException e) {
                                log.error(e.getMessage(), e);
                                return EnvStatus.builder()
                                        .status(ERROR)
                                        .uuid(uuid)
                                        .details(e.getMessage())
                                        .environment(env)
                                        .build();
                            }
                        }).collect(Collectors.toList())));

        return awaitSyncStatusCompletion(status);
    }


    private void deployOrganisasjon(String uuid, Organisasjon organisasjon, String env) {

        if (!organisasjon.getOrganisasjonsnummer().equals(
                organisasjonApiConsumer.getStatus(organisasjon.getOrganisasjonsnummer(), env).getOrgnummer())) {

            organisasjonMottakConsumer.opprettOrganisasjon(uuid, organisasjon, env);
        } else {
            organisasjonMottakConsumer.endreOrganisasjon(uuid, organisasjon, env);
        }
    }

    private DeployResponse awaitSyncStatusCompletion(Map<String, List<EnvStatus>> deployStatus) {

        return DeployResponse.builder()
                .orgStatus(deployStatus.keySet().parallelStream()
                        .collect(Collectors.toMap(
                                orgnr -> orgnr,
                                orgnr -> Stream.of(
                                        deployStatus.get(orgnr).stream()
                                                .filter(EnvStatus::isError)
                                                .collect(Collectors.toList()),
                                        deployStatusService.awaitDeployedDone(
                                                deployStatus.get(orgnr).stream()
                                                        .filter(EnvStatus::isOk)
                                                        .map(entry -> mapperFacade.map(entry, DeployEntry.class))
                                                        .collect(Collectors.toList()))
                                )
                                        .flatMap(Collection::stream)
                                        .collect(Collectors.toList()))))
                .build();
    }
}
