package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.DNR;
import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.FNR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.registre.hodejegeren.consumer.TpsSyntetisererenConsumer;
import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
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
    
    @Autowired
    private TpsfConsumer tpsfConsumer;
    
    @Autowired
    private ValidationService validationService;
    
    public List<Long> puttIdenterIMeldingerOgLagre(GenereringsOrdreRequest genereringsOrdreRequest) {
        final Map<String, Integer> antallMeldingerPerAarsakskode = genereringsOrdreRequest.getAntallMeldingerPerAarsakskode();
        final List<String> sorterteAarsakskoder = filtrerOgSorterBestilteAarsakskoder(antallMeldingerPerAarsakskode);
        
        List<Long> ids = new ArrayList<>();
        Map<String, List<RsMeldingstype>> syntetiserteMldPerAarsakskode = new HashMap<>();
        
        //        kall tpsfConsumer og hent tpsfstatsulistene
        List<String> nyeIdenter = new ArrayList<>();

        for (String aarsakskode : sorterteAarsakskoder) {
            syntetiserteMldPerAarsakskode.put(aarsakskode, tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(aarsakskode, antallMeldingerPerAarsakskode.get(aarsakskode)));
            validationService.logAndRemoveInvalidMessages(syntetiserteMldPerAarsakskode.get(aarsakskode));
            
            nyeIdenter.addAll(nyeIdenterService.settInnNyeIdenterITrans1Meldinger(FNR, syntetiserteMldPerAarsakskode.get("01"))); //Bør jeg sette en øvre aldersgrense? åpent søk vil
            nyeIdenter.addAll(nyeIdenterService.settInnNyeIdenterITrans1Meldinger(FNR, syntetiserteMldPerAarsakskode.get("02")));
            nyeIdenter.addAll(nyeIdenterService.settInnNyeIdenterITrans1Meldinger(FNR, syntetiserteMldPerAarsakskode.get("39")));
            nyeIdenter.addAll(nyeIdenterService.settInnNyeIdenterITrans1Meldinger(DNR, syntetiserteMldPerAarsakskode.get("91")));
            
            //putt inn eksisterende identer i meldingene -  finn eksisterende identer, sjekk deres status quo, putt inn i meldingene
            
            ids.addAll(tpsfConsumer.saveSkdEndringsmeldingerInTPSF(genereringsOrdreRequest.getGruppeId(), syntetiserteMldPerAarsakskode.get(aarsakskode)));
        }
        return ids;
    }
    
    private List<String> filtrerOgSorterBestilteAarsakskoder(Map<String, Integer> antallMeldingerPerAarsakskode) {
        List<String> sorterteAarsakskoder = Arrays.asList(AarsakskoderTrans1.values()).stream().map(AarsakskoderTrans1::getAarsakskode).collect(Collectors.toList());
        sorterteAarsakskoder.retainAll(antallMeldingerPerAarsakskode.keySet());
        return sorterteAarsakskoder;
    }
}
