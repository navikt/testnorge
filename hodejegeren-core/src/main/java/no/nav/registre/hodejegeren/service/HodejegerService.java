package no.nav.registre.hodejegeren.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.registre.hodejegeren.consumer.TpsSyntetisererenConsumer;
import no.nav.registre.hodejegeren.provider.rs.requests.GenereringsOrdreRequest;

/**
 *  Hoved-service i Hodejegeren. Her blir Tps Synt. kalt. Den genererer syntetiske skdmeldinger og returnerer dem til hodejegeren. Hodejegeren
 *  finner nye identer i ident-pool og eksisterende identer TPS i angitt miljø som oppfyller kriterier for de ulike skdmeldingene (årsakskodene).
 *  Disse identene fylles inn i skdmeldingene.
 *  Til slutt lagres skdmeldingene i TPSF databasen, i Skd-endringsmelding-tabellen.
 */
@Service
public class HodejegerService {
    
    @Autowired
    private TpsSyntetisererenConsumer tpsSyntetisererenConsumer;
    
    public List<Long> puttIdenterIMeldingerOgLagre(GenereringsOrdreRequest genereringsOrdreRequest) {
        //List<RsMeldingstype> Konsumer Syntetisereren - hent nye syntetiserte skdmeldinger
        //Valider skdmeldingene
        
        //Putt inn nye identer - konsumer ident-pool
        //putt inn eksisterende identer i meldingene -  finn eksisterende identer, sjekk deres status quo, putt inn i meldingene
        //lagre ferdige meldinger i tpsf skdendringsmeldinger
        //returner tabell-id til de lagrede meldingene
        return new ArrayList<>();
    }
}
