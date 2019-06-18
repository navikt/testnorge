package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.DATO_DO;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.NAV_ENHET;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.STATSBORGER;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSMELDING;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSNUMMERKORREKSJON;
import static no.nav.registre.hodejegeren.service.Endringskoder.INNVANDRING;
import static no.nav.registre.hodejegeren.service.Endringskoder.TILDELING_DNUMMER;
import static no.nav.registre.hodejegeren.service.TpsStatusQuoService.AKSJONSKODE;
import static no.nav.registre.hodejegeren.service.utilities.IdentUtility.getFoedselsdatoFraFnr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import no.nav.registre.hodejegeren.provider.rs.responses.relasjon.Relasjon;
import no.nav.registre.hodejegeren.provider.rs.responses.relasjon.RelasjonsResponse;

@Service
@Slf4j
public class EksisterendeIdenterService {

    private static final String ROUTINE_PERSDATA = "FS03-FDNUMMER-PERSDATA-O";
    private static final String ROUTINE_KERNINFO = "FS03-FDNUMMER-KERNINFO-O";
    private static final String ROUTINE_PERSRELA = "FS03-FDNUMMER-PERSRELA-O";
    public static final String TRANSAKSJONSTYPE = "1";

    @Autowired
    private TpsfConsumer tpsfConsumer;

    @Autowired
    private TpsStatusQuoService tpsStatusQuoService;

    @Autowired
    private Random rand;

    public List<String> hentLevendeIdenterIGruppeOgSjekkStatusQuo(Long gruppeId, String miljoe, int henteAntall, int minimumAlder) {
        List<String> gyldigeIdenter = finnAlleIdenterOverAlder(gruppeId, minimumAlder);

        if (gyldigeIdenter.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> collected = gyldigeIdenter.parallelStream().filter(gyldigIdent -> {
            Map<String, String> status = null;
            try {
                status = tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSDATA, Arrays.asList(DATO_DO, STATSBORGER), miljoe, gyldigIdent);
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            } catch (ManglendeInfoITpsException e) {
                log.warn(e.getMessage());
            }

            return status != null && (status.get(DATO_DO) == null || status.get(DATO_DO).isEmpty());
        }).collect(Collectors.toList());

        if (collected.size() < henteAntall) {
            log.info("Antall ønskede identer å hente er større enn myndige identer i avspillergruppe. - HenteAntall:{} MyndigeIdenter:{}", henteAntall, collected.size());
            henteAntall = collected.size();
        }

        List<String> hentedeIdenter = new ArrayList<>(henteAntall);

        for (int i = 0; i < henteAntall; i++) {
            int index = rand.nextInt(collected.size());
            hentedeIdenter.add(collected.get(index));
            collected.remove(index);
        }

        return hentedeIdenter;
    }

    public Map<String, String> hentFnrMedNavKontor(String miljoe, List<String> identer) {
        Map<String, String> fnrMedNavKontor = new HashMap<>();

        int antallFeilet = 0;

        for (String ident : identer) {
            Map<String, String> feltMedStatusQuo;

            try {
                feltMedStatusQuo = tpsStatusQuoService.hentStatusQuo(ROUTINE_KERNINFO, Collections.singletonList(NAV_ENHET), miljoe, ident);
                String statusQuo = feltMedStatusQuo.get(NAV_ENHET);
                if (!statusQuo.isEmpty()) {
                    fnrMedNavKontor.put(ident, statusQuo);
                } else {
                    antallFeilet++;
                    log.warn("Person med fnr {} hadde ingen tilknytning til NAV-enhet.", ident);
                }
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
                antallFeilet++;
            } catch (ManglendeInfoITpsException e) {
                log.warn(e.getMessage());
                antallFeilet++;
            }
        }

        if (antallFeilet > 0) {
            log.warn("Kunne ikke finne NAV-enhet for {} av identene.", antallFeilet);
        }

        return fnrMedNavKontor;
    }

    public Map<String, JsonNode> hentGittAntallIdenterMedStatusQuo(Long avspillergruppeId, String miljoe, int antallIdenter) {
        List<String> alleIdenter = finnAlleIdenter(avspillergruppeId);
        if (alleIdenter.size() < antallIdenter) {
            log.info("Antall ønskede identer å hente er større enn tilgjengelige identer i avspillergruppe. - HenteAntall:{} TilgjengeligeIdenter:{}", antallIdenter, alleIdenter.size());
            antallIdenter = alleIdenter.size();
        }

        Map<String, JsonNode> utvalgteIdenterMedStatusQuo = new HashMap<>(antallIdenter);
        for (int i = 0; i < antallIdenter; i++) {
            String tilfeldigIdent = alleIdenter.remove(rand.nextInt(alleIdenter.size()));
            try {
                tpsStatusQuoService.resetCache();
                utvalgteIdenterMedStatusQuo.put(tilfeldigIdent, tpsStatusQuoService.getInfoOnRoutineName(ROUTINE_KERNINFO, AKSJONSKODE, miljoe, tilfeldigIdent));
            } catch (IOException e) {
                log.error("Kunne ikke hente status quo på ident {} - ", tilfeldigIdent, e);
            }
        }

        return utvalgteIdenterMedStatusQuo;
    }

