package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.consumer.OrganisasjonApiConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonMottakConsumer;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.jpa.repository.OrganisasjonRepository;
import no.nav.organisasjonforvalter.provider.rs.requests.DeployRequest;
import no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse;
import no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse.EnvStatus;
import no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse.Status;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse.Status.ERROR;
import static no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse.Status.OK;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeploymentService {

    private static final long MAX_TIMEOUT_PER_ENV = 1000L * 60 * 3;
    private final OrganisasjonRepository organisasjonRepository;
    private final OrganisasjonMottakConsumer organisasjonMottakConsumer;
    private final DeployStatusService deployStatusService;
    private final OrganisasjonApiConsumer organisasjonApiConsumer;

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

        return DeployResponse.builder()
                .orgStatus(status.keySet().parallelStream()
                        .collect(Collectors.toMap(
                                orgnr -> orgnr,
                                orgnr -> syncStatus(status.get(orgnr), request.getEnvironments().size()))))
                .build();
    }


    private void deployOrganisasjon(String uuid, Organisasjon organisasjon, String env) {

        if (!organisasjon.getOrganisasjonsnummer().equals(
                organisasjonApiConsumer.getStatus(organisasjon.getOrganisasjonsnummer(), env).getOrgnummer())) {

            organisasjonMottakConsumer.opprettOrganisasjon(uuid, organisasjon, env);
        } else {
            organisasjonMottakConsumer.endreOrganisasjon(uuid, organisasjon, env);
        }

        if (!organisasjon.getUnderenheter().isEmpty()) {
            organisasjon.getUnderenheter().forEach(org -> deployOrganisasjon(uuid, org, env));
        }
    }

    private List<EnvStatus> syncStatus(List<EnvStatus> envStatuses, int envCount) {
        return envStatuses.parallelStream()
                .map(envStatus -> {
                    try {
                        Status deployStatus = getStatus(envStatus.getUuid(), envStatus.getStatus(), envCount);
                        return EnvStatus.builder()
                                .uuid(envStatus.getUuid())
                                .environment(envStatus.getEnvironment())
                                .status(deployStatus)
                                .details(deployStatus == ERROR && isBlank(envStatus.getDetails()) ?
                                        format("Timeout, oppretting ikke fullført etter %d ms",
                                                envCount * MAX_TIMEOUT_PER_ENV)
                                        : envStatus.getDetails())
                                .build();
                    } catch (RuntimeException e) {
                        return EnvStatus.builder()
                                .uuid(envStatus.getUuid())
                                .environment(envStatus.getEnvironment())
                                .status(ERROR)
                                .details(e.getMessage())
                                .build();
                    }
                })
                .collect(Collectors.toList());
    }

    private Status getStatus(String uuid, Status status, int envCount) {
        return status == OK ? deployStatusService.checkStatus(uuid, envCount * MAX_TIMEOUT_PER_ENV) : status;
    }
}
