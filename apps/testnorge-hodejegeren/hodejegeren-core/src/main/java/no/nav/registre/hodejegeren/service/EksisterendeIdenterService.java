package no.nav.registre.hodejegeren.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
import no.nav.registre.hodejegeren.exception.IdentIOException;
import no.nav.registre.hodejegeren.exception.ManglendeInfoITpsException;
import no.nav.registre.hodejegeren.provider.rs.responses.NavEnhetResponse;
import no.nav.registre.hodejegeren.provider.rs.responses.kontoinfo.KontoinfoResponse;
import no.nav.registre.hodejegeren.provider.rs.responses.persondata.PersondataResponse;
import no.nav.registre.hodejegeren.provider.rs.responses.relasjon.Relasjon;
import no.nav.registre.hodejegeren.provider.rs.responses.relasjon.RelasjonsResponse;
import no.nav.registre.testnorge.libs.core.util.IdentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.DATO_DO;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.NAV_ENHET;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.NAV_ENHET_BESKRIVELSE;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.STATSBORGER;
import static no.nav.registre.hodejegeren.service.TpsStatusQuoService.AKSJONSKODE;

@Service
@Slf4j
public class EksisterendeIdenterService {

    private static final String STATUS_QUO_FEILMELDING = "Kunne ikke hente status quo på ident {} - ";
    private static final String BOSTEDSADRESSE = "bostedsAdresse";
    private static final String KORTNAVN = "kortnavn";
    private static final String FORNAVN = "fornavn";
    private static final String MELLOMNAVN = "mellomnavn";
    private static final String ETTERNAVN = "etternavn";
    private static final String SVARSTATUS = "svarStatus";
    private static final int TPSF_PAGE_SIZE = 80;
    private static final String ROUTINE_PERSDATA = "FS03-FDNUMMER-PERSDATA-O";
    private static final String ROUTINE_KERNINFO = "FS03-FDNUMMER-KERNINFO-O";
    private static final String ROUTINE_PERSRELA = "FS03-FDNUMMER-PERSRELA-O";
    private static final String AKSJONSKODE_A0 = "A0";
    private static final String AKSJONSKODE_A2 = "A2";
    public static final String TRANSAKSJONSTYPE = "1";

    @Autowired
    private TpsfConsumer tpsfConsumer;

    @Autowired
    private TpsStatusQuoService tpsStatusQuoService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private TpsfFiltreringService tpsfFiltreringService;

    @Autowired
    private Random rand;

