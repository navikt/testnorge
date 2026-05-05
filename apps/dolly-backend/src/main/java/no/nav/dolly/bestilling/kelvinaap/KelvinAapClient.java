package no.nav.dolly.bestilling.kelvinaap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.kelvinaap.domain.AapOpprettRequest;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.service.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.substring;

@Slf4j
@Service
@RequiredArgsConstructor
public class KelvinAapClient implements ClientRegister {

    private final KelvinAapConsumer kelvinAapConsumer;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Mono.just(bestilling)
                .filter(bestilling1 -> nonNull(bestilling1.getKelvinAap()))
                .map(RsDollyBestilling::getKelvinAap)
                .flatMap(kelvinAap -> kelvinAapConsumer.readAap(dollyPerson.getIdent())
                        .zipWith(Mono.just(kelvinAap)))
                .flatMap(kelvinAap -> {
                    log.info("Hentet AAP for ident {} {}", dollyPerson.getIdent(), kelvinAap.getT1());
                    if (nonNull(kelvinAap.getT1().getStatus())) {
                        val errStatus = "FEIL ved henting av AAP for ident: %s, %s %s".formatted(
                                dollyPerson.getIdent(), kelvinAap.getT1().getStatus(), kelvinAap.getT1().getError());
                        log.error(errStatus);
                        return Mono.just(errStatus);
                    } else if (nonNull(kelvinAap.getT1().getSoeknad())) {
                        log.info("AAP behandling for ident {} finnes allerede: {}", dollyPerson.getIdent(), kelvinAap.getT1());
                        return Mono.just("OK");
                    } else {
                        val context = MappingContextUtils.getMappingContext();
                        context.setProperty("ident", dollyPerson.getIdent());
                        var aapOpprettRequest = mapperFacade.map(kelvinAap.getT2(), AapOpprettRequest.class, context);
                        return kelvinAapConsumer.createAap(aapOpprettRequest)
                                .map(response -> {
                                    if (isNotBlank(response.getSaksnummer())) {
                                        log.info("AAP opprettet for ident {}: saksnummer {}", dollyPerson.getIdent(), response);
                                        return "OK";
                                    } else {
                                        log.error("Feil ved oppretting av AAP for ident {}: {}", dollyPerson.getIdent(), response);
                                        return "FEIL: %s %s".formatted(response.getStatus(), response.getError());
                                    }
                                });
                    }
                })
                .flatMap(status -> oppdaterStatus(progress, status));
    }

    @Override
    public void release(List<String> identer) {

        // Kelvin AAP har ingen egen release-funksjonalitet, så denne metoden er implementert som en no-op.
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress,
                BestillingProgress::getKelvinAapStatus,
                BestillingProgress::setKelvinAapStatus, substring(status, 0, 255));
    }
}