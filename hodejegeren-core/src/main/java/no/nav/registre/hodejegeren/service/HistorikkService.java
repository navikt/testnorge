package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.service.EksisterendeIdenterService.ROUTINE_KERNINFO;
import static no.nav.registre.hodejegeren.service.EksisterendeIdenterService.ROUTINE_PERSRELA;
import static no.nav.registre.hodejegeren.service.TpsStatusQuoService.AKSJONSKODE;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.hodejegeren.TpsPersonDokument;
import no.nav.registre.hodejegeren.mongodb.Data;
import no.nav.registre.hodejegeren.mongodb.SyntHistorikk;
import no.nav.registre.hodejegeren.mongodb.SyntHistorikkRepository;
import no.nav.registre.hodejegeren.mongodb.tpsStatusQuo.Kjerneinfo.Kjerneinformasjon;
import no.nav.registre.hodejegeren.mongodb.tpsStatusQuo.Personrelasjon.Relasjonsinformasjon;
import no.nav.registre.hodejegeren.mongodb.tpsStatusQuo.StatusQuo;
import no.nav.registre.hodejegeren.provider.rs.requests.DataRequest;
import no.nav.registre.hodejegeren.provider.rs.requests.HistorikkRequest;

@Slf4j
@RequiredArgsConstructor
@Service
public class HistorikkService {

    @Autowired
    private final TpsStatusQuoService tpsStatusQuoService;

    private final SyntHistorikkRepository syntHistorikkRepository;

    public List<SyntHistorikk> hentAllHistorikk() {
        return syntHistorikkRepository.findAll();
    }

    public SyntHistorikk hentHistorikkMedId(String id) {
        return syntHistorikkRepository.findById(id).orElse(null);
    }

    public SyntHistorikk opprettHistorikk(SyntHistorikk syntHistorikk) {
        Map<String, List<Data>> kilder = syntHistorikk.getKilder();
        for (List<Data> data : kilder.values()) {
            for (Data d : data) {
                if (d.getDatoOpprettet() == null) {
                    d.setDatoOpprettet(LocalDateTime.now());
                }
                if (d.getDatoEndret() == null) {
                    d.setDatoEndret(d.getDatoOpprettet());
                }
            }
        }
        return syntHistorikkRepository.save(syntHistorikk);
    }

    public List<String> leggTilHistorikkPaaIdent(HistorikkRequest historikkRequest) {
        List<String> opprettedeIder = new ArrayList<>();
        List<DataRequest> kilder = historikkRequest.getIdentMedData();

        for (DataRequest kilde : kilder) {
            String id = kilde.getId();
            List<Data> nyData = new ArrayList<>(kilde.getData().size());

            LocalDateTime opprettelsesTidspunkt = LocalDateTime.now();
            for (Object o : kilde.getData()) {
                nyData.add(Data.builder()
                        .innhold(o)
                        .datoOpprettet(opprettelsesTidspunkt)
                        .datoEndret(opprettelsesTidspunkt)
                        .build());
            }
            SyntHistorikk eksisterendeHistorikk = hentHistorikkMedId(id);
            if (eksisterendeHistorikk != null) {
                Map<String, List<Data>> eksisterendeKilder = eksisterendeHistorikk.getKilder();

                List<Data> eksisterendeData = eksisterendeKilder.get(historikkRequest.getKilde());
                if (eksisterendeData != null) {
                    eksisterendeData.addAll(nyData);
                } else {
                    eksisterendeKilder.put(historikkRequest.getKilde(), nyData);
                }

                opprettedeIder.add(syntHistorikkRepository.save(eksisterendeHistorikk).getId());
            } else {
                Map<String, List<Data>> nyKilde = new HashMap<>();
                nyKilde.put(historikkRequest.getKilde(), nyData);
                opprettedeIder.add(opprettHistorikk(SyntHistorikk.builder().id(id).kilder(nyKilde).build()).getId());
            }
        }
        return opprettedeIder;
    }

    public List<String> oppdaterSkdHistorikk(HistorikkRequest historikkRequest) {
        ObjectMapper mapper = new ObjectMapper();
        List<DataRequest> identMedData = historikkRequest.getIdentMedData();
        List<String> opprettedeIder = new ArrayList<>();
        for (DataRequest ident : identMedData) {
            SyntHistorikk syntHistorikk = hentHistorikkMedId(ident.getId());
            if (syntHistorikk != null) {
                Data eksisterendeSkdData = syntHistorikk.getKilder().get(historikkRequest.getKilde()).get(0);
                eksisterendeSkdData.setInnhold(mapper.convertValue(historikkRequest.getIdentMedData().get(0).getData().get(0), StatusQuo.class));
                eksisterendeSkdData.setDatoEndret(LocalDateTime.now());
                opprettedeIder.add(syntHistorikkRepository.save(syntHistorikk).getId());
            } else {
                StatusQuo statusQuo = mapper.convertValue(historikkRequest.getIdentMedData().get(0).getData().get(0), StatusQuo.class);
                historikkRequest.getIdentMedData().get(0).setData(Collections.singletonList(statusQuo));
                opprettedeIder.addAll(leggTilHistorikkPaaIdent(historikkRequest));
            }
        }
        return opprettedeIder;
    }

