package no.nav.dolly.bestilling.kontoregisterservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.kontoregister.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.kontoregister.v1.OppdaterKontoRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class KontoregisterClient implements ClientRegister {

    private final KontoregisterConsumer kontoregisterConsumer;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Mono.just(bestilling)
                .filter(ordre -> nonNull(bestilling.getBankkonto()))
                .flatMap(ordre -> {
                    if (isOpprettEndre) {
                        return Mono.just(true);
                    } else {
                        return kontoregisterConsumer.getKontonummer(dollyPerson.getIdent())
                                .map(status -> !HttpStatus.OK.equals(status.getStatus()));
                    }
                })
                .flatMap(opprettNytt -> {
                    if (isTrue(opprettNytt)) {
                        return prepareRequest(bestilling, dollyPerson.getIdent())
                                .flatMap(request -> kontoregisterConsumer.opprettKontonummer(request)
                                        .map(status -> status.getStatus().is2xxSuccessful() ? "OK" :
                                                errorStatusDecoder.getErrorText(status.getStatus(),
                                                        status.getFeilmelding())));
                    } else {
                        return Mono.just("OK");
                    }
                })
                .flatMap(status -> oppdaterStatus(progress, status));
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress, BestillingProgress::setKontoregisterStatus, status);
    }

    private Mono<OppdaterKontoRequestDTO> prepareRequest(RsDollyUtvidetBestilling bestilling, String ident) {

        var norskBankkonto = nonNull(bestilling.getBankkonto().getNorskBankkonto()) ?
                bestilling.getBankkonto().getNorskBankkonto() : getNorskBankkonto(bestilling);

        var utenlandskBankkonto = nonNull(bestilling.getBankkonto().getUtenlandskBankkonto()) ?
                bestilling.getBankkonto().getUtenlandskBankkonto() : getUtenlandskBankkonto(bestilling);

        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", ident);

        if (nonNull(norskBankkonto)) {
            return Mono.just(mapperFacade.map(norskBankkonto, OppdaterKontoRequestDTO.class, context));

        } else if (nonNull(utenlandskBankkonto)) {
            return Mono.just(mapperFacade.map(utenlandskBankkonto, OppdaterKontoRequestDTO.class, context));

        } else {
            return Mono.empty();
        }
    }

    private BankkontonrNorskDTO getNorskBankkonto(RsDollyUtvidetBestilling bestilling) {

        return nonNull(bestilling.getTpsMessaging()) ? bestilling.getTpsMessaging().getNorskBankkonto() : null;
    }

    private BankkontonrUtlandDTO getUtenlandskBankkonto(RsDollyUtvidetBestilling bestilling) {

        return nonNull(bestilling.getTpsMessaging()) ? bestilling.getTpsMessaging().getUtenlandskBankkonto() : null;
    }

    @Override
    public void release(List<String> identer) {

        kontoregisterConsumer.deleteKontonumre(identer)
                .collectList()
                .subscribe(response -> log.info("Slettet kontoer fra Kontoregister"));
    }
}
