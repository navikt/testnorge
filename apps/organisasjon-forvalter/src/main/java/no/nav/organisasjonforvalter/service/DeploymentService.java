package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.organisasjonforvalter.consumer.OrganisasjonMottakConsumer;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.jpa.repository.OrganisasjonRepository;
import no.nav.organisasjonforvalter.provider.rs.requests.DeployRequest;
import no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse;
import no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse.EnvStatus;
import no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse.Status;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Adresse;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Epost;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Formaal;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Internettadresse;
import org.springframework.stereotype.Service;
import zipkin2.Call;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse.Status.ERROR;
import static no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse.Status.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeploymentService {

    private final OrganisasjonRepository organisasjonRepository;
    private final OrganisasjonMottakConsumer organisasjonMottakConsumer;
    private final DeployStatusService deployStatusService;

    public DeployResponse deploy(DeployRequest request) {

        List<Organisasjon> organisasjoner = organisasjonRepository.findAllByOrganisasjonsnummerIn(request.getOrgnumre());

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
                        .collect(Collectors.toMap(orgnr -> orgnr, orgnr -> syncStatus(status.get(orgnr)))))
                .build();
    }


    private void deployOrganisasjon(String uuid, Organisasjon organisasjon, String env) {

        organisasjonMottakConsumer.opprettOrganisasjon(uuid, organisasjon, env);

        if (!organisasjon.getUnderenheter().isEmpty()) {
            organisasjon.getUnderenheter().forEach(org -> deployOrganisasjon(uuid, org, env));
        }
    }

    private List<EnvStatus> syncStatus(List<EnvStatus> envStatuses) {
        return envStatuses.parallelStream().map(envStatus ->
                EnvStatus.builder()
                        .uuid(envStatus.getUuid())
                        .environment(envStatus.getEnvironment())
                        .status(getStatus(envStatus.getUuid(), envStatus.getStatus()))
                        .details(envStatus.getDetails())
                        .build())
                .collect(Collectors.toList());
    }

    private Status getStatus(String uuid, Status status) {
        return status == OK ? deployStatusService.checkStatus(uuid) : status;
    }
}
