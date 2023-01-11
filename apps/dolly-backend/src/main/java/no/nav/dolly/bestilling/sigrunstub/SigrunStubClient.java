package no.nav.dolly.bestilling.sigrunstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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

        if (!bestilling.getSigrunstub().isEmpty()) {

            var context = MappingContextUtils.getMappingContext();
            context.setProperty("ident", dollyPerson.getHovedperson());

            var skattegrunnlag =
                    mapperFacade.mapAsList(bestilling.getSigrunstub(), OpprettSkattegrunnlag.class, context);

            return Flux.from(deleteSkattegrunnlag(dollyPerson.getHovedperson(), isOpprettEndre)
                    .flatMap(deletedStatus -> sigrunStubConsumer.createSkattegrunnlag(skattegrunnlag))
                    .map(this::getStatus)
                    .map(resultat -> futurePersist(progress, resultat)));
        }
        return Flux.empty();
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            progress.setSigrunstubStatus(status);
            transactionHelperService.persister(progress);
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

        return response.getStatus().is2xxSuccessful() ? "OK" :
                errorStatusDecoder.getErrorText(response.getStatus(), response.getMelding());
    }
}
