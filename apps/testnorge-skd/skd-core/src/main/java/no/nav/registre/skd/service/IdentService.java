package no.nav.registre.skd.service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.skd.consumer.IdentPoolConsumer;
import no.nav.registre.skd.consumer.TpsfConsumer;
import no.nav.registre.skd.consumer.requests.SendToTpsRequest;
import no.nav.registre.skd.consumer.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.skd.exceptions.IdentIOException;
import no.nav.registre.skd.service.utilities.IdenterCache;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    private static final Map<String, String> gamleTilNyeKommunenummer;
    private static final int PARTITION_SIZE = 100;

    private final TpsfConsumer tpsfConsumer;
    private final IdentPoolConsumer identPoolConsumer;
    private final HodejegerenConsumer hodejegerenConsumer;
    private final IdenterCache oppdaterteIdenterCache;

    static {
        gamleTilNyeKommunenummer = new HashMap<>();
        URL resource = Resources.getResource("kommunenummer.json");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> map = objectMapper.readValue(resource, new TypeReference<>() {
            });
            map.forEach(gamleTilNyeKommunenummer::put);
        } catch (IOException e) {
            log.error("Kunne ikke laste inn kommunenummer.", e);
            throw new IdentIOException("Kunne ikke laste inn kommunenummer.", e);
        }
    }

    public List<Long> slettIdenterFraAvspillergruppe(
            Long avspillergruppeId,
            List<String> miljoer,
            List<String> identer
    ) {
        tpsfConsumer.slettIdenterFraTps(miljoer, identer);
        var meldingIderTilhoerendeIdenter = tpsfConsumer.getMeldingIderTilhoerendeIdenter(avspillergruppeId, identer);
        var tpsfResponse = tpsfConsumer.slettMeldingerFraTpsf(meldingIderTilhoerendeIdenter);
        if (tpsfResponse.getStatusCode().is2xxSuccessful()) {
            List<String> frigjorteIdenter = identPoolConsumer.frigjoerLedigeIdenter(identer);
            log.info("Identer som ble frigjort i ident-pool: {}", frigjorteIdenter.toString());
            return meldingIderTilhoerendeIdenter;
        } else {
            return new ArrayList<>();
        }
    }

    public List<Long> oppdaterKommunenummerIAvspillergruppe(
            Long avspillergruppeId,
            String miljoe,
            Boolean sendTilTps
    ) {
        List<Long> oppdaterteIder = new ArrayList<>();
        var identerIAvspillergruppe = hodejegerenConsumer.get(avspillergruppeId);
        int antallIderSjekket = 0;
        int antallIdenterSjekket = 0;
        for (var partisjonerteIdenter : Lists.partition(identerIAvspillergruppe, PARTITION_SIZE)) {
            List<RsMeldingstype> meldingerSomSkalOppdateres = new ArrayList<>();
            var ikkeCachedeIdenter = getIdenterSomIkkeErCachet(partisjonerteIdenter);
            var meldingIderTilhoerendeIdenter = tpsfConsumer.getMeldingIderTilhoerendeIdenter(avspillergruppeId, ikkeCachedeIdenter);

            List<RsMeldingstype> meldinger = tpsfConsumer.getMeldingerMedIds(meldingIderTilhoerendeIdenter.stream().map(Objects::toString).collect(Collectors.toList()));
            for (var melding : meldinger) {
                var kommunenummer = ((RsMeldingstype1Felter) melding).getKommunenummer();
                if (gamleTilNyeKommunenummer.containsKey(kommunenummer)) {
                    ((RsMeldingstype1Felter) melding).setKommunenummer(gamleTilNyeKommunenummer.get(kommunenummer));
                    meldingerSomSkalOppdateres.add(melding);
                    oppdaterteIder.add(melding.getId());
                }
                antallIderSjekket++;
            }
            antallIdenterSjekket += partisjonerteIdenter.size();
            var meldingIdsSomIkkeKunneOppdateres = tpsfConsumer.oppdaterSkdMeldinger(meldingerSomSkalOppdateres);
            oppdaterteIder.removeAll(meldingIdsSomIkkeKunneOppdateres);
            if (sendTilTps) {
                var meldingIdsSomSkalOppdateres = meldingerSomSkalOppdateres.stream().map(RsMeldingstype::getId).collect(Collectors.toList());
                meldingIdsSomSkalOppdateres.removeAll(meldingIdsSomIkkeKunneOppdateres);
                sendToTps(avspillergruppeId, new SendToTpsRequest(miljoe, meldingIdsSomSkalOppdateres));
            }
            oppdaterteIdenterCache.addToCache(new HashSet<>(partisjonerteIdenter));
            log.info("Antall identer kontrollert: {}. Antall meldinger kontrollert: {}. Antall meldinger oppdatert: {}.", antallIdenterSjekket, antallIderSjekket, oppdaterteIder.size());
        }

        return oppdaterteIder;
    }

    public SkdMeldingerTilTpsRespons sendToTps(
            Long avspillergruppeId,
            SendToTpsRequest sendToTpsRequest
    ) {
        var response = new SkdMeldingerTilTpsRespons(0, 0, new ArrayList<>());
        for (var partisjonerteIder : Lists.partition(sendToTpsRequest.getIds(), PARTITION_SIZE)) {
            try {
                var partisjonertResponse = tpsfConsumer.sendSkdmeldingerToTps(avspillergruppeId, new SendToTpsRequest(sendToTpsRequest.getEnvironment(), partisjonerteIder));
                response.setAntallSendte(response.getAntallSendte() + partisjonertResponse.getAntallSendte());
                response.setAntallFeilet(response.getAntallFeilet() + partisjonertResponse.getAntallFeilet());
                response.getStatusFraFeilendeMeldinger().addAll(partisjonertResponse.getStatusFraFeilendeMeldinger());
            } catch (HttpStatusCodeException e) {
                log.error("Kunne ikke sende meldinger med id-er til tps: {}", partisjonerteIder.toString(), e);
            }
        }
        return response;
    }

    private List<String> getIdenterSomIkkeErCachet(List<String> identer) {
        var alleredeOppdaterteIdenter = oppdaterteIdenterCache.getIdenterCache();
        List<String> ikkeCachedeIdenter = new ArrayList<>();
        for (var ident : identer) {
            if (!alleredeOppdaterteIdenter.contains(ident)) {
                ikkeCachedeIdenter.add(ident);
            }
        }
        return ikkeCachedeIdenter;
    }
}
