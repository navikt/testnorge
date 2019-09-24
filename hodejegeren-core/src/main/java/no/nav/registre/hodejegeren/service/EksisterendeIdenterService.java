package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.DATO_DO;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.NAV_ENHET;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.NAV_ENHET_BESKRIVELSE;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.STATSBORGER;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSMELDING;
import static no.nav.registre.hodejegeren.service.Endringskoder.FOEDSELSNUMMERKORREKSJON;
import static no.nav.registre.hodejegeren.service.Endringskoder.INNVANDRING;
import static no.nav.registre.hodejegeren.service.Endringskoder.TILDELING_DNUMMER;
import static no.nav.registre.hodejegeren.service.TpsStatusQuoService.AKSJONSKODE;
import static no.nav.registre.hodejegeren.service.utilities.IdentUtility.getFoedselsdatoFraFnr;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
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
import no.nav.registre.hodejegeren.provider.rs.responses.NavEnhetResponse;
import no.nav.registre.hodejegeren.provider.rs.responses.SlettIdenterResponse;
import no.nav.registre.hodejegeren.provider.rs.responses.persondata.PersondataResponse;
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

    public List<String> hentLevendeIdenterIGruppeOgSjekkStatusQuo(Long gruppeId, String miljoe, Integer henteAntall, Integer minimumAlder) {
        List<String> gyldigeIdenter = finnAlleIdenterOverAlder(gruppeId, minimumAlder);
        List<String> utvalgteIdenter;

        if (henteAntall != null) {
            Collections.shuffle(gyldigeIdenter);
            if (gyldigeIdenter.size() < henteAntall) {
                log.info("Antall ønskede identer å hente er større enn identer over alder i avspillergruppe. - HenteAntall:{} GyldigeIdenter:{}", henteAntall, gyldigeIdenter.size());
                henteAntall = gyldigeIdenter.size();
            }
            utvalgteIdenter = gyldigeIdenter.subList(0, henteAntall);
        } else {
            utvalgteIdenter = new ArrayList<>(gyldigeIdenter);
        }

        if (utvalgteIdenter.isEmpty()) {
            return Collections.emptyList();
        }

        return utvalgteIdenter.parallelStream().filter(gyldigIdent -> {
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
    }

    public List<NavEnhetResponse> hentFnrMedNavKontor(String miljoe, List<String> identer) {
        List<NavEnhetResponse> navEnhetResponseListe = new ArrayList<>(identer.size());

        int antallFeilet = 0;

        for (String ident : identer) {
            try {
                Map<String, String> feltMedStatusQuo = tpsStatusQuoService.hentStatusQuo(ROUTINE_KERNINFO, Arrays.asList(NAV_ENHET, NAV_ENHET_BESKRIVELSE), miljoe, ident);
                String navEnhet = feltMedStatusQuo.get(NAV_ENHET);
                String navEnhetBeskrivelse = feltMedStatusQuo.get(NAV_ENHET_BESKRIVELSE);
                if (!navEnhet.isEmpty() && !navEnhetBeskrivelse.isEmpty()) {
                    navEnhetResponseListe.add(
                            NavEnhetResponse.builder()
                                    .ident(ident)
                                    .navEnhet(navEnhet)
                                    .navEnhetBeskrivelse(navEnhetBeskrivelse)
                                    .build());
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

        return navEnhetResponseListe;
    }

    public Map<String, JsonNode> hentGittAntallIdenterMedStatusQuo(Long avspillergruppeId, String miljoe, int antallIdenter, int minimumAlder, int maksimumAlder) {
        List<String> alleIdenter = finnLevendeIdenterIAldersgruppe(avspillergruppeId, minimumAlder, maksimumAlder);
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

    public List<String> finnLevendeIdenterIAldersgruppe(Long avspillergruppeId, int minimumAlder, int maksimumAlder) {
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

    public PersondataResponse hentPersondata(String ident, String miljoe) {
        try {
            tpsStatusQuoService.resetCache();
            JsonNode statusQuoTilIdent = tpsStatusQuoService.getInfoOnRoutineName(ROUTINE_PERSDATA, AKSJONSKODE, miljoe, ident);
            return PersondataResponse.builder()
                    .fnr(statusQuoTilIdent.findValue("fnr").asText())
                    .kortnavn(statusQuoTilIdent.findValue("kortnavn").asText())
                    .fornavn(statusQuoTilIdent.findValue("fornavn").asText())
                    .mellomnavn(statusQuoTilIdent.findValue("mellomnavn").asText())
                    .etternavn(statusQuoTilIdent.findValue("etternavn").asText())
                    .kodeStatsborger(statusQuoTilIdent.findValue("kodeStatsborger").asText())
                    .statsborger(statusQuoTilIdent.findValue("statsborger").asText())
                    .datoStatsborger(statusQuoTilIdent.findValue("datoStatsborger").asText())
                    .kodeSivilstand(statusQuoTilIdent.findValue("kodeSivilstand").asText())
                    .sivilstand(statusQuoTilIdent.findValue("sivilstand").asText())
                    .datoSivilstand(statusQuoTilIdent.findValue("datoSivilstand").asText())
                    .kodeInnvandretFra(statusQuoTilIdent.findValue("kodeInnvandretFra").asText())
                    .innvandretFra(statusQuoTilIdent.findValue("innvandretFra").asText())
                    .datoInnvandret(statusQuoTilIdent.findValue("datoInnvandret").asText())
                    .build();
        } catch (IOException e) {
            log.error("Kunne ikke hente status quo på ident {} - ", ident, e);
            throw new RuntimeException("Kunne ikke hente status quo på ident " + ident, e);
        }
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

    public SlettIdenterResponse slettIdenterUtenStatusQuo(Long avspillergruppeId, String miljoe, List<String> identer) throws IOException {
        SlettIdenterResponse slettIdenterResponse = SlettIdenterResponse.builder()
                .identerSomIkkeKunneBliSlettet(new ArrayList<>())
                .identerMedGyldigStatusQuo(new ArrayList<>())
                .build();

        List<String> identerSomSkalSlettes = new ArrayList<>();

        for (String ident : identer) {
            try {
                tpsStatusQuoService.hentStatusQuo(ROUTINE_PERSDATA, Arrays.asList(DATO_DO, STATSBORGER), miljoe, ident);
                slettIdenterResponse.getIdenterMedGyldigStatusQuo().add(ident);
            } catch (ManglendeInfoITpsException e) {
                if (e.getMessage().contains("Utfyllende melding fra TPS: PERSON IKKE FUNNET")) {
                    identerSomSkalSlettes.add(ident);
                } else {
                    slettIdenterResponse.getIdenterSomIkkeKunneBliSlettet().add(ident);
                }
            }
        }

        List<Long> meldingIderTilhoerendeIdent = tpsfConsumer.getMeldingIderTilhoerendeIdenter(avspillergruppeId, identerSomSkalSlettes);
        ResponseEntity response = tpsfConsumer.slettMeldingerFraTpsf(meldingIderTilhoerendeIdent);

        if (response.getStatusCode().is2xxSuccessful()) {
            slettIdenterResponse.setIdenterSomBleSlettetFraAvspillergruppe(identerSomSkalSlettes);
        }

        return slettIdenterResponse;
    }

    public List<String> hentIdenterSomIkkeErITps(Long avspillergruppeId, String miljoe) {
        List<String> alleIdenter = finnAlleIdenter(avspillergruppeId);
        List<String> identerIkkeITps = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for (List<String> identer : Lists.partition(alleIdenter, 80)) {
            try {
                JsonNode jsonNode = tpsfConsumer.hentTpsStatusPaaIdenter(miljoe, identer);
                if (!"00".equals(jsonNode.findValue("status").findValue("kode").asText())) {
                    JsonNode statusFromTps = jsonNode.findValue("EFnr");
                    List<Map<String, Object>> identStatus = objectMapper.convertValue(statusFromTps, new TypeReference<List<Map<String, Object>>>() {
                    });
                    for (Map<String, Object> map : identStatus) {
                        if (map.containsKey("svarStatus")) {
                            Map<String, String> svarStatus = objectMapper.convertValue(map.get("svarStatus"), new TypeReference<Map<String, String>>() {
                            });
                            if ("08".equals(svarStatus.get("returStatus"))) {
                                identerIkkeITps.add(String.valueOf(map.get("fnr")));
                            }
                        }
                    }
                }
            } catch (IOException e) {
                log.error("Kunne ikke hente status fra TPS", e);
            }
        }
        return identerIkkeITps;
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
