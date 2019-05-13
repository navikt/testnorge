package no.nav.registre.hodejegeren.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.hodejegeren.mongodb.SyntHistorikk;
import no.nav.registre.hodejegeren.mongodb.requests.HistorikkRequest;
import no.nav.registre.hodejegeren.service.HistorikkService;

@RestController
@RequestMapping("api/v1/historikk")
public class HistorikkController {

    @Autowired
    private HistorikkService historikkService;

    @GetMapping(value = "hentAllHistorikk")
    public List<SyntHistorikk> hentAllHistorikk() {
        return historikkService.hentAllHistorikk();
    }

    @GetMapping(value = "hentHistorikkMedId/{id}")
    public SyntHistorikk hentHistorikkMedId(@PathVariable String id) {
        return historikkService.hentHistorikkMedId(id);
    }

    @PostMapping(value = "leggTil")
    public List<String> leggTilHistorikk(@RequestBody List<HistorikkRequest> historikkRequests) {
        return historikkService.leggTilHistorikkPaaIdent(historikkRequests);
    }

    @PostMapping(value = "opprettHistorikk")
    public SyntHistorikk opprettHistorikk(@RequestBody SyntHistorikk syntHistorikk) {
        return historikkService.opprettHistorikk(syntHistorikk);
    }

    @DeleteMapping(value = "slettHistorikk/{id}")
    public ResponseEntity slettHistorikk(@PathVariable String id) {
        return historikkService.slettHistorikk(id);
    }

    @DeleteMapping(value = "slettKilde/{id}")
    public ResponseEntity slettKilde(@PathVariable String id, @RequestParam String navnPaaKilde) {
        return historikkService.slettKilde(id, navnPaaKilde);
    }
}
