package no.nav.registre.hodejegeren.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSMELDING;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSNUMMERKORREKSJON;
import static no.nav.registre.hodejegeren.service.Endringskoder.INNVANDRING;
import static no.nav.registre.hodejegeren.service.Endringskoder.TILDELING_DNUMMER;
import static no.nav.registre.hodejegeren.service.HodejegerService.TRANSAKSJONSTYPE;
import static no.nav.registre.hodejegeren.service.utilities.IdentUtility.getFoedselsdatoFraFnr;

@Service
@Slf4j
public class EksisterendeIdenterService {

    @Autowired
    TpsfConsumer tpsfConsumer;

    @Autowired
    TpsStatusQuoService tpsStatusQuoService;

    private static final String ROUTINE_PERSDATA = "FS03-FDNUMMER-PERSDATA-O";

    public List<String> hentVokseneIdenterIGruppe(Long gruppeId, String miljoe, int henteAntall) {
        Random rand = new Random();
        List<String> hentedeIdenter = new ArrayList<>(henteAntall);

        List<String> identer = finnLevendeIdenter(gruppeId);
        List<String> gyldigeIdenter = identer.stream().filter(ident -> getFoedselsdatoFraFnr(ident).isBefore(LocalDate.now().minusYears(18))).collect(Collectors.toList());
        if (henteAntall > gyldigeIdenter.size()) {
            henteAntall = gyldigeIdenter.size();
        }

        if (gyldigeIdenter.isEmpty()) {
            return hentedeIdenter;
        }

        int index = rand.nextInt(gyldigeIdenter.size());
        String ident = gyldigeIdenter.get(index);

        while (hentedeIdenter.size() != henteAntall) {
            try {
                //Finn status på ident
                Map<String, String> status = tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSDATA, Arrays.asList("datoDo", "statsborger"), miljoe, ident);
                //Hvis dato død ikke er satt så lever personen i følge tps
                if (status.get("datoDo") == null || status.get("datoDo").isEmpty()) {
                    //Legg til i hentede identer
                    hentedeIdenter.add(ident);
                }
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
            //Fjern ident uansett hva, enten har den blitt lagt til eller så er den ugyldig
            gyldigeIdenter.remove(index);
            //Hvis gyldige er tom etter å ha fjernet, avslutt løkken
            if (gyldigeIdenter.isEmpty()) {
                break;
            }
            //Gå til neste ident
            index = rand.nextInt(gyldigeIdenter.size());
            ident = gyldigeIdenter.get(index);
        }
        return hentedeIdenter;
    }

    public List<String> finnAlleIdenter(Long gruppeId) {
        return new ArrayList<>(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(
                gruppeId, Arrays.asList(
                        FOEDSELSMELDING.getAarsakskode(),
                        INNVANDRING.getAarsakskode(),
                        FOEDSELSNUMMERKORREKSJON.getAarsakskode(),
                        TILDELING_DNUMMER.getAarsakskode()),
                TRANSAKSJONSTYPE));
    }

    public List<String> finnLevendeIdenter(Long gruppeId) {

        List<String> opprettedeIdenterITpsf = finnAlleIdenter(gruppeId);
        List<String> doedeOgUtvandredeIdenter = finnDoedeOgUtvandredeIdenter(gruppeId);

        List<String> levendeIdenterINorge = new ArrayList<>(opprettedeIdenterITpsf);
        levendeIdenterINorge.removeAll(doedeOgUtvandredeIdenter);

        return levendeIdenterINorge;
    }

    public List<String> finnDoedeOgUtvandredeIdenter(Long gruppeId) {
        return new ArrayList<>(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(
                gruppeId, Arrays.asList(
                        Endringskoder.DOEDSMELDING.getAarsakskode(),
                        Endringskoder.UTVANDRING.getAarsakskode()),
                TRANSAKSJONSTYPE));
    }

    public List<String> finnGifteIdenter(Long gruppeId) {
        return new ArrayList<>(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(
                gruppeId, Collections.singletonList(
                        Endringskoder.VIGSEL.getAarsakskode()),
                TRANSAKSJONSTYPE));
    }
}
