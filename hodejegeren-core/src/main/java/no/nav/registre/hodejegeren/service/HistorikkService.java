package no.nav.registre.hodejegeren.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.registre.hodejegeren.TpsPersonDokument;
import no.nav.registre.hodejegeren.mongodb.Data;
import no.nav.registre.hodejegeren.mongodb.Kilde;
import no.nav.registre.hodejegeren.mongodb.SyntHistorikk;
import no.nav.registre.hodejegeren.mongodb.SyntHistorikkRepository;
import no.nav.registre.hodejegeren.provider.rs.requests.DataRequest;
import no.nav.registre.hodejegeren.provider.rs.requests.HistorikkRequest;

@Slf4j
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

    public List<SyntHistorikk> hentHistorikkMedKilde(String kilde) {
        return syntHistorikkRepository.findAllByKildenavn(kilde);
    }

    public List<String> hentIdsMedKilde(String kilde) {
        List<String> ids = new ArrayList<>();
        List<SyntHistorikk> historikkByKildenavn = syntHistorikkRepository.findAllIdsByKildenavn(kilde);
        for (SyntHistorikk historikk : historikkByKildenavn) {
            ids.add(historikk.getId());
        }
        return ids;
    }

    public SyntHistorikk opprettHistorikk(SyntHistorikk syntHistorikk) {
        List<Kilde> kilder = syntHistorikk.getKilder();
        for (Kilde kilde : kilder) {
            for (Data d : kilde.getData()) {
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
                List<Kilde> eksisterendeKilder = eksisterendeHistorikk.getKilder();
                List<Data> eksisterendeData = null;
                for (Kilde k : eksisterendeKilder) {
                    if (historikkRequest.getKilde().equals(k.getNavn())) {
                        eksisterendeData = k.getData();
                    }
                }

                if (eksisterendeData != null) {
                    eksisterendeData.addAll(nyData);
                } else {
                    eksisterendeKilder.add(Kilde.builder().navn(historikkRequest.getKilde()).data(nyData).build());
                }

                opprettedeIder.add(syntHistorikkRepository.save(eksisterendeHistorikk).getId());
            } else {
                Kilde nyKilde = Kilde.builder().navn(historikkRequest.getKilde()).data(nyData).build();
                opprettedeIder.add(opprettHistorikk(SyntHistorikk.builder().id(id).kilder(Collections.singletonList(nyKilde)).build()).getId());
            }
        }
        return opprettedeIder;
    }

    public List<String> oppdaterTpsPersonDokument(String ident, TpsPersonDokument tpsPersonDokument) {
        SyntHistorikk syntHistorikk = hentHistorikkMedId(ident);
        if (syntHistorikk != null) {
            Kilde kilde = null;
            for (Kilde k : syntHistorikk.getKilder()) {
                if ("skd".equals(k.getNavn())) {
                    kilde = k;
                }
            }
            if (kilde != null) {
                Data eksisterendeSkdData = kilde.getData().get(0);
                eksisterendeSkdData.setInnhold(tpsPersonDokument);
                eksisterendeSkdData.setDatoEndret(LocalDateTime.now());
                return Collections.singletonList(syntHistorikkRepository.save(syntHistorikk).getId());
            } else {
                return leggTilSkdData(ident, tpsPersonDokument);
            }
        } else {
            return leggTilSkdData(ident, tpsPersonDokument);
        }
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
            List<Kilde> kilder = historikk.getKilder();
            Kilde kilde = null;
            for (Kilde k : kilder) {
                if (navnPaaKilde.equals(k.getNavn())) {
                    kilde = k;
                }
            }
            if (kilde != null) {
                kilder.remove(kilde);
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

    private List<String> leggTilSkdData(String ident, TpsPersonDokument tpsPersonDokument) {
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
