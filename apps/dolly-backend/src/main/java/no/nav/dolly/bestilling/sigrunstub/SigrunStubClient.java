package no.nav.dolly.bestilling.sigrunstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.sigrunstub.dto.PensjonsgivendeForFolketrygden;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Log4j2
@Service
@RequiredArgsConstructor
public class SigrunStubClient implements ClientRegister {

    private static final String LIGNET_INNTEKT = "SIGRUN_LIGNET:";
    private static final String PENSJONSGIVENDE_INNTEKT = "SIGRUN_PENSJONSGIVENDE:";

    private final SigrunStubConsumer sigrunStubConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.from(Flux.merge(
                        Flux.just(bestilling)
                                .filter(bestilling1 -> !bestilling1.getSigrunstub().isEmpty())
                                .map(RsDollyUtvidetBestilling::getSigrunstub)
                                .flatMap(lignetInntekt -> {

                                    var context = MappingContextUtils.getMappingContext();
                                    context.setProperty("ident", dollyPerson.getIdent());

                                    var skattegrunnlag =
                                            mapperFacade.mapAsList(lignetInntekt, OpprettSkattegrunnlag.class, context);

                                    return Flux.from(deleteSkattegrunnlag(dollyPerson.getIdent(), isOpprettEndre)
                                                    .flatMap(deletedStatus -> sigrunStubConsumer.createSkattegrunnlag(skattegrunnlag))
                                                    .map(this::getStatus))
                                            .map(status -> LIGNET_INNTEKT + status);
                                }),
                        Flux.just(bestilling)
                                .filter(bestilling2 -> !bestilling2.getSigrunstubPensjonsgivende().isEmpty())
                                .map(RsDollyUtvidetBestilling::getSigrunstubPensjonsgivende)
                                .flatMap(pensjonsgivende -> {

                                    var context = MappingContextUtils.getMappingContext();
                                    context.setProperty("ident", dollyPerson.getIdent());

                                    var skattegrunnlag =
                                            mapperFacade.mapAsList(pensjonsgivende, PensjonsgivendeForFolketrygden.class, context);

                                    return sigrunStubConsumer.updatePensjonsgivendeInntekt(skattegrunnlag)
                                            .map(this::getStatus)
                                            .map(status -> PENSJONSGIVENDE_INNTEKT + status);
                                }))
                .collect(Collectors.joining(","))
                .map(resultat -> futurePersist(progress, resultat)));
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress, BestillingProgress::setSigrunstubStatus, status);
            return progress;
        };
    }

    @Override
    public void release(List<String> identer) {

        sigrunStubConsumer.deleteSkattegrunnlag(identer)
                .collectList()
                .subscribe(response -> log.info("Slettet antall {} identer fra Sigrunstub", response.size()));
    }

    private Mono<SigrunstubResponse> deleteSkattegrunnlag(String ident, boolean opprettEndre) {

        return opprettEndre ? sigrunStubConsumer.deleteSkattegrunnlag(ident) :
                Mono.just(new SigrunstubResponse());
    }

    private String getStatus(SigrunstubResponse response) {

        return isNull(response.getErrorStatus()) ?
                getStatus(response.getOpprettelseTilbakemeldingsListe()) :
                ErrorStatusDecoder.encodeStatus(errorStatusDecoder.getErrorText(response.getErrorStatus(), response.getMelding()));
    }

    private static String getStatus(List<SigrunstubResponse.OpprettelseTilbakemelding> tilbakemeldinger) {

        return tilbakemeldinger.stream()
                .allMatch(SigrunstubResponse.OpprettelseTilbakemelding::isOK) ? "OK" :
                String.format("Feil: %s", ErrorStatusDecoder.encodeStatus(
                        tilbakemeldinger.stream()
                                .filter(SigrunstubResponse.OpprettelseTilbakemelding::isError)
                                .map(status -> String.format("Inntektsår: ‰s, feilmelding: %s", status.getInntektsaar(), status.getMessage()))
                                .collect(Collectors.joining(", "))));
    }
}
