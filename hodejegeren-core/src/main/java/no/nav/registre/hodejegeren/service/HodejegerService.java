package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.DNR;
import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.FNR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
        
        final List<RsMeldingstype> alleMeldinger = sortereMeldingenePaaAarsakskode(syntetiserteMldPerAarsakskode);
        return tpsfConsumer.saveSkdEndringsmeldingerInTPSF(genereringsOrdreRequest.getGruppeId(), alleMeldinger);
    }
    
    private List<RsMeldingstype> sortereMeldingenePaaAarsakskode(Map<String, List<RsMeldingstype>> syntetiserteMldPerAarsakskode) {
        final TreeMap<Integer, List<RsMeldingstype>> treeMap = new TreeMap<>();
        treeMap.put(1, syntetiserteMldPerAarsakskode.getOrDefault("02", new ArrayList<>()));
        treeMap.put(2, syntetiserteMldPerAarsakskode.getOrDefault("91", new ArrayList<>()));
        treeMap.put(3, syntetiserteMldPerAarsakskode.getOrDefault("01", new ArrayList<>()));
        treeMap.put(4, syntetiserteMldPerAarsakskode.getOrDefault("39", new ArrayList<>()));
        
        Set<String> aarsakskoderMidtIRekkefoelgen = new HashSet<>(syntetiserteMldPerAarsakskode.keySet());
        aarsakskoderMidtIRekkefoelgen.removeAll(Arrays.asList("02", "91", "01", "39", "43", "32", "85"));
        for (String aarsakskode : aarsakskoderMidtIRekkefoelgen) {
            treeMap.put(treeMap.lastKey() + 1, syntetiserteMldPerAarsakskode.get(aarsakskode));
        }
        
        treeMap.put(treeMap.lastKey() + 1, syntetiserteMldPerAarsakskode.getOrDefault("85", new ArrayList<>()));
        treeMap.put(treeMap.lastKey() + 1, syntetiserteMldPerAarsakskode.getOrDefault("43", new ArrayList<>()));
        treeMap.put(treeMap.lastKey() + 1, syntetiserteMldPerAarsakskode.getOrDefault("32", new ArrayList<>()));
        
        return treeMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }
}
