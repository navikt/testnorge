package no.nav.dolly.bestilling.organisasjonforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployResponse.EnvStatus;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.service.OrganisasjonBestillingService;
import no.nav.dolly.service.OrganisasjonProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisasjonClient {

    private static final String FEIL_UGYLDIGE_ORGNUMRE = "FEIL= Ugyldig deployment, liste med miljø eller orgnumre eksisterer ikke";
    public static final String FEIL_STATUS_ORGFORVALTER_DEPLOY = "FEIL= Mottok ikke status fra Org-Forvalter deploy";

    private final OrganisasjonConsumer organisasjonConsumer;
    private final OrganisasjonProgressService organisasjonProgressService;
    private final OrganisasjonBestillingService organisasjonBestillingService;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;

    @Async
    @Transactional
    public void opprett(RsOrganisasjonBestilling rsBestilling, OrganisasjonBestilling bestilling) {

        BestillingRequest bestillingRequest = BestillingRequest.builder()
                .organisasjoner(List.of(mapperFacade.map(rsBestilling.getOrganisasjon(), BestillingRequest.SyntetiskOrganisasjon.class)))
                .build();

        Set<String> orgnumre = new HashSet<>();

        organisasjonProgressService.save(OrganisasjonBestillingProgress.builder()
                .bestilling(bestilling)
                .organisasjonsnummer("Ikke bestemt ...")
                .organisasjonsforvalterStatus(rsBestilling.getEnvironments().stream().map(env -> env + ":Pågående").collect(Collectors.joining(",")))
                .build());

        bestillingRequest.getOrganisasjoner().forEach(organisasjon -> {

            try {
                log.info("Bestiller orgnumre fra Organisasjon Forvalter");
                ResponseEntity<BestillingResponse> response = organisasjonConsumer.postOrganisasjon(bestillingRequest);

                orgnumre.addAll(requireNonNull(response.getBody()).getOrgnummer());
                if (!organisasjonProgressService.fetchOrganisasjonBestillingProgressByBestillingsId(bestilling.getId()).isEmpty()) {
                    List<OrganisasjonBestillingProgress> organisasjonBestillingProgresses = organisasjonProgressService.fetchOrganisasjonBestillingProgressByBestillingsId(bestilling.getId());
                    OrganisasjonBestillingProgress organisasjonBestillingProgress = organisasjonBestillingProgresses.get(0);
                    organisasjonBestillingProgress.setBestilling(bestilling);
                    organisasjonBestillingProgress.setOrganisasjonsnummer(requireNonNull(response.getBody().getOrgnummer().iterator().next()));
                    organisasjonBestillingProgress.setOrganisasjonsforvalterStatus(rsBestilling.getEnvironments().stream().map(env -> env + ":Deployer").collect(Collectors.joining(",")));

                    organisasjonProgressService.save(organisasjonBestillingProgress);
                    saveErrorToDb(orgnumre, bestilling.getId(), rsBestilling.getEnvironments());
                    deployOrganisasjon(orgnumre, bestilling, rsBestilling.getEnvironments());
                }
            } catch (RuntimeException e) {

                log.error("Feilet med å opprette organisasjon(er)", e);
                organisasjonBestillingService.setBestillingFeil(bestilling.getId(), errorStatusDecoder.decodeRuntimeException(e));
                organisasjonProgressService.setBestillingFeil(bestilling.getId(), errorStatusDecoder.decodeRuntimeException(e));
            }
        });
    }

    @Async
    public void gjenopprett(DeployRequest request, OrganisasjonBestilling bestilling) {

        organisasjonProgressService.save(OrganisasjonBestillingProgress.builder()
                .bestilling(bestilling)
                .organisasjonsnummer(request.getOrgnumre().iterator().next())
                .organisasjonsforvalterStatus(request.getEnvironments().stream().map(env -> env + ":Deployer").collect(Collectors.joining(",")))
                .build());

        deployOrganisasjon(request.getOrgnumre(), bestilling, request.getEnvironments());
    }

    private void saveErrorToDb(Set<String> orgnumre, Long bestillingId, List<String> environments) {

        log.info("Deployer orgnumre fra Organisasjon Forvalter");
        if (isNull(orgnumre) || orgnumre.isEmpty() || isNull(environments) || environments.isEmpty()) {
            organisasjonBestillingService.setBestillingFeil(bestillingId, FEIL_UGYLDIGE_ORGNUMRE);
            throw new DollyFunctionalException("Ugyldig deployment, liste med miljø eller orgnumre eksisterer ikke");
        }
    }

    private void deployOrganisasjon(Set<String> orgnumre, OrganisasjonBestilling bestilling, List<String> environments) {
        ResponseEntity<DeployResponse> deployResponse = organisasjonConsumer.deployOrganisasjon(new DeployRequest(orgnumre, environments));

        if (deployResponse.hasBody()) {
            requireNonNull(deployResponse.getBody()).getOrgStatus().entrySet().forEach(orgStatus -> {

                OrganisasjonBestillingProgress organisasjonBestillingProgress = new OrganisasjonBestillingProgress();

                if (!organisasjonProgressService.fetchOrganisasjonBestillingProgressByBestillingsId(bestilling.getId()).isEmpty()) {
                    List<OrganisasjonBestillingProgress> organisasjonBestillingProgresses = organisasjonProgressService.fetchOrganisasjonBestillingProgressByBestillingsId(bestilling.getId());
                    organisasjonBestillingProgress = organisasjonBestillingProgresses.get(0);
                }

                if (deployResponse.getBody().getOrgStatus().get(organisasjonBestillingProgress.getOrganisasjonsnummer()).get(0).getStatus().name().contains("ERROR")) {
                    organisasjonBestillingService.setBestillingFeil(
                            bestilling.getId(),
                            deployResponse.getBody().getOrgStatus().get(organisasjonBestillingProgress.getOrganisasjonsnummer()).get(0).getDetails());
                }

                organisasjonBestillingProgress.setBestilling(bestilling);
                organisasjonBestillingProgress.setOrganisasjonsnummer(orgStatus.getKey());
                organisasjonBestillingProgress.setOrganisasjonsforvalterStatus(mapStatusFraDeploy(orgStatus));

                organisasjonProgressService.save(organisasjonBestillingProgress);
            });
        } else {
            organisasjonBestillingService.setBestillingFeil(bestilling.getId(), FEIL_STATUS_ORGFORVALTER_DEPLOY);
            log.error(FEIL_STATUS_ORGFORVALTER_DEPLOY);
        }
    }


    private String mapStatusFraDeploy(Entry<String, List<EnvStatus>> orgStatus) {

        if (isNull(orgStatus)) {
            return null;
        }
        StringBuilder status = new StringBuilder();
        orgStatus.getValue().forEach(envStatus -> {

            log.info("Deploy har status: {} for org: {} i miljoe: {}",
                    envStatus.getStatus(),
                    orgStatus.getKey(),
                    envStatus.getEnvironment());

            status.append(isNotBlank(status) ? ',' : "");
            status.append(envStatus.getEnvironment());
            status.append(':');
            status.append(envStatus.getStatus());
            if (nonNull(envStatus.getDetails())) {
                status.append("-");
                status.append(envStatus.getDetails());
            }
        });
        return status.toString();
    }

    public void release(List<String> orgnummer) {

        throw new UnsupportedOperationException("Release ikke implementert");
    }
}
