package no.nav.dolly.bestilling.organisasjonforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployResponse.EnvStatus;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.jpa.OrganisasjonNummer;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.service.OrganisasjonBestillingService;
import no.nav.dolly.service.OrganisasjonNummerService;
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
    private final OrganisasjonNummerService organisasjonNummerService;
    private final OrganisasjonProgressService organisasjonProgressService;
    private final OrganisasjonBestillingService organisasjonBestillingService;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;

    @Async
    @Transactional
    public void opprett(RsOrganisasjonBestilling bestilling, Long bestillingId) {

        BestillingRequest bestillingRequest = BestillingRequest.builder()
                .organisasjoner(List.of(mapperFacade.map(bestilling.getOrganisasjon(), BestillingRequest.SyntetiskOrganisasjon.class)))
                .build();

        Set<String> orgnumre = new HashSet<>();

        organisasjonProgressService.save(OrganisasjonBestillingProgress.builder()
                .bestillingId(bestillingId)
                .organisasjonsnummer("NA")
                .uuid("NA")
                .organisasjonsforvalterStatus(bestilling.getEnvironments().stream().map(env -> env + ":Pågående").collect(Collectors.joining(",")))
                .build());

        bestillingRequest.getOrganisasjoner().forEach(organisasjon -> {

            try {
                log.info("Bestiller orgnumre fra Organisasjon Forvalter");
                ResponseEntity<BestillingResponse> response = organisasjonConsumer.postOrganisasjon(bestillingRequest);

                orgnumre.addAll(requireNonNull(response.getBody()).getOrgnummer());
                if (!organisasjonProgressService.fetchOrganisasjonBestillingProgressByBestillingsId(bestillingId).isEmpty()) {
                    List<OrganisasjonBestillingProgress> organisasjonBestillingProgresses = organisasjonProgressService.fetchOrganisasjonBestillingProgressByBestillingsId(bestillingId);
                    OrganisasjonBestillingProgress organisasjonBestillingProgress = organisasjonBestillingProgresses.get(0);
                    organisasjonBestillingProgress.setBestillingId(bestillingId);
                    organisasjonBestillingProgress.setOrganisasjonsnummer(requireNonNull(response.getBody().getOrgnummer().iterator().next()));
                    organisasjonBestillingProgress.setOrganisasjonsforvalterStatus(bestilling.getEnvironments().stream().map(env -> env + ":Deployer").collect(Collectors.joining(",")));

                    organisasjonProgressService.save(organisasjonBestillingProgress);
                    saveOrgnumreToDb(orgnumre, bestillingId, bestilling.getEnvironments());
                    deployOrganisasjon(orgnumre, bestillingId, bestilling.getEnvironments());
                }
            } catch (RuntimeException e) {

                log.error("Feilet med å opprette organisasjon(er)", e);
                organisasjonBestillingService.setBestillingFeil(bestillingId, errorStatusDecoder.decodeRuntimeException(e));
                organisasjonProgressService.setBestillingFeil(bestillingId, errorStatusDecoder.decodeRuntimeException(e));
            }
        });
    }

    @Async
    public void gjenopprett(DeployRequest request, Long bestillingId) {

        organisasjonProgressService.save(OrganisasjonBestillingProgress.builder()
                .bestillingId(bestillingId)
                .organisasjonsnummer(request.getOrgnumre().iterator().next())
                .uuid("NA")
                .organisasjonsforvalterStatus(request.getEnvironments().stream().map(env -> env + ":Deployer").collect(Collectors.joining(",")))
                .build());

        organisasjonNummerService.save(OrganisasjonNummer.builder()
                .bestillingId(bestillingId)
                .organisasjonsnr(request.getOrgnumre().iterator().next())
                .build());

        deployOrganisasjon(request.getOrgnumre(), bestillingId, request.getEnvironments());
    }

    private void saveOrgnumreToDb(Set<String> orgnumre, Long bestillingId, List<String> environments) {

        log.info("Deployer orgnumre fra Organisasjon Forvalter");
        if (isNull(orgnumre) || orgnumre.isEmpty() || isNull(environments) || environments.isEmpty()) {
            organisasjonBestillingService.setBestillingFeil(bestillingId, FEIL_UGYLDIGE_ORGNUMRE);
            throw new DollyFunctionalException("Ugyldig deployment, liste med miljø eller orgnumre eksisterer ikke");
        }
        orgnumre.forEach(orgnummer -> organisasjonNummerService.save(OrganisasjonNummer.builder()
                .bestillingId(bestillingId)
                .organisasjonsnr(orgnummer)
                .build()));
    }

    private void deployOrganisasjon(Set<String> orgnumre, Long bestillingId, List<String> environments) {
        ResponseEntity<DeployResponse> deployResponse = organisasjonConsumer.deployOrganisasjon(new DeployRequest(orgnumre, environments));

        if (deployResponse.hasBody()) {
            requireNonNull(deployResponse.getBody()).getOrgStatus().entrySet().forEach(orgStatus -> {

                OrganisasjonBestillingProgress organisasjonBestillingProgress = new OrganisasjonBestillingProgress();

                if (!organisasjonProgressService.fetchOrganisasjonBestillingProgressByBestillingsId(bestillingId).isEmpty()) {
                    List<OrganisasjonBestillingProgress> organisasjonBestillingProgresses = organisasjonProgressService.fetchOrganisasjonBestillingProgressByBestillingsId(bestillingId);
                    organisasjonBestillingProgress = organisasjonBestillingProgresses.get(0);
                }

                if (deployResponse.getBody().getOrgStatus().get(organisasjonBestillingProgress.getOrganisasjonsnummer()).get(0).getStatus().name().contains("ERROR")) {
                    organisasjonBestillingService.setBestillingFeil(
                            bestillingId,
                            deployResponse.getBody().getOrgStatus().get(organisasjonBestillingProgress.getOrganisasjonsnummer()).get(0).getDetails());
                }

                organisasjonBestillingProgress.setBestillingId(bestillingId);
                organisasjonBestillingProgress.setOrganisasjonsnummer(orgStatus.getKey());
                organisasjonBestillingProgress.setOrganisasjonsforvalterStatus(mapStatusFraDeploy(orgStatus));

                organisasjonProgressService.save(organisasjonBestillingProgress);
            });
        } else {
            organisasjonBestillingService.setBestillingFeil(bestillingId, FEIL_STATUS_ORGFORVALTER_DEPLOY);
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
