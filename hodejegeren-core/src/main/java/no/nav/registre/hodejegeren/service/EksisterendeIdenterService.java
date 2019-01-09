package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSMELDING;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSNUMMERKORREKSJON;
import static no.nav.registre.hodejegeren.service.Endringskoder.INNVANDRING;
import static no.nav.registre.hodejegeren.service.Endringskoder.TILDELING_DNUMMER;
import static no.nav.registre.hodejegeren.service.HodejegerService.TRANSAKSJONSTYPE;
import static no.nav.registre.hodejegeren.service.utilities.IdentUtility.getFoedselsdatoFraFnr;

import lombok.extern.slf4j.Slf4j;
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

import no.nav.registre.hodejegeren.consumer.TpsfConsumer;

@Service
@Slf4j
public class EksisterendeIdenterService {

    private static final String ROUTINE_PERSDATA = "FS03-FDNUMMER-PERSDATA-O";
    @Autowired
    private TpsfConsumer tpsfConsumer;
    @Autowired
    private TpsStatusQuoService tpsStatusQuoService;
    @Autowired
    private Random rand;

    public List<String> hentMyndigeIdenterIAvspillerGruppe(Long gruppeId, String miljoe, int henteAntall) {
        List<String> hentedeIdenter = new ArrayList<>(henteAntall);
        List<String> identer = finnLevendeIdenter(gruppeId);
        List<String> gyldigeIdenter = identer.stream().filter(ident -> getFoedselsdatoFraFnr(ident).isBefore(LocalDate.now().minusYears(18))).collect(Collectors.toList());

        if (henteAntall > gyldigeIdenter.size()) {
            log.info("Antall ønskede identer å hente er større enn myndige identer i avspiller gruppe.\n HenteAntall:{} MyndigeIdenter:{}", henteAntall, gyldigeIdenter.size());
            henteAntall = gyldigeIdenter.size();
        }

        if (gyldigeIdenter.isEmpty()) {
            return hentedeIdenter;
        }

        int index = rand.nextInt(gyldigeIdenter.size());
        String ident = gyldigeIdenter.get(index);

        int identerFeilet = 0;

        while (hentedeIdenter.size() != henteAntall) {
            try {
                Map<String, String> status = tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSDATA, Arrays.asList("datoDo", "statsborger"), miljoe, ident);
                if (status.get("datoDo") == null || status.get("datoDo").isEmpty()) {
                    hentedeIdenter.add(ident);
                } else {
                    identerFeilet++;
                }
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
                identerFeilet++;
            }
            gyldigeIdenter.remove(index);
            if (gyldigeIdenter.isEmpty()) {
                if (hentedeIdenter.size() != henteAntall) {
                    log.info("Fant ikke ønsket antall identer pga de var døde i TPS og ikke TPSF (kan også inneholde feilparset FNR fra TPS).\n Antall som var døde i TPS: {}", identerFeilet);
                }
                break;
            }
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
