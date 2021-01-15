package no.nav.dolly.bestilling.organisasjonforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.OrganisasjonRegister;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisasjonClient implements OrganisasjonRegister {

    private static final String FEIL_UGYLDIGE_ORGNUMRE = "Feil= Ugyldig deployment, liste med miljø eller orgnumre eksisterer ikke";
    public static final String FEIL_STATUS_ORGFORVALTER_DEPLOY = "FEIL= Mottok ikke status fra Org-Forvalter deploy";

    private final OrganisasjonConsumer organisasjonConsumer;
    private final OrganisasjonNummerService organisasjonNummerService;
    private final OrganisasjonProgressService organisasjonProgressService;
    private final OrganisasjonBestillingService organisasjonBestillingService;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;

    @Override
    public void opprett(RsOrganisasjonBestilling bestilling, Long bestillingId) {

        BestillingRequest bestillingRequest = BestillingRequest.builder()
                .organisasjoner(List.of(mapperFacade.map(bestilling.getOrganisasjon(), BestillingRequest.SyntetiskOrganisasjon.class)))
                .build();

        Set<String> orgnumre = new HashSet<>();

        bestillingRequest.getOrganisasjoner().forEach(organisasjon -> {

            try {
                log.info("Bestiller orgnumre fra Organisasjon Forvalter");
                ResponseEntity<BestillingResponse> response = organisasjonConsumer.postOrganisasjon(bestillingRequest);
                if (response.hasBody()) {

                    orgnumre.addAll(requireNonNull(response.getBody()).getOrgnummer());
                }
            } catch (RuntimeException e) {

                log.error("Feilet med å opprette organisasjon(er)", e);

                organisasjonBestillingService.setBestillingFeil(bestillingId, errorStatusDecoder.decodeRuntimeException(e));
            }
        });
        saveOrgnumreToDbAndDeploy(orgnumre, bestillingId, bestilling.getEnvironments());

        organisasjonBestillingService.setBestillingFerdig(bestillingId);
    }

    @Override
    public DeployResponse gjenopprett(DeployRequest request) {

        ResponseEntity<DeployResponse> deployResponseResponseEntity = organisasjonConsumer.gjenopprettOrganisasjon(request);

        if (deployResponseResponseEntity.hasBody()) {
            return deployResponseResponseEntity.getBody();
        }

        throw new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, FEIL_STATUS_ORGFORVALTER_DEPLOY);
    }

    private void saveOrgnumreToDbAndDeploy(Set<String> orgnumre, Long bestillingId, List<String> environments) {

        log.info("Deployer orgnumre fra Organisasjon Forvalter");
        if (isNull(orgnumre) || orgnumre.isEmpty() || isNull(environments) || environments.isEmpty()) {
            organisasjonBestillingService.setBestillingFeil(bestillingId, FEIL_UGYLDIGE_ORGNUMRE);
            throw new DollyFunctionalException("Ugyldig deployment, liste med miljø eller orgnumre eksisterer ikke");
        }
        orgnumre.forEach(orgnummer -> organisasjonNummerService.save(OrganisasjonNummer.builder()
                .bestillingId(bestillingId)
                .organisasjonsnr(orgnummer)
                .build()));
        ResponseEntity<DeployResponse> deployResponse = organisasjonConsumer.deployOrganisasjon(new DeployRequest(orgnumre, environments));

        if (deployResponse.hasBody()) {
            requireNonNull(deployResponse.getBody()).getOrgStatus().entrySet().forEach(orgStatus -> organisasjonProgressService.save(OrganisasjonBestillingProgress.builder()
                    .bestillingId(bestillingId)
                    .organisasjonsnummer(orgStatus.getKey())
                    .organisasjonsforvalterStatus(mapStatusFraDeploy(orgStatus))
                    .uuid(mapUuidFraDeploy(orgStatus))
                    .build()));
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

    private String mapUuidFraDeploy(Entry<String, List<EnvStatus>> orgStatus) {

        if (isNull(orgStatus)) {
            return null;
        }
        StringBuilder status = new StringBuilder();
        orgStatus.getValue().forEach(envStatus -> {

            log.info("Deploy har status: {} for org: {} i miljoe: {} med UUID: {}",
                    envStatus.getStatus(),
                    orgStatus.getKey(),
                    envStatus.getEnvironment(),
                    envStatus.getUuid());

            status.append(isNotBlank(status) ? ',' : "");
            status.append(envStatus.getEnvironment());
            status.append(':');
            status.append(envStatus.getUuid());
        });
        return status.toString();
    }

    @Override
    public void release(List<String> orgnummer) {

        throw new UnsupportedOperationException("Release ikke implementert");
    }
}
