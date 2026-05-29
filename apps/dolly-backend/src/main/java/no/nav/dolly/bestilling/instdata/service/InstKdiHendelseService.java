package no.nav.dolly.bestilling.instdata.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.inst.RsInstdataKdi;
import no.nav.dolly.service.TransactionHelperService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class InstKdiHendelseService {

    private static final String INNSETTELSE = "AAA";
    private static final String AVBRUDD_START = "BBB";
    private static final String AVBRUDD_SLUTT = "CCC";
    private static final String LOESLATELSE = "DDD";
    private static final String FORVENTET_LOESLATELSE = "EEE";

    private final TransactionHelperService transactionHelperService;

    public Mono<RsInstdataKdi> getOppdaterBestilling(RsDollyUtvidetBestilling bestilling, Long bestillingId, boolean isOpprettEndre) {

        if (isOpprettEndre) {
            return Mono.just(getOppdaterBestilling(bestilling.getInstdataKdi(), bestillingId))
                    .doOnNext(bestilling::setInstdataKdi)
                    .flatMap(oppdaterBestilling -> transactionHelperService.persister(bestillingId, bestilling)
                            .then(Mono.just(oppdaterBestilling)));
        } else {
            return Mono.just(bestilling.getInstdataKdi());
        }
    }

    private RsInstdataKdi getOppdaterBestilling(RsInstdataKdi bestilling, Long bestillingId) {

        oppdaterHendelser(bestilling.getInnsettelse(), INNSETTELSE, bestillingId);
        oppdaterHendelser(bestilling.getAvbruddStart(), AVBRUDD_START, bestillingId);
        oppdaterHendelser(bestilling.getAvbruddSlutt(), AVBRUDD_SLUTT, bestillingId);
        oppdaterHendelser(bestilling.getLoeslatelse(), LOESLATELSE, bestillingId);
        oppdaterHendelser(bestilling.getForventetLoeslatelse(), FORVENTET_LOESLATELSE, bestillingId);

        bestilling.getAnnullering()
                .forEach(annulering -> annulering.setPubliseringstidspunkt(
                        isNull(annulering.getPubliseringstidspunkt()) ?
                                LocalDateTime.now() : annulering.getPubliseringstidspunkt()));

        return bestilling;
    }

    private static void oppdaterHendelser(List<? extends RsInstdataKdi.Hendelse> hendelser, String type, Long bestillingId) {

        var loepenummer = new AtomicInteger(1);

        hendelser.forEach(hendelse -> {

            if (StringUtils.isBlank(hendelse.getMeldingId())) {
                hendelse.setMeldingId(UUID.randomUUID().toString());
            }
            if (isBlank(hendelse.getHendelseId())) {
                hendelse.setHendelseId(makeHendelseId(bestillingId, type, loepenummer.getAndIncrement()));
            }
            if (isNull(hendelse.getPubliseringstidspunkt())) {
                hendelse.setPubliseringstidspunkt(LocalDateTime.now());
            }
        });
    }

    private static String makeHendelseId(Long bestillingId, String type, Integer loepenummer) {

        return "0x%08d0%3s%08d0%3s%04d".formatted(bestillingId, type, bestillingId, type, loepenummer);
    }
}