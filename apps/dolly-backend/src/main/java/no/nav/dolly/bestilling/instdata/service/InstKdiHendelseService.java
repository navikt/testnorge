package no.nav.dolly.bestilling.instdata.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.instdata.InstdataConsumer;
import no.nav.dolly.bestilling.instdata.domain.InstdataKdiDTO;
import no.nav.dolly.bestilling.instdata.domain.InstdataKdiResponse;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.inst.RsInstdataKdi;
import no.nav.dolly.service.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class InstKdiHendelseService {

    private static final String INNSETTELSE = "A";
    private static final String AVBRUDD_START = "B";
    private static final String AVBRUDD_SLUTT = "C";
    private static final String LOESLATELSE = "D";
    private static final String FORVENTET_LOESLATELSE = "E";

    private final InstdataConsumer instdataConsumer;
    private final TransactionHelperService transactionHelperService;

    public Mono<RsInstdataKdi> getOppdaterBestilling(RsDollyUtvidetBestilling bestilling, Long bestillingId, String ident, String miljoe, boolean isOpprettEndre) {

        if (isOpprettEndre || hasEmptyHendelseId(bestilling.getInstdataKdi())) {

            return instdataConsumer.getInstdataKdi(ident, miljoe)
                    .filter(response -> response.getStatus().is2xxSuccessful())
                    .map(instdataKdiResponse -> getOppdaterBestilling(instdataKdiResponse, bestilling.getInstdataKdi(), bestillingId))
                    .doOnNext(bestilling::setInstdataKdi)
                    .flatMap(oppdaterBestilling -> transactionHelperService.persister(bestillingId, bestilling)
                            .then(Mono.just(oppdaterBestilling)));

        } else {
            return Mono.just(bestilling.getInstdataKdi());
        }
    }

    private RsInstdataKdi getOppdaterBestilling(InstdataKdiResponse instdataKdiResponse, RsInstdataKdi bestilling, Long bestillingId) {

        var kdidata = instdataKdiResponse.getInstdataKdi().getData();

        var annuleringer = kdidata.getAnnullering().stream()
                .map(InstdataKdiDTO.Annullering::getHendelseId)
                .toList();

        oppdaterHendelser(bestilling.getInnsettelse(), getHendelseId(kdidata.getInnsettelse(),
                INNSETTELSE, bestillingId, annuleringer));
        oppdaterHendelser(bestilling.getAvbruddStart(), getHendelseId(kdidata.getAvbruddStart(),
                AVBRUDD_START, bestillingId, annuleringer));
        oppdaterHendelser(bestilling.getAvbruddSlutt(), getHendelseId(kdidata.getAvbruddSlutt(),
                AVBRUDD_SLUTT, bestillingId, annuleringer));
        oppdaterHendelser(bestilling.getLoeslatelse(), getHendelseId(kdidata.getLoeslatelse(),
                LOESLATELSE, bestillingId, annuleringer));
        oppdaterHendelser(bestilling.getForventetLoeslatelse(), getHendelseId(kdidata.getForventetLoeslatelse(),
                FORVENTET_LOESLATELSE, bestillingId, annuleringer));

        bestilling.getAnnullering()
                .forEach(annulering -> annulering.setPubliseringstidspunkt(
                        isNull(annulering.getPubliseringstidspunkt()) ?
                                LocalDateTime.now() : annulering.getPubliseringstidspunkt()));

        return bestilling;
    }

    private static boolean hasEmptyHendelseId(RsInstdataKdi instdataKdi) {

        return Stream.of(instdataKdi.getInnsettelse(), instdataKdi.getAvbruddStart(),
                        instdataKdi.getAvbruddSlutt(), instdataKdi.getLoeslatelse(),
                        instdataKdi.getForventetLoeslatelse())
                .flatMap(Collection::stream)
                .anyMatch(hendelse -> isNull(hendelse.getHendelseId()));
    }

    private static String getHendelseId(List<? extends InstdataKdiDTO.Hendelse> hendelser,
                                        String type,
                                        Long bestillingId,
                                        List<String> annuleringer) {

        var hendelseId = getHendelseId(hendelser, type, bestillingId);

        return !annuleringer.contains(hendelseId) ? hendelseId : getNextHendelseId(hendelseId);
    }

    private static String getNextHendelseId(String hendelseId) {

        return hendelseId.substring(0, 21) + String.format("%010d", Long.parseLong(hendelseId.substring(21)) + 1);
    }

    private static String makeHendelseId(Long bestillingId, String type, Integer loepenummer) {

        return "0x%010d00000%s00%010d".formatted(bestillingId, type, loepenummer);
    }

    private static String getHendelseId(List<? extends InstdataKdiDTO.Hendelse> hendelser, String type, Long bestillingId) {

        return hendelser.stream()
                .map(InstdataKdiDTO.Hendelse::getHendelseId)
                .max(String::compareTo)
                .orElse(makeHendelseId(bestillingId, type, 1));
    }

    private static void oppdaterHendelser(List<? extends RsInstdataKdi.Hendelse> hendelser, String hendelseId) {

        hendelser.forEach(hendelse -> {
            hendelse.setHendelseId(hendelseId);
            if (isNull(hendelse.getPubliseringstidspunkt())) {
                hendelse.setPubliseringstidspunkt(LocalDateTime.now());
            }
        });
    }
}