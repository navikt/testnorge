package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.consumer.OrganisasjonMottakConsumer;
import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import no.nav.organisasjonforvalter.jpa.repository.OrganisasjonRepository;
import no.nav.organisasjonforvalter.provider.rs.requests.DeployRequest;
import no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public DeployResponse deploy(DeployRequest request) {

        List<Organisasjon> organisasjoner = organisasjonRepository.findAllByOrganisasjonsnummerIn(request.getOrgnumre());

        return DeployResponse.builder()
                .orgStatus(organisasjoner.stream()
                        .collect(Collectors.toMap(Organisasjon::getOrganisasjonsnummer,
                                organisasjon -> request.getEnvironments().stream().map(env -> {
                                    try {
                                        deployOrganisasjon(UUID.randomUUID().toString(), organisasjon, env);
                                        return DeployResponse.EnvStatus.builder()
                                                .status(OK)
                                                .environment(env)
                                                .build();
                                    } catch (RuntimeException e) {
                                        log.error(e.getMessage(), e);
                                        return DeployResponse.EnvStatus.builder()
                                                .status(ERROR)
                                                .details(e.getMessage())
                                                .environment(env)
                                                .build();
                                    }
                                }).collect(Collectors.toList())))
                ).build();
    }

    private void deployOrganisasjon(String uuid, Organisasjon organisasjon, String env) {

        organisasjonMottakConsumer.sendOrgnavn(uuid, organisasjon, env);
        organisasjonMottakConsumer.sendNaeringskode(uuid, organisasjon, env);
        organisasjonMottakConsumer.sendForretningsadresse(uuid, organisasjon, env);
        organisasjonMottakConsumer.sendPostadresse(uuid, organisasjon, env);
        organisasjonMottakConsumer.sendInternetadresse(uuid, organisasjon, env);
        organisasjonMottakConsumer.sendEpost(uuid, organisasjon, env);
        organisasjonMottakConsumer.sendParent(uuid, organisasjon, env);

        if (!organisasjon.getUnderenheter().isEmpty()) {
            organisasjon.getUnderenheter().forEach(org -> deployOrganisasjon(uuid, org, env));
        }
    }
}