    public List<String> oppdaterTpsPersonDokument(String ident, TpsPersonDokument tpsPersonDokument) {
        SyntHistorikk syntHistorikk = hentHistorikkMedId(ident);
        if (syntHistorikk != null) {
            Data eksisterendeSkdData = syntHistorikk.getKilder().get("skd").get(0);
            eksisterendeSkdData.setInnhold(tpsPersonDokument);
            eksisterendeSkdData.setDatoEndret(LocalDateTime.now());
            return Collections.singletonList(syntHistorikkRepository.save(syntHistorikk).getId());
        } else {
            HistorikkRequest historikkRequest = HistorikkRequest.builder()
                    .kilde("skd")
                    .identMedData(Collections.singletonList(DataRequest.builder()
                            .id(ident)
                            .data(Collections.singletonList(tpsPersonDokument))
                            .build()))
                    .build();
            return leggTilHistorikkPaaIdent(historikkRequest);
        }
    }

    public List<String> oppdaterSkdStatusPaaIdenter(List<String> identer, String miljoe) {
        ObjectMapper mapper = new ObjectMapper();
        List<String> oppdaterteIdenter = new ArrayList<>();
        StatusQuo statusQuo;
        for (String ident : identer) {
            JsonNode kerninfo = null;
            JsonNode persrela = null;
            try {
                kerninfo = tpsStatusQuoService.getInfoOnRoutineName(ROUTINE_KERNINFO, AKSJONSKODE, miljoe, ident).findValue("data1");
                persrela = tpsStatusQuoService.getInfoOnRoutineName(ROUTINE_PERSRELA, AKSJONSKODE, miljoe, ident).findValue("data1");
                tpsStatusQuoService.resetCache();
            } catch (IOException e) {
                log.error("Kunne ikke lese status quo på ident {} i miljø {}.", ident.replaceAll("[\r\n]", ""), miljoe.replaceAll("[\r\n]", ""), e);
            }

            if (kerninfo != null && persrela != null) {
                try {
                    statusQuo = StatusQuo.builder()
                            .kjerneinformasjon(mapper
                                    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                                    .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                                    .convertValue(kerninfo, Kjerneinformasjon.class))
                            .relasjonsinformasjon(mapper
                                    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                                    .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                                    .convertValue(persrela, Relasjonsinformasjon.class))
                            .build();

                    HistorikkRequest historikkRequest = HistorikkRequest.builder()
                            .kilde("skd")
                            .identMedData(Collections.singletonList(DataRequest.builder()
                                    .id(ident)
                                    .data(Collections.singletonList(statusQuo))
                                    .build()))
                            .build();
                    oppdaterteIdenter.addAll(oppdaterSkdHistorikk(historikkRequest));
                } catch (IllegalArgumentException e) {
                    log.error("Kunne ikke oppdatere skd til ident {}", ident, e);
                }
            }
        }
        return oppdaterteIdenter;
    }

    public ResponseEntity slettHistorikk(String id) {
        SyntHistorikk historikk = syntHistorikkRepository.findById(id).orElse(null);
        if (historikk != null) {
            syntHistorikkRepository.deleteById(id);
            return ResponseEntity.ok(String.format("Historikk tilhørende id '%s' ble slettet", id));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Fant ingen historikk tilhørende id '%s'", id));
        }
    }

    public ResponseEntity slettKilde(String id, String navnPaaKilde) {
        SyntHistorikk historikk = syntHistorikkRepository.findById(id).orElse(null);

        if (historikk != null) {
            Map<String, List<Data>> eksisterendeKilder = historikk.getKilder();
            if (eksisterendeKilder.containsKey(navnPaaKilde)) {
                eksisterendeKilder.remove(navnPaaKilde);
                if (historikk.getKilder().isEmpty()) {
                    slettHistorikk(id);
                } else {
                    syntHistorikkRepository.save(historikk);
                }
                return ResponseEntity.ok(String.format("Kilde '%s' tilhørende id '%s' ble slettet", navnPaaKilde, id));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Fant ingen identMedData med navn '%s' tilhørende id '%s'", navnPaaKilde, id));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Fant ingen historikk tilhørende id '%s'", id));
        }
    }
}
