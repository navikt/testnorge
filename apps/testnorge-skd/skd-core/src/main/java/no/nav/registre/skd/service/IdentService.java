package no.nav.registre.skd.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import no.nav.registre.skd.consumer.IdentPoolConsumer;
import no.nav.registre.skd.consumer.TpsfConsumer;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentService {

    private final TpsfConsumer tpsfConsumer;
    private final IdentPoolConsumer identPoolConsumer;

    private static final Map<String, String> gamleTilNyeKommunenummer;

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

    public List<String> oppdaterAdresseinformasjon(
            Long avspillergruppeId,
            String miljoe
    ) {
        int pageNumber = 0;
        List<RsMeldingstype> meldinger;
        List<String> oppdaterteIdenter = new ArrayList<>();
        do {
            meldinger = tpsfConsumer.getGruppePaginert(avspillergruppeId, pageNumber++);
            if (!meldinger.isEmpty()) {
                for (var melding : meldinger) {
                    String kommunenummer = ((RsMeldingstype1Felter) melding).getKommunenummer();
                    if (gamleTilNyeKommunenummer.containsKey(kommunenummer)) {
                        String fodselsdato = ((RsMeldingstype1Felter) melding).getFodselsdato();
                        String personnummer = ((RsMeldingstype1Felter) melding).getPersonnummer();
                        String fnr = fodselsdato + personnummer;
                        oppdaterteIdenter.add(fnr);
                    }
                }
            }
            if (pageNumber >= 3)
                break;
        } while (!meldinger.isEmpty());
        return oppdaterteIdenter;
    }
}
