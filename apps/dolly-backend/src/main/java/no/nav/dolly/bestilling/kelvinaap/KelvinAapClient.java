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
                .map(kelvinAap -> {
                    val context = MappingContextUtils.getMappingContext();
                    context.setProperty("ident", dollyPerson.getIdent());
                    return mapperFacade.map(kelvinAap, AapOpprettRequest.class, context);
                })
                .flatMap(kelvinAapConsumer::createAap)
                .map(response -> {
                    if (isNotBlank(response.getSaksnummer())) {
                        log.info("AAP opprettet for ident {}: saksnummer {}", dollyPerson.getIdent(), response);
                        return "OK";
                    } else {
                        log.error("Feil ved oppretting av AAP for ident {}: {}", dollyPerson.getIdent(), response);
                        return "FEIL: %s %s".formatted(response.getStatus(), response.getError());
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