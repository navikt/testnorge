package no.nav.dolly.bestilling.skattekort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.testnav.libs.dto.skattekortservice.v1.SokosSkattekortRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkattekortClient implements ClientRegister {

    private final SkattekortConsumer skattekortConsumer;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                                                BestillingProgress progress, boolean isOpprettEndre) {

        log.info("[SKATTEKORT_CLIENT] SkattekortClient.gjenopprett called for ident: {}, hasSkattekort: {}", 
                dollyPerson.getIdent(), nonNull(bestilling.getSkattekort()));

        if (!nonNull(bestilling.getSkattekort())) {
            return Mono.just(progress);
        }

        MappingContext context = MappingContextUtils.getMappingContext();
        context.setProperty("ident", dollyPerson.getIdent());

        SokosSkattekortRequest request = mapperFacade.map(bestilling.getSkattekort(), SokosSkattekortRequest.class, context);

        String orgNumber = bestilling.getSkattekort().getArbeidsgiverSkatt().isEmpty() ? "unknown" :
                bestilling.getSkattekort().getArbeidsgiverSkatt().get(0).getArbeidsgiveridentifikator().getOrganisasjonsnummer();
        Integer year = request.getSkattekort() != null ? request.getSkattekort().getInntektsaar() : null;

        return skattekortConsumer.sendSkattekort(request)
                .map(response -> {
                    String status = formatStatus(response, orgNumber, year);
                    log.info("[SKATTEKORT_CLIENT] Skattekort response received: status={}, formatted={}", response.getStatus(), status);
                    return status;
                })
                .flatMap(status -> {
                    log.info("[SKATTEKORT_CLIENT] Persisting skattekort status: {}", status);
                    return oppdaterStatus(progress, status);
                })
                .onErrorResume(throwable -> {
                    Integer yr = request.getSkattekort() != null ? request.getSkattekort().getInntektsaar() : null;
                    String status = orgNumber + "+" + yr + "|Feil: " + throwable.getMessage();
                    log.error("[SKATTEKORT_CLIENT] Error processing skattekort for org: {}, year: {}, error: {}", orgNumber, yr, throwable.getMessage(), throwable);
                    return oppdaterStatus(progress, status);
                });
    }

    @Override
    public void release(List<String> identer) {
    }

    private String formatStatus(SkattekortResponse response, String orgNumber, Integer year) {
        String prefix = orgNumber + "+" + year + "|";
        if (response.isOK()) {
            return prefix + "Skattekort lagret";
        }
        return prefix + response.getFeilmelding();
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {
        return transactionHelperService.persister(progress, BestillingProgress::setSkattekortStatus, status)
                .defaultIfEmpty(progress);
    }
}
