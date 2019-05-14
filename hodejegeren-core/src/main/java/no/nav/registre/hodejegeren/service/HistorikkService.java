package no.nav.registre.hodejegeren.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.hodejegeren.mongodb.Data;
import no.nav.registre.hodejegeren.mongodb.SyntHistorikk;
import no.nav.registre.hodejegeren.mongodb.SyntHistorikkRepository;
import no.nav.registre.hodejegeren.provider.rs.requests.DataRequest;
import no.nav.registre.hodejegeren.provider.rs.requests.HistorikkRequest;

@RequiredArgsConstructor
@Service
public class HistorikkService {

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

    //    public List<String> leggTilHistorikkPaaIdent(List<HistorikkRequest> historikkRequests) {
    //        List<String> opprettedeIder = new ArrayList<>();
    //        for (HistorikkRequest historikkRequest : historikkRequests) {
    //            String id = historikkRequest.getId();
    //            String navnPaaNyKilde = historikkRequest.getIdentMedData().getNavn();
    //            List<Data> nyData = new ArrayList<>(historikkRequest.getIdentMedData().getData().size());
    //
    //            LocalDateTime opprettelsesTidspunkt = LocalDateTime.now();
    //            for (Object o : historikkRequest.getIdentMedData().getData()) {
    //                nyData.add(Data.builder()
    //                        .innhold(o)
    //                        .datoOpprettet(opprettelsesTidspunkt)
    //                        .datoEndret(opprettelsesTidspunkt)
    //                        .build());
    //            }
    //
    //            SyntHistorikk eksisterendeHistorikk = hentHistorikkMedId(id);
    //            if (eksisterendeHistorikk != null) {
    //                Map<String, List<Data>> eksisterendeKilder = eksisterendeHistorikk.getKilder();
    //
    //                List<Data> eksisterendeData = eksisterendeKilder.get(navnPaaNyKilde);
    //                if (eksisterendeData != null) {
    //                    eksisterendeData.addAll(nyData);
    //                } else {
    //                    eksisterendeKilder.put(navnPaaNyKilde, nyData);
    //                }
    //
    //                opprettedeIder.add(syntHistorikkRepository.save(eksisterendeHistorikk).getId());
    //            } else {
    //                Map<String, List<Data>> nyKilde = new HashMap<>();
    //                nyKilde.put(navnPaaNyKilde, nyData);
    //                opprettedeIder.add(opprettHistorikk(SyntHistorikk.builder().id(id).kilder(nyKilde).build()).getId());
    //            }
    //        }
    //
    //        return opprettedeIder;
    //    }

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
