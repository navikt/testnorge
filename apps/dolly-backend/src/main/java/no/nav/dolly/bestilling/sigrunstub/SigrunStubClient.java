package no.nav.dolly.bestilling.sigrunstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Log4j2
@Service
@RequiredArgsConstructor
public class SigrunStubClient implements ClientRegister {

    private final SigrunStubConsumer sigrunStubConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Flux<Void> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!bestilling.getSigrunstub().isEmpty()) {

            var context = new MappingContext.Factory().getContext();
            context.setProperty("ident", dollyPerson.getHovedperson());

            var skattegrunnlag =
                    mapperFacade.mapAsList(bestilling.getSigrunstub(), OpprettSkattegrunnlag.class, context);

            deleteSkattegrunnlag(dollyPerson.getHovedperson(), isOpprettEndre)
                    .flatMap(deletedStatus -> sigrunStubConsumer.createSkattegrunnlag(skattegrunnlag))
                    .map(this::getStatus)
                    .subscribe(resultat -> {
                        progress.setSigrunstubStatus(resultat);
                        transactionHelperService.persister(progress);
                    });
        }
        return Flux.just();
    }

    @Override
    public void release(List<String> identer) {

        sigrunStubConsumer.deleteSkattegrunnlag(identer)
                .collectList()
                .subscribe(response -> log.info("Slettet antall {} identer fra Sigrunstub", response.size()));
    }

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return isNull(kriterier.getSigrunstub()) ||
                bestilling.getProgresser().stream()
                        .allMatch(entry -> isNotBlank(entry.getSigrunstubStatus()));
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
