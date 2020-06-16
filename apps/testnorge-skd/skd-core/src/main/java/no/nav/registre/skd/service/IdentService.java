package no.nav.registre.skd.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.skd.consumer.IdentPoolConsumer;
import no.nav.registre.skd.consumer.TpsfConsumer;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    private static final Map<String, String> gamleTilNyeKommunenummer;
    private static final int PARTITION_SIZE_GET_MELDING_IDER = 100;
    private static final int PARTITION_SIZE_OPPDATER_MELDINGER = 50;

    private final TpsfConsumer tpsfConsumer;
    private final IdentPoolConsumer identPoolConsumer;
    private final HodejegerenConsumer hodejegerenConsumer;

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
            throw new RuntimeException("Kunne ikke laste inn kommunenummer.", e);
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
            Long avspillergruppeId
    ) {
        List<RsMeldingstype> meldingerSomSkalOppdateres = new ArrayList<>();
        var identerIAvspillergruppe = hodejegerenConsumer.get(avspillergruppeId);
        int antallIderSjekket = 0;
        int antallIdenterSjekket = 0;
        for (var partisjonerteIdenter : Lists.partition(identerIAvspillergruppe, PARTITION_SIZE_GET_MELDING_IDER)) {
            var meldingIderTilhoerendeIdenter = tpsfConsumer.getMeldingIderTilhoerendeIdenter(avspillergruppeId, partisjonerteIdenter);

            for (var meldingId : meldingIderTilhoerendeIdenter) {
                var melding = tpsfConsumer.getMeldingMedId(meldingId);
                if (melding != null) {
                    if (melding.getId() == null) {
                        melding.setId(meldingId);
                    }
                    var kommunenummer = ((RsMeldingstype1Felter) melding).getKommunenummer();
                    if (gamleTilNyeKommunenummer.containsKey(kommunenummer)) {
                        ((RsMeldingstype1Felter) melding).setKommunenummer(gamleTilNyeKommunenummer.get(kommunenummer));
                        meldingerSomSkalOppdateres.add(melding);
                    }
                }
                antallIderSjekket++;
            }
            antallIdenterSjekket += partisjonerteIdenter.size();
        }

        for (var partisjonerteMeldinger : Lists.partition(meldingerSomSkalOppdateres, PARTITION_SIZE_OPPDATER_MELDINGER)) {
            var meldingerSomIkkeKunneOppdateres = tpsfConsumer.oppdaterSkdMeldinger(partisjonerteMeldinger);
            meldingerSomSkalOppdateres.removeAll(meldingerSomIkkeKunneOppdateres);
        }

        log.info("Antall identer kontrollert: {}. Antall meldinger kontrollert: {}. Antall meldinger oppdatert: {}.", antallIdenterSjekket, antallIderSjekket, meldingerSomSkalOppdateres.size());

        return meldingerSomSkalOppdateres.stream().map(RsMeldingstype::getId).collect(Collectors.toList());
    }
}
