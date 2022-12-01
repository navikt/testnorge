package no.nav.dolly.bestilling.kontoregisterservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.BankkontonrNorskDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.BankkontonrUtlandDTO;
import no.nav.testnav.libs.dto.kontoregisterservice.v1.OppdaterKontoRequestDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVarselSlutt;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getVenterTekst;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class KontoregisterClient implements ClientRegister {

    private static final String SYSTEM = "Kontoregister";
    private final KontoregisterConsumer kontoregisterConsumer;
    private final MapperFacade mapperFacade;
    private final PersonServiceConsumer personServiceConsumer;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Flux<Void> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getBankkonto())) {


            progress.setKontoregisterStatus(encodeStatus(getInfoVenter(SYSTEM)));
            transactionHelperService.oppdaterProgress(progress);

            var request = prepareRequest(bestilling, dollyPerson.getHovedperson());
            if (nonNull(request)) {
                personServiceConsumer.getPdlSyncReady(dollyPerson.getHovedperson())
                        .flatMap(isPresent -> {
                            if (isPresent) {
                                return kontoregisterConsumer.postKontonummerRegister(request);
                            } else {
                                return Mono.just(encodeStatus(getVarselSlutt(SYSTEM)));
                            }
                        })
                        .subscribe(respons -> {
                            progress.setKontoregisterStatus(respons);
                            transactionHelperService.oppdaterProgress(progress);
                        });
            }
        }
        return Flux.just();
    }

    private OppdaterKontoRequestDTO prepareRequest(RsDollyUtvidetBestilling bestilling, String ident) {

        var norskBankkonto = nonNull(bestilling.getBankkonto().getNorskBankkonto()) ?
                bestilling.getBankkonto().getNorskBankkonto() : getNorskBankkonto(bestilling);

        var utenlandskBankkonto = nonNull(bestilling.getBankkonto().getUtenlandskBankkonto()) ?
                bestilling.getBankkonto().getUtenlandskBankkonto() : getUtenlandskBankkonto(bestilling);

        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", ident);

        if (nonNull(norskBankkonto)) {
            return mapperFacade.map(norskBankkonto, OppdaterKontoRequestDTO.class, context);

        } else if (nonNull(utenlandskBankkonto)) {
            return mapperFacade.map(utenlandskBankkonto, OppdaterKontoRequestDTO.class, context);

        } else {
            return null;
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
                .subscribe(response -> log.info("Slettet kontoer fra Kontoregister"));
    }

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return isNull(kriterier.getBankkonto()) ||
                bestilling.getProgresser().stream()
                        .allMatch(entry -> isNotBlank(entry.getKontoregisterStatus()) &&
                                !entry.getKontoregisterStatus().contains(getVenterTekst()));
    }
}
