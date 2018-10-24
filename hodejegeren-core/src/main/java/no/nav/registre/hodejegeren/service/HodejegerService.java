package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.DNR;
import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.FNR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.registre.hodejegeren.consumer.TpsSyntetisererenConsumer;
import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
import no.nav.registre.hodejegeren.provider.rs.requests.GenereringsOrdreRequest;

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
        
        //        kall tpsfConsumer og hent tpsfstatsulistene
        
        for (String aarsakskode : sorterteAarsakskoder) {
            List syntetiserteSkdmeldinger = tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(aarsakskode, antallMeldingerPerAarsakskode.get(aarsakskode));
            validationService.logAndRemoveInvalidMessages(syntetiserteSkdmeldinger);
            if (Arrays.asList("01", "02", "39").contains(aarsakskode)) {
                nyeIdenterService.settInnNyeIdenterITrans1Meldinger(FNR, syntetiserteSkdmeldinger); //Bør jeg sette en øvre aldersgrense? åpent søk vil
            }
            if ("91".equals(aarsakskode)) {
                nyeIdenterService.settInnNyeIdenterITrans1Meldinger(DNR, syntetiserteSkdmeldinger);
            }
            //putt inn eksisterende identer i meldingene -  finn eksisterende identer, sjekk deres status quo, putt inn i meldingene
            
            ids.addAll(tpsfConsumer.saveSkdEndringsmeldingerInTPSF(genereringsOrdreRequest.getGruppeId(), syntetiserteSkdmeldinger));
        }
        return ids;
    }
    
    private List<String> filtrerOgSorterBestilteAarsakskoder(Map<String, Integer> antallMeldingerPerAarsakskode) {
        List<String> sorterteAarsakskoder = Arrays.asList(AarsakskoderTrans1.values()).stream().map(AarsakskoderTrans1::getAarsakskode).collect(Collectors.toList());
        sorterteAarsakskoder.retainAll(antallMeldingerPerAarsakskode.keySet());
        return sorterteAarsakskoder;
    }
}
