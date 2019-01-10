package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.DATO_DO;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.NAV_ENHET;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.STATSBORGER;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
import no.nav.registre.hodejegeren.exception.ManglendeInfoITpsException;

@Service
@Slf4j
public class EksisterendeIdenterService {

    private static final String ROUTINE_PERSDATA = "FS03-FDNUMMER-PERSDATA-O";
    private static final String ROUTINE_KERNINFO = "FS03-FDNUMMER-KERNINFO-O";
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
            log.info("Antall ønskede identer å hente er større enn myndige identer i avspiller gruppe. - HenteAntall:{} MyndigeIdenter:{}", henteAntall, gyldigeIdenter.size());
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
                Map<String, String> status = tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSDATA, Arrays.asList(DATO_DO, STATSBORGER), miljoe, ident);
                if (status.get(DATO_DO) == null || status.get(DATO_DO).isEmpty()) {
                    hentedeIdenter.add(ident);
                } else {
                    identerFeilet++;
                }
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
                identerFeilet++;
            } catch (ManglendeInfoITpsException e) {
                log.warn(e.getMessage()); // TODO - Vi bør gi bruker beskjed om at ikke alle identene kunne hentes (men fortsatt ikke stoppe eksekveringen)
            }
            gyldigeIdenter.remove(index);
            if (gyldigeIdenter.isEmpty()) {
                if (hentedeIdenter.size() != henteAntall) {
                    log.info("Fant ikke ønsket antall identer fordi status i TPS og TPSF ikke samsvarte. - Antall identer som feilet: {}", identerFeilet);
                }
                break;
            }
            index = rand.nextInt(gyldigeIdenter.size());
            ident = gyldigeIdenter.get(index);
        }
        return hentedeIdenter;
    }

    public Map<String, String> hentFnrMedNavKontor(String miljoe, List<String> identer) {
        Map<String, String> fnrMedNavKontor = new HashMap<>();

        for (String ident : identer) {
            Map<String, String> feltMedStatusQuo;

            try {
                feltMedStatusQuo = tpsStatusQuoService.hentStatusQuo(ROUTINE_KERNINFO, Arrays.asList(NAV_ENHET), miljoe, ident);
                fnrMedNavKontor.put(ident, feltMedStatusQuo.get(NAV_ENHET));
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            } catch (ManglendeInfoITpsException e) {
                log.warn(e.getMessage()); // TODO - Vi bør gi bruker beskjed om at ikke alle identene kunne hentes (men fortsatt ikke stoppe eksekveringen)
            }
        }

        return fnrMedNavKontor;
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
