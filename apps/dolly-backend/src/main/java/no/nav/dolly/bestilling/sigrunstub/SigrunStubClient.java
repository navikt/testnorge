package no.nav.dolly.bestilling.sigrunstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubLignetInntektRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubPensjonsgivendeInntektRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubSummertskattegrunnlagRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubSummertskattegrunnlagRequest.Summertskattegrunnlag;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.util.TransactionHelperService;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Log4j2
@Service
@RequiredArgsConstructor
public class SigrunStubClient implements ClientRegister {

    private final SigrunStubConsumer sigrunStubConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.from(Flux.merge(
                        doLignetInntekt(bestilling, dollyPerson),
                        doPensjonsgivendeInntekt(bestilling, dollyPerson),
                        doSummertSkattegrunnlag(bestilling, dollyPerson))
                .collect(Collectors.joining(","))
                .map(resultat -> futurePersist(progress, resultat)));
    }

    private Publisher<String> doSummertSkattegrunnlag(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson) {
        return Flux.just(bestilling)
                .filter(bestilling1 -> !bestilling1.getSigrunstubSummertSkattegrunnlag().isEmpty())
                .map(RsDollyUtvidetBestilling::getSigrunstubSummertSkattegrunnlag)
                .flatMap(summertSkattegrunnlag -> {

                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("ident", dollyPerson.getIdent());

                    var skattegrunnlag = SigrunstubSummertskattegrunnlagRequest.builder()
                            .summertskattegrunnlag(
                                    mapperFacade.mapAsList(summertSkattegrunnlag, Summertskattegrunnlag.class, context)
                            ).build();

                    return sigrunStubConsumer.createSigrunstubSummertSkattegrunnlag(skattegrunnlag)
                            .map(this::getStatus)
                            .map(status -> "SIGRUN_SUMMERT:" + status);
                });
    }

    private Flux<String> doPensjonsgivendeInntekt(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson) {
        return Flux.just(bestilling)
                .filter(bestilling2 -> !bestilling2.getSigrunstubPensjonsgivende().isEmpty())
                .map(RsDollyUtvidetBestilling::getSigrunstubPensjonsgivende)
                .flatMap(pensjonsgivende -> {

                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("ident", dollyPerson.getIdent());

                    var skattegrunnlag =
                            mapperFacade.mapAsList(pensjonsgivende, SigrunstubPensjonsgivendeInntektRequest.class, context);

                    return sigrunStubConsumer.updatePensjonsgivendeInntekt(skattegrunnlag)
                            .map(this::getStatus)
                            .map(status -> "SIGRUN_PENSJONSGIVENDE:" + status);
                });
    }

    private Flux<String> doLignetInntekt(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson) {
        return Flux.just(bestilling)
                .filter(bestilling1 -> !bestilling1.getSigrunstub().isEmpty())
                .map(RsDollyUtvidetBestilling::getSigrunstub)
                .flatMap(lignetInntekt -> {

                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("ident", dollyPerson.getIdent());

                    var skattegrunnlag =
                            mapperFacade.mapAsList(lignetInntekt, SigrunstubLignetInntektRequest.class, context);

                    return sigrunStubConsumer.updateLignetInntekt(skattegrunnlag)
                            .map(this::getStatus)
                            .map(status -> "SIGRUN_LIGNET:" + status);
                });
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress, BestillingProgress::setSigrunstubStatus, status);
            return progress;
        };
    }

    @Override
    public void release(List<String> identer) {

        Flux.merge(sigrunStubConsumer.deleteLignetInntekt(identer)
                                .filter(SigrunstubResponse::isOK)
                                .count()
                                .map(antall -> "lignet inntekt: " + antall),
                        sigrunStubConsumer.deletePensjonsgivendeInntekt(identer)
                                .filter(SigrunstubResponse::isOK)
                                .count()
                                .map(antall -> "pensjonsgivende inntekt: " + antall),
                        sigrunStubConsumer.deleteSummertSkattegrunnlag(identer)
                                .filter(SigrunstubResponse::isOK)
                                .count()
                                .map(antall -> "summert skattegrunnlag: " + antall))
                .collect(Collectors.joining(", "))
                .subscribe(response -> log.info("Slettet antall {} fra Sigrunstub", response));
    }

    private String getStatus(SigrunstubResponse response) {

        log.info("Response fra Sigrunstub med data {} ", response);
        return isNull(response.getStatus()) ?
                getStatus(response.getOpprettelseTilbakemeldingsListe()) :
                ErrorStatusDecoder.encodeStatus(errorStatusDecoder.getErrorText(response.getStatus(), response.getMelding()));
    }

    private static String getStatus(List<SigrunstubResponse.OpprettelseTilbakemelding> tilbakemeldinger) {

        return tilbakemeldinger.stream()
                .allMatch(SigrunstubResponse.OpprettelseTilbakemelding::isOK) ? "OK" :
                ErrorStatusDecoder.encodeStatus(String.format("Feil: %s",
                        tilbakemeldinger.stream()
                                .filter(SigrunstubResponse.OpprettelseTilbakemelding::isError)
                                .map(status -> String.format("Inntektsår: %s, feilmelding: %s", status.getInntektsaar(), status.getMessage()))
                                .collect(Collectors.joining(", "))));
    }
}