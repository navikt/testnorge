package no.nav.dolly.bestilling.skattekort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.skattekort.domain.ArbeidsgiverSkatt;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortResponse;
import no.nav.dolly.bestilling.skattekort.domain.Skattekortmelding;
import no.nav.dolly.bestilling.skattekort.domain.SokosSkattekortRequest;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkattekortClient implements ClientRegister {

    private static final String SYSTEM = "SKATTEKORT";

    private final SkattekortConsumer skattekortConsumer;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                                                BestillingProgress progress, boolean isOpprettEndre) {

        if (isNull(bestilling.getSkattekort()) || bestilling.getSkattekort().getArbeidsgiverSkatt().isEmpty()) {
            return Mono.empty();
        }

        return oppdaterStatus(progress, getInfoVenter(SYSTEM))
                .flatMap(updatedProgress -> Flux.fromIterable(bestilling.getSkattekort().getArbeidsgiverSkatt())
                        .flatMap(arbeidsgiver -> sendSkattekortForArbeidsgiver(arbeidsgiver, dollyPerson))
                        .collect(Collectors.joining(","))
                        .flatMap(resultat -> {
                            if (isNotBlank(resultat)) {
                                return oppdaterStatus(updatedProgress, resultat);
                            } else {
                                return Mono.just(updatedProgress);
                            }
                        }));
    }

    @Override
    public void release(List<String> identer) {
    }

    private Flux<String> sendSkattekortForArbeidsgiver(ArbeidsgiverSkatt arbeidsgiver, DollyPerson dollyPerson) {
        if (isNull(arbeidsgiver.getArbeidstaker()) || arbeidsgiver.getArbeidstaker().isEmpty()) {
            return Flux.empty();
        }

        String orgNumber = nonNull(arbeidsgiver.getArbeidsgiveridentifikator())
                ? arbeidsgiver.getArbeidsgiveridentifikator().getOrganisasjonsnummer()
                : "unknown";

        return Flux.fromIterable(arbeidsgiver.getArbeidstaker())
                .flatMap(arbeidstaker -> sendSkattekortForArbeidstaker(arbeidstaker, dollyPerson, orgNumber));
    }

    private Mono<String> sendSkattekortForArbeidstaker(Skattekortmelding arbeidstaker, DollyPerson dollyPerson, String orgNumber) {
        MappingContext context = MappingContextUtils.getMappingContext();
        context.setProperty("ident", dollyPerson.getIdent());

        SokosSkattekortRequest request = mapperFacade.map(arbeidstaker, SokosSkattekortRequest.class, context);
        Integer year = arbeidstaker.getInntektsaar();

        if (request.getSkattekort().getForskuddstrekkList().isEmpty()) {
            log.warn("Skipping skattekort for person: {}, org: {}, year: {} - forskuddstrekkList is empty",
                    dollyPerson.getIdent(), orgNumber, year);
            return Mono.just(orgNumber + "+" + year + "|Feil: Forskuddstrekk list er tom");
        }

        return skattekortConsumer.sendSkattekort(request)
                .map(response -> formatStatus(response, orgNumber, year))
                .onErrorResume(throwable -> {
                    log.error("Error sending skattekort for person: {}, org: {}, year: {}: {}",
                            dollyPerson.getIdent(), orgNumber, year, throwable.getMessage());
                    String status = orgNumber + "+" + year + "|Feil: " + throwable.getMessage();
                    return Mono.just(status);
                });
    }

    private String formatStatus(SkattekortResponse response, String orgNumber, Integer year) {
        String prefix = orgNumber + "+" + year + "|";
        if (response.isOK()) {
            return prefix + "Skattekort lagret";
        }
        return prefix + response.getFeilmelding();
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {
        return transactionHelperService.persister(progress, BestillingProgress::getSkattekortStatus,
                BestillingProgress::setSkattekortStatus, status);
    }
}