    public List<String> hentLevendeIdenterIGruppeOgSjekkStatusQuo(
            Long gruppeId,
            String miljoe,
            Integer henteAntall,
            Integer minimumAlder
    ) {
        log.info("Henter levende identer med minimum alder {}...", minimumAlder);
        var gyldigeIdenter = finnAlleIdenterOverAlder(gruppeId, minimumAlder);
        log.info("{} levende identer hentet.", gyldigeIdenter.size());
        List<String> utvalgteIdenter;

        if (henteAntall != null) {
            Collections.shuffle(gyldigeIdenter);
            if (gyldigeIdenter.size() < henteAntall) {
                log.info("Antall ønskede identer å hente er større enn identer over alder i avspillergruppe. - HenteAntall:{} GyldigeIdenter:{}",
                        henteAntall.toString().replaceAll("[\r\n]", ""),
                        gyldigeIdenter.size());
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

    public List<NavEnhetResponse> hentFnrMedNavKontor(
            String miljoe,
            List<String> identer
    ) {
        List<NavEnhetResponse> navEnhetResponseListe = new ArrayList<>(identer.size());

        int antallFeilet = 0;

        for (String ident : identer) {
            try {
                var feltMedStatusQuo = tpsStatusQuoService.hentStatusQuo(ROUTINE_KERNINFO, Arrays.asList(NAV_ENHET, NAV_ENHET_BESKRIVELSE), miljoe, ident);
                var navEnhet = feltMedStatusQuo.get(NAV_ENHET);
                var navEnhetBeskrivelse = feltMedStatusQuo.get(NAV_ENHET_BESKRIVELSE);
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

    public Map<String, JsonNode> hentGittAntallIdenterMedStatusQuo(
            Long avspillergruppeId,
            String miljoe,
            int antallIdenter,
            int minimumAlder,
            int maksimumAlder
    ) {
        var alleIdenter = finnLevendeIdenterIAldersgruppe(avspillergruppeId, minimumAlder, maksimumAlder);
        if (alleIdenter.size() < antallIdenter) {
            log.info("Antall ønskede identer å hente er større enn tilgjengelige identer i avspillergruppe. - HenteAntall:{} TilgjengeligeIdenter:{}", antallIdenter, alleIdenter.size());
            antallIdenter = alleIdenter.size();
        }

        Map<String, JsonNode> utvalgteIdenterMedStatusQuo = new HashMap<>(antallIdenter);
        for (int i = 0; i < antallIdenter; i++) {
            var tilfeldigIdent = alleIdenter.remove(rand.nextInt(alleIdenter.size()));
            try {
                utvalgteIdenterMedStatusQuo.put(tilfeldigIdent, tpsStatusQuoService.getInfoOnRoutineName(ROUTINE_KERNINFO, AKSJONSKODE, miljoe, tilfeldigIdent));
            } catch (IOException e) {
                log.error(STATUS_QUO_FEILMELDING, tilfeldigIdent, e);
            }
        }

        return utvalgteIdenterMedStatusQuo;
    }

    public List<KontoinfoResponse> hentGittAntallIdenterMedKononummerinfo(
            Long avspillergruppeId,
            String miljoe,
            int antallIdenter,
            int minimumAlder,
            int maksimumAlder
    ) {
        var alleIdenter = finnLevendeIdenterIAldersgruppe(avspillergruppeId, minimumAlder, maksimumAlder);
        if (alleIdenter.size() < antallIdenter) {
            log.info("Antall ønskede identer å hente er større enn tilgjengelige identer i avspillergruppe. - HenteAntall:{} TilgjengeligeIdenter:{}", antallIdenter, alleIdenter.size());
            antallIdenter = alleIdenter.size();
        }

        List<KontoinfoResponse> identerMedKontoinformasjon = new ArrayList<>(antallIdenter);
        int i = 0;
        while (i < antallIdenter && !alleIdenter.isEmpty()) {
            String ident = alleIdenter.remove(rand.nextInt(alleIdenter.size()));
            try {
                var statusQuoTilIdent = tpsStatusQuoService.getInfoOnRoutineName(ROUTINE_KERNINFO, AKSJONSKODE, miljoe, ident);
                if (statusQuoTilIdent == null) {
                    continue;
                }
                var kontonummerNode = statusQuoTilIdent.findValue("kontoNummer");
                if (kontonummerNode == null) {
                    continue;
                }
                var kontonummer = kontonummerNode.asText();
                if (kontonummer.isEmpty()) {
                    continue;
                }
                JsonNode bostedsAdresse = statusQuoTilIdent.findValue(BOSTEDSADRESSE);
                identerMedKontoinformasjon.add(KontoinfoResponse.builder()
                        .fnr(statusQuoTilIdent.findValue("fodselsnummer").asText())
                        .kortnavn(statusQuoTilIdent.findValue(KORTNAVN).asText())
                        .fornavn(statusQuoTilIdent.findValue(FORNAVN).asText())
                        .mellomnavn(statusQuoTilIdent.findValue(MELLOMNAVN).asText())
                        .etternavn(statusQuoTilIdent.findValue(ETTERNAVN).asText())
                        .kontonummer(kontonummer)
                        .navn(statusQuoTilIdent.findValue("banknavn").asText())
                        .adresseLinje1(bostedsAdresse.findValue("adresse1").asText())
                        .adresseLinje2(bostedsAdresse.findValue("adresse2").asText())
                        .adresseLinje3("") // finnes ikke i rutine-kerninfo under bostedsadresse
                        .postnr(bostedsAdresse.findValue("postnr").asText())
                        .landkode(bostedsAdresse.findValue("landKode").asText())
                        .build());
                i++;
            } catch (IOException e) {
                log.error(STATUS_QUO_FEILMELDING, ident, e);
                throw new IdentIOException("Kunne ikke hente status quo på ident " + ident, e);
            }
        }
        return identerMedKontoinformasjon;
    }

    public Map<String, JsonNode> hentAdressePaaIdenter(
            String miljoe,
            List<String> identer
    ) {
        Map<String, JsonNode> utvalgteIdenterMedStatusQuo = new HashMap<>(identer.size());

        ObjectNode navnOgAdresse;
        ObjectMapper mapper = new ObjectMapper();
        for (var ident : identer) {
            navnOgAdresse = mapper.createObjectNode();
            try {
                var infoOnRoutineName = tpsStatusQuoService.getInfoOnRoutineName(ROUTINE_KERNINFO, AKSJONSKODE, miljoe, ident);

                navnOgAdresse.set("personnavn", infoOnRoutineName.findValue("personnavn"));
                navnOgAdresse.set(BOSTEDSADRESSE, infoOnRoutineName.findValue(BOSTEDSADRESSE));
                navnOgAdresse.set("NAVenhetDetalj", infoOnRoutineName.findValue("NAVenhetDetalj"));

                utvalgteIdenterMedStatusQuo.put(ident, navnOgAdresse);
            } catch (IOException e) {
                log.error(STATUS_QUO_FEILMELDING, ident, e);
            }
        }
        return utvalgteIdenterMedStatusQuo;
    }

    public List<String> finnAlleIdenterOverAlder(
            Long avspillergruppeId,
            int minimumAlder
    ) {
        var identer = cacheService.hentLevendeIdenterCache(avspillergruppeId);
        return identer.stream().filter(ident -> IdentUtil.getFoedselsdatoFraIdent(ident).isBefore(LocalDate.now().minusYears(minimumAlder))).collect(Collectors.toList());
    }

    public List<String> finnLevendeIdenterIAldersgruppe(
            Long avspillergruppeId,
            int minimumAlder,
            int maksimumAlder
    ) {
        var identer = cacheService.hentLevendeIdenterCache(avspillergruppeId);
        return filtrerIdenterIAldersgruppe(identer, minimumAlder, maksimumAlder);
    }

    public List<String> finnFoedteIdenter(
            Long avspillergruppeId,
            int minimumAlder,
            int maksimumAlder
    ) {
        var identer = cacheService.hentFoedteIdenterCache(avspillergruppeId);
        return filtrerIdenterIAldersgruppe(new ArrayList<>(identer), minimumAlder, maksimumAlder);
    }

    public PersondataResponse hentPersondata(
            String ident,
            String miljoe
    ) {
        try {
            var statusQuoTilIdent = tpsStatusQuoService.getInfoOnRoutineName(ROUTINE_PERSDATA, AKSJONSKODE, miljoe, ident);
            log.trace("Status Quo til identen: {}", statusQuoTilIdent);
            if (statusQuoTilIdent.toString().contains("PERSON IKKE FUNNET")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Fant ingen personer med ident: %s", ident));
            }
            return PersondataResponse.builder()
                    .fnr(statusQuoTilIdent.findValue("fnr").asText())
                    .kortnavn(statusQuoTilIdent.findValue(KORTNAVN).asText())
                    .fornavn(statusQuoTilIdent.findValue(FORNAVN).asText())
                    .mellomnavn(statusQuoTilIdent.findValue(MELLOMNAVN).asText())
                    .etternavn(statusQuoTilIdent.findValue(ETTERNAVN).asText())
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
            log.error(STATUS_QUO_FEILMELDING, ident, e);
            throw new IdentIOException("Kunne ikke hente status quo på ident " + ident, e);
        }
    }

    public RelasjonsResponse hentRelasjoner(
            String ident,
            String miljoe
    ) {
        RelasjonsResponse relasjonsResponse = null;
        try {
            var statusQuoTilIdent = tpsStatusQuoService.getInfoOnRoutineName(ROUTINE_PERSRELA, AKSJONSKODE, miljoe, ident);
            var antallRelasjoner = 0;
            JsonNode antallRelasjonerJsonNode = statusQuoTilIdent.findValue("antallRelasjoner");
            if (antallRelasjonerJsonNode != null) {
                antallRelasjoner = antallRelasjonerJsonNode.asInt();
            }

            if (antallRelasjoner == 1) {
                relasjonsResponse = RelasjonsResponse.builder()
                        .fnr(statusQuoTilIdent.findValue("fnr").asText())
                        .relasjoner(Collections.singletonList(parseRelasjonNode(statusQuoTilIdent.findValue("relasjon")))).build();
            } else if (antallRelasjoner > 1) {
                var relasjonNode = (ArrayNode) statusQuoTilIdent.findValue("relasjon");
                List<Relasjon> relasjoner = new ArrayList<>(relasjonNode.size());
                for (var relasjonselement : relasjonNode) {
                    relasjoner.add(parseRelasjonNode(relasjonselement));
                }
                relasjonsResponse = RelasjonsResponse.builder().fnr(ident).relasjoner(relasjoner).build();
            } else {
                relasjonsResponse = RelasjonsResponse.builder().fnr(ident).relasjoner(new ArrayList<>()).build();
            }
        } catch (IOException e) {
            log.error(STATUS_QUO_FEILMELDING, ident, e);
        }
        return relasjonsResponse;
    }

    public List<String> hentIdenterSomIkkeErITps(
            Long avspillergruppeId,
            String miljoe
    ) {
        var alleIdenter = tpsfFiltreringService.finnAlleIdenter(avspillergruppeId);
        List<String> identerIkkeITps = new ArrayList<>();
        var objectMapper = new ObjectMapper();
        for (var identer : Lists.partition(alleIdenter, TPSF_PAGE_SIZE)) {
            try {
                var jsonNode = tpsfConsumer.hentTpsStatusPaaIdenter(AKSJONSKODE_A0, miljoe, identer);
                var status = jsonNode.findValue("status");
                if (status != null) {
                    var kode = status.findValue("kode");
                    if (kode != null && !"00".equals(kode.asText())) {
                        var statusFromTps = jsonNode.findValue("EFnr");
                        List<Map<String, Object>> identStatus = objectMapper.convertValue(statusFromTps, new TypeReference<List<Map<String, Object>>>() {
                        });
                        identStatus.stream()
                                .filter(map -> map.containsKey(SVARSTATUS))
                                .forEach(map -> {
                                    Map<String, String> svarStatus = objectMapper.convertValue(map.get(SVARSTATUS), new TypeReference<Map<String, String>>() {
                                    });
                                    if ("08".equals(svarStatus.get("returStatus"))) {
                                        identerIkkeITps.add(String.valueOf(map.get("fnr")));
                                    }
                                });

                    }
                }
            } catch (IOException e) {
                log.error("Kunne ikke hente status fra TPS", e);
            }
        }
        return identerIkkeITps;
    }

    public List<String> hentIdenterSomKolliderer(
            Long avspillergruppeId
    ) {
        var alleIdenter = tpsfFiltreringService.finnAlleIdenter(avspillergruppeId);
        List<String> identerITps = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (var identer : Lists.partition(alleIdenter, TPSF_PAGE_SIZE)) {
            try {
                var jsonNode = tpsfConsumer.hentTpsStatusPaaIdenter(AKSJONSKODE_A2, "q2", identer);
                var statusFromTps = jsonNode.findValue("EFnr");
                if (statusFromTps != null) {
                    List<Map<String, Object>> identStatus = objectMapper.convertValue(statusFromTps, new TypeReference<List<Map<String, Object>>>() {
                    });
                    for (var map : identStatus) {
                        if (!map.containsKey(SVARSTATUS)) {
                            identerITps.add(String.valueOf(map.get("fnr")));
                        }
                    }
                } else {
                    log.warn("Kunne ikke ekstrahere status fra TPS.");
                }
            } catch (IOException e) {
                log.error("Kunne ikke hente status fra TPS", e);
            }
        }
        return identerITps;
    }

    private Relasjon parseRelasjonNode(
            JsonNode relasjonNode
    ) {
        return Relasjon.builder()
                .kortnavn(relasjonNode.findValue(KORTNAVN).asText())
                .datoDo(relasjonNode.findValue("datoDo").asText())
                .typeRelBeskr(relasjonNode.findValue("typeRelBeskr").asText())
                .mellomnavn(relasjonNode.findValue(MELLOMNAVN).asText())
                .etternavn(relasjonNode.findValue(ETTERNAVN).asText())
                .adresseStatus(relasjonNode.findValue("adresseStatus").asInt())
                .adrStatusBeskr(relasjonNode.findValue("adrStatusBeskr").asText())
                .spesregType(relasjonNode.findValue("spesregType").asText())
                .fornavn(relasjonNode.findValue(FORNAVN).asText())
                .fnrRelasjon(relasjonNode.findValue("fnrRelasjon").asText())
                .typeRelasjon(relasjonNode.findValue("typeRelasjon").asText())
                .build();
    }

    private static List<String> filtrerIdenterIAldersgruppe(
            List<String> identer,
            int minimumAlder,
            int maksimumAlder
    ) {
        var identerOverAlder = identer.stream().filter(ident -> IdentUtil.getFoedselsdatoFraIdent(ident).isBefore(LocalDate.now().minusYears(minimumAlder))).collect(Collectors.toList());
        return identerOverAlder.stream().filter(ident -> IdentUtil.getFoedselsdatoFraIdent(ident).isAfter(LocalDate.now().minusYears(maksimumAlder))).collect(Collectors.toList());
    }
}
