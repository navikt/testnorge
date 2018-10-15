package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.DNR;
import static no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest.IdentType.FNR;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.registre.hodejegeren.consumer.IdentPoolConsumer;
import no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;

/**
 * Service for å plassere nye identer i skdmeldingene.
 */
@Service
public class NyeIdenterService {
    
    @Autowired
    private IdentPoolConsumer identPoolConsumer;
    
    public void settInnNyeIdenter(List<RsMeldingstype> request) {
        settInnNyeIdenterIAktuelleMeldinger(FNR, request, Arrays.asList("01", "02", "39"));
        settInnNyeIdenterIAktuelleMeldinger(DNR, request, Arrays.asList("91"));
    }
    
    public List<String> settInnNyeIdenterIAktuelleMeldinger(HentIdenterRequest.IdentType identType, List<RsMeldingstype> request, List<String> aarsakskoder) {
        List<RsMeldingstype> aktuelleMeldinger = request.stream()
                .filter(mld -> mld instanceof RsMeldingstype1Felter && aarsakskoder.contains(mld.getAarsakskode()))
                .collect(Collectors.toList());
        int antallNyeIdenter = aktuelleMeldinger.size();
        List<String> identer = identPoolConsumer.hentNyeIdenter(HentIdenterRequest.builder().antall(antallNyeIdenter).identtype(identType).build());
        for (int i = 0; i < antallNyeIdenter; i++) {
            ((RsMeldingstype1Felter) aktuelleMeldinger.get(i)).setFodselsdato(identer.get(i).substring(0, 6));
            ((RsMeldingstype1Felter) aktuelleMeldinger.get(i)).setPersonnummer(identer.get(i).substring(6));
        }
        return identer;
    }
}
