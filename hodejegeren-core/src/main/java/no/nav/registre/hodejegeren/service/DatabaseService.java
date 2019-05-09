package no.nav.registre.hodejegeren.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

import no.nav.registre.hodejegeren.mongodb.Data;
import no.nav.registre.hodejegeren.mongodb.Kilde;
import no.nav.registre.hodejegeren.mongodb.SyntHistorikk;
import no.nav.registre.hodejegeren.mongodb.SyntHistorikkRepository;

@Service
public class DatabaseService {

    private final SyntHistorikkRepository syntHistorikkRepository;

    public DatabaseService(SyntHistorikkRepository syntHistorikkRepository) {
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

    public void leggTilHistorikkPaaIdent(String id, SyntHistorikk syntHistorikk) {
        List<Kilde> kilder = syntHistorikk.getKilder();
        for (Kilde kilde : kilder) {
            String navnPaaKilde = kilde.getNavnPaaKilde();

            List<Kilde> eksisterendeKilder = hentHistorikkMedId(id).getKilder();

            // finn en annen måte å gjøre dette på:
            for (Kilde eksisterendeKilde : eksisterendeKilder) {
                if (eksisterendeKilde.getNavnPaaKilde().equals(navnPaaKilde)) {
                    eksisterendeKilde.getData().addAll(kilde.getData());
                    break;
                }
            }

        }
    }
}
