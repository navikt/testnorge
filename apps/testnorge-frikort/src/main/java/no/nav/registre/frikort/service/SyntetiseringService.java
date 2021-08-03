package no.nav.registre.frikort.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.frikort.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.frikort.provider.rs.request.SyntetiserFrikortRequest;
import no.nav.registre.frikort.provider.rs.response.SyntetiserFrikortResponse;
import no.nav.testnav.libs.servletcore.util.IdentUtil;
import no.nav.registre.frikort.service.util.ServiceUtils;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyntetiseringService {

    private static final int ANTALL_EGENANDELER_PER_IDENT = 1;
    private static final int MIN_ALDER = 16;
    private static final int MAX_ALDER = 70;
    private static final Long AVSPILLERGRUPPE_MININORGE = 100000883L;
    private static final String MILJOE_MININORGE = "q2";
    private static final Boolean VALIDER_EGENANDELER = true;

    private final HodejegerenConsumer hodejegerenConsumer;
    private final HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;
    private final ServiceUtils serviceUtils;

    public List<SyntetiserFrikortResponse> opprettSyntetiskeEgenandeler(
            SyntetiserFrikortRequest syntetiserFrikortRequest,
            boolean leggPaaKoe
    ) throws JAXBException {
        List<String> identer;
        if (AVSPILLERGRUPPE_MININORGE.equals(syntetiserFrikortRequest.getAvspillergruppeId()) && MILJOE_MININORGE.equals(syntetiserFrikortRequest.getMiljoe().toLowerCase())) {
            identer = hodejegerenHistorikkConsumer.hentIdenterMedKontonummer(syntetiserFrikortRequest.getAntallNyeIdenter());
            identer.removeIf(ident -> ChronoUnit.YEARS.between(IdentUtil.getFoedselsdatoFraIdent(ident), LocalDate.now()) < MIN_ALDER);
        } else {
            var identerMedKontonummer = hodejegerenConsumer
                    .getIdenterMedKontonummer(
                            syntetiserFrikortRequest.getAvspillergruppeId(),
                            syntetiserFrikortRequest.getMiljoe(),
                            syntetiserFrikortRequest.getAntallNyeIdenter(),
                            MIN_ALDER,
                            MAX_ALDER
                    );

            identer = identerMedKontonummer.stream().map(KontoinfoResponse::getFnr).collect(Collectors.toList());
        }
        var identMap = identer.stream().collect(Collectors.toMap(ident -> ident, ident -> ANTALL_EGENANDELER_PER_IDENT, (a, b) -> b));

        return serviceUtils.hentSyntetiskeEgenandelerOgLeggPaaKoe(identMap, leggPaaKoe, VALIDER_EGENANDELER);
    }
}
