package no.nav.dolly.bestilling.instdata.service;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.instdata.InstdataConsumer;
import no.nav.dolly.bestilling.instdata.domain.InstdataKdiDTO;
import no.nav.dolly.bestilling.instdata.domain.InstdataKdiResponse;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.inst.RsInstdataKdi;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.testnav.libs.dto.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public Mono<RsInstdataKdi> getBestilling(RsDollyUtvidetBestilling bestilling, Long bestillingId, String ident, String miljoe, boolean isOpprettEndre) {

        if (isOpprettEndre) {
            return instdataConsumer.getInstdataKdi(ident, miljoe)
                    .map(instdataKdiResponse -> oppdaterBestilling(instdataKdiResponse, bestilling.getInstdataKdi(), bestillingId, miljoe))
                    .doOnNext(bestilling::setInstdataKdi)
                    .flatMap(oppdaterBestilling -> transactionHelperService.persister(bestillingId, bestilling)
                            .then(Mono.just(oppdaterBestilling)));

        } else {
            return Mono.just(bestilling.getInstdataKdi());
        }
    }

    private RsInstdataKdi oppdaterBestilling(InstdataKdiResponse instdataKdiResponse, RsInstdataKdi bestilling, Long bestillingId, String miljoe) {

        var kdidata = instdataKdiResponse.getInstdataKdi().getData();

        var annuleringer = kdidata.getAnnullering().stream()
                .map(InstdataKdiDTO.Annullering::getHendelseId)
                .toList();

        var data = bestilling.getData();

        oppdaterHendelser(data.getInnsettelse(), getHendelseId(kdidata.getInnsettelse(),
                INNSETTELSE, bestillingId, annuleringer), miljoe);
        oppdaterHendelser(data.getAvbruddStart(), getHendelseId(kdidata.getAvbruddStart(),
                AVBRUDD_START, bestillingId, annuleringer), miljoe);
        oppdaterHendelser(data.getAvbruddSlutt(), getHendelseId(kdidata.getAvbruddSlutt(),
                AVBRUDD_SLUTT, bestillingId, annuleringer), miljoe);
        oppdaterHendelser(data.getLoeslatelse(), getHendelseId(kdidata.getLoeslatelse(),
                LOESLATELSE, bestillingId, annuleringer), miljoe);
        oppdaterHendelser(data.getForventetLoeslatelse(), getHendelseId(kdidata.getForventetLoeslatelse(),
                FORVENTET_LOESLATELSE, bestillingId, annuleringer), miljoe);

        data.getAnnullering()
                .forEach(annulering ->
                        annulering.getVersion().put(miljoe,
                                RsInstdataKdi.Version.builder()
                                        .hendelseId(annulering.getHendelseId())
                                        .publiseringstidspunkt(isNull(annulering.getPubliseringstidspunkt()) ?
                                                LocalDateTime.now() : annulering.getPubliseringstidspunkt())
                                        .build()));

        return bestilling;
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

    private static void oppdaterHendelser(List<? extends RsInstdataKdi.Hendelse> hendelser, String hendelseId, String miljoe) {

        hendelser.forEach(hendelse -> hendelse.getVersion().put(miljoe,
                RsInstdataKdi.Version.builder()
                        .hendelseId(hendelseId)
                        .publiseringstidspunkt(LocalDateTime.now())
                        .build()
        ));
    }
}