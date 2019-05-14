package no.nav.registre.hodejegeren.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.registre.hodejegeren.mongodb.Data;
import no.nav.registre.hodejegeren.mongodb.Kilde;
import no.nav.registre.hodejegeren.mongodb.SyntHistorikk;
import no.nav.registre.hodejegeren.mongodb.SyntHistorikkRepository;
import no.nav.registre.hodejegeren.mongodb.requests.HistorikkRequest;

@Service
public class HistorikkService {

    private final SyntHistorikkRepository syntHistorikkRepository;

    public HistorikkService(SyntHistorikkRepository syntHistorikkRepository) {
        this.syntHistorikkRepository = syntHistorikkRepository;
    }

    public List<SyntHistorikk> hentAllHistorikk() {
        return syntHistorikkRepository.findAll();
    }

    public SyntHistorikk hentHistorikkMedId(String id) {
        return syntHistorikkRepository.findById(id).orElse(null);
    }

    public SyntHistorikk opprettHistorikk(@RequestBody SyntHistorikk syntHistorikk) {
        List<Kilde> kilder = syntHistorikk.getKilder();
        for (Kilde kilde : kilder) {
            List<Data> data = kilde.getData();
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

    public List<String> leggTilHistorikkPaaIdent(List<HistorikkRequest> historikkRequests) {
        List<String> opprettedeIder = new ArrayList<>();
        for (HistorikkRequest historikkRequest : historikkRequests) {
            String id = historikkRequest.getId();
            Kilde kilde = Kilde.builder()
                    .navn(historikkRequest.getKilde().getNavn())
                    .data(new ArrayList<>())
                    .build();

            for (Object o : historikkRequest.getKilde().getData()) {
                kilde.getData().add(Data.builder()
                        .innhold(o)
                        .datoOpprettet(LocalDateTime.now())
                        .datoEndret(LocalDateTime.now())
                        .build());
            }

            SyntHistorikk eksisterendeHistorikk = hentHistorikkMedId(id);
            if (eksisterendeHistorikk != null) {
                List<Kilde> eksisterendeKilder = eksisterendeHistorikk.getKilder();

                boolean kildeEksisterer = false;
                for (Kilde eksisterendeKilde : eksisterendeKilder) {
                    if (eksisterendeKilde.getNavn().equals(kilde.getNavn())) {
                        eksisterendeKilde.getData().addAll(kilde.getData());
                        kildeEksisterer = true;
                        break;
                    }
                }

                if (!kildeEksisterer) {
                    eksisterendeHistorikk.getKilder().add(kilde);
                }

                opprettedeIder.add(syntHistorikkRepository.save(eksisterendeHistorikk).getId());
            } else {
                opprettedeIder.add(opprettHistorikk(SyntHistorikk.builder().id(id).kilder(Collections.singletonList(kilde)).build()).getId());
            }
        }

        return opprettedeIder;
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
            Kilde kildeSomSkalSlettes = null;

            for (Kilde kilde : kilder) {
                if (kilde.getNavn().equals(navnPaaKilde)) {
                    kildeSomSkalSlettes = kilde;
                }
            }

            if (kildeSomSkalSlettes != null) {
                kilder.remove(kildeSomSkalSlettes);

                if (historikk.getKilder().isEmpty()) {
                    slettHistorikk(id);
                } else {
                    syntHistorikkRepository.save(historikk);
                }

                return ResponseEntity.ok(String.format("Kilde '%s' tilhørende id '%s' ble slettet", navnPaaKilde, id));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Fant ingen kilde med navn '%s' tilhørende id '%s'", navnPaaKilde, id));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Fant ingen historikk tilhørende id '%s'", id));
        }
    }
}
