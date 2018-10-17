package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.DNR;
import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.FNR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.registre.hodejegeren.consumer.TpsSyntetisererenConsumer;
import no.nav.registre.hodejegeren.provider.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;

/**
 * Hoved-service i Hodejegeren. Her blir Tps Synt. kalt. Den genererer syntetiske skdmeldinger og returnerer dem til hodejegeren. Hodejegeren
 * finner nye identer i ident-pool og eksisterende identer TPS i angitt miljø som oppfyller kriterier for de ulike skdmeldingene (årsakskodene).
 * Disse identene fylles inn i skdmeldingene.
 * Til slutt lagres skdmeldingene i TPSF databasen, i Skd-endringsmelding-tabellen.
 */
@Service
public class HodejegerService {
    
    @Autowired
    private TpsSyntetisererenConsumer tpsSyntetisererenConsumer;
    
    @Autowired
    private NyeIdenterService nyeIdenterService;
    
    public List<Long> puttIdenterIMeldingerOgLagre(GenereringsOrdreRequest genereringsOrdreRequest) {
        Map<String, List<RsMeldingstype>> syntetiserteMldPerAarsakskode = new HashMap<>();
        final Map<String, Integer> antallMeldingerPerAarsakskode = genereringsOrdreRequest.getAntallMeldingerPerAarsakskode();
        for (String aarsakskode : antallMeldingerPerAarsakskode.keySet()) {
            syntetiserteMldPerAarsakskode.put(aarsakskode, tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(aarsakskode, antallMeldingerPerAarsakskode.get(aarsakskode)));
        }
        //Valider skdmeldingene
        
        List<String> nyeIdenter = new ArrayList<>();
        nyeIdenter.addAll(nyeIdenterService.settInnNyeIdenterITrans1Meldinger(FNR, syntetiserteMldPerAarsakskode.get("01"))); //Bør jeg sette en øvre aldersgrense? åpent søk vil
        nyeIdenter.addAll(nyeIdenterService.settInnNyeIdenterITrans1Meldinger(FNR, syntetiserteMldPerAarsakskode.get("02")));
        nyeIdenter.addAll(nyeIdenterService.settInnNyeIdenterITrans1Meldinger(FNR, syntetiserteMldPerAarsakskode.get("39")));
        nyeIdenter.addAll(nyeIdenterService.settInnNyeIdenterITrans1Meldinger(DNR, syntetiserteMldPerAarsakskode.get("91")));
        
        //putt inn eksisterende identer i meldingene -  finn eksisterende identer, sjekk deres status quo, putt inn i meldingene
        //lagre ferdige meldinger i tpsf skdendringsmeldinger
        //returner tabell-id til de lagrede meldingene
        return new ArrayList<>();
    }
}