    public Map<String, JsonNode> hentAdressePaaIdenter(String miljoe, List<String> identer) {
        Map<String, JsonNode> utvalgteIdenterMedStatusQuo = new HashMap<>(identer.size());

        ObjectNode navnOgAdresse;
        ObjectMapper mapper = new ObjectMapper();
        for (String ident : identer) {
            tpsStatusQuoService.resetCache();
            navnOgAdresse = mapper.createObjectNode();
            try {
                JsonNode infoOnRoutineName = tpsStatusQuoService.getInfoOnRoutineName(ROUTINE_KERNINFO, AKSJONSKODE, miljoe, ident);

                navnOgAdresse.set("personnavn", infoOnRoutineName.findValue("personnavn"));
                navnOgAdresse.set("bostedsAdresse", infoOnRoutineName.findValue("bostedsAdresse"));

                utvalgteIdenterMedStatusQuo.put(ident, navnOgAdresse);
            } catch (IOException e) {
                log.error("Kunne ikke hente status quo på ident {} - ", ident, e);
            }
        }
        return utvalgteIdenterMedStatusQuo;
    }

    public List<String> finnAlleIdenterOverAlder(Long avspillergruppeId, int minimumAlder) {
        List<String> identer = finnLevendeIdenter(avspillergruppeId);
        return identer.stream().filter(ident -> getFoedselsdatoFraFnr(ident).isBefore(LocalDate.now().minusYears(minimumAlder))).collect(Collectors.toList());
    }

    public List<String> finnAlleIdenterIAldersgruppe(Long avspillergruppeId, int minimumAlder, int maksimumAlder) {
        List<String> identer = finnLevendeIdenter(avspillergruppeId);
        List<String> identerOverAlder = identer.stream().filter(ident -> getFoedselsdatoFraFnr(ident).isBefore(LocalDate.now().minusYears(minimumAlder))).collect(Collectors.toList());
        return identerOverAlder.stream().filter(ident -> getFoedselsdatoFraFnr(ident).isAfter(LocalDate.now().minusYears(maksimumAlder))).collect(Collectors.toList());
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

    public List<String> finnFoedteIdenter(Long gruppeId) {
        return new ArrayList<>(tpsfConsumer.getIdenterFiltrertPaaAarsakskode(
                gruppeId, Collections.singletonList(FOEDSELSMELDING.getAarsakskode()),
                TRANSAKSJONSTYPE
        ));
    }

    public RelasjonsResponse hentRelasjoner(String ident, String miljoe) {
        RelasjonsResponse relasjonsResponse = null;
        try {
            tpsStatusQuoService.resetCache();
            JsonNode statusQuoTilIdent = tpsStatusQuoService.getInfoOnRoutineName(ROUTINE_PERSRELA, AKSJONSKODE, miljoe, ident);
            int antallRelasjoner = statusQuoTilIdent.findValue("antallRelasjoner").asInt();

            if (antallRelasjoner == 1) {
                relasjonsResponse = RelasjonsResponse.builder()
                        .fnr(statusQuoTilIdent.findValue("fnr").asText())
                        .relasjoner(Collections.singletonList(parseRelasjonNode(statusQuoTilIdent.findValue("relasjon")))).build();
            } else if (antallRelasjoner > 1) {
                ArrayNode relasjonNode = (ArrayNode) statusQuoTilIdent.findValue("relasjon");
                List<Relasjon> relasjoner = new ArrayList<>(relasjonNode.size());
                for (JsonNode relasjonselement : relasjonNode) {
                    relasjoner.add(parseRelasjonNode(relasjonselement));
                }
                relasjonsResponse = RelasjonsResponse.builder().fnr(statusQuoTilIdent.findValue("fnr").asText()).relasjoner(relasjoner).build();
            } else {
                relasjonsResponse = RelasjonsResponse.builder().fnr(statusQuoTilIdent.findValue("fnr").asText()).relasjoner(new ArrayList<>()).build();
            }
        } catch (IOException e) {
            log.error("Kunne ikke hente status quo på ident {} - ", ident, e);
        }
        return relasjonsResponse;
    }

    public List<Long> slettIdenterFraAvspillergruppe(Long avspillergruppeId, List<String> identer) {
        List<Long> meldingIderTilhoerendeIdenter = tpsfConsumer.getMeldingIderTilhoerendeIdenter(avspillergruppeId, identer);
        ResponseEntity tpsfResponse = tpsfConsumer.slettMeldingerFraTpsf(meldingIderTilhoerendeIdenter);
        if (tpsfResponse.getStatusCode().is2xxSuccessful()) {
            return meldingIderTilhoerendeIdenter;
        } else {
            return new ArrayList<>();
        }
    }

    private Relasjon parseRelasjonNode(JsonNode relasjonNode) {
        return Relasjon.builder()
                .kortnavn(relasjonNode.findValue("kortnavn").asText())
                .datoDo(relasjonNode.findValue("datoDo").asText())
                .typeRelBeskr(relasjonNode.findValue("typeRelBeskr").asText())
                .mellomnavn(relasjonNode.findValue("mellomnavn").asText())
                .etternavn(relasjonNode.findValue("etternavn").asText())
                .adresseStatus(relasjonNode.findValue("adresseStatus").asInt())
                .adrStatusBeskr(relasjonNode.findValue("adrStatusBeskr").asText())
                .spesregType(relasjonNode.findValue("spesregType").asText())
                .fornavn(relasjonNode.findValue("fornavn").asText())
                .fnrRelasjon(relasjonNode.findValue("fnrRelasjon").asText())
                .typeRelasjon(relasjonNode.findValue("typeRelasjon").asText())
                .build();
    }
}
