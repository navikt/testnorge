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
import no.nav.registre.hodejegeren.provider.rs.requests.HistorikkRequest;
import no.nav.registre.hodejegeren.service.HistorikkService;

@RestController
@RequestMapping("api/v1/historikk")
public class HistorikkController {

    @Autowired
    private HistorikkService historikkService;

    @GetMapping(value = "")
    public List<SyntHistorikk> hentAllHistorikk() {
        return historikkService.hentAllHistorikk();
    }

    @GetMapping(value = "{id}")
    public SyntHistorikk hentHistorikkMedId(@PathVariable String id) {
        return historikkService.hentHistorikkMedId(id);
    }

    @PostMapping(value = "")
    public List<String> leggTilHistorikk(@RequestBody HistorikkRequest historikkRequest) {
        if ("skd".equals(historikkRequest.getKilde())) {
            return historikkService.oppdaterSkdHistorikk(historikkRequest);
        } else {
            return historikkService.leggTilHistorikkPaaIdent(historikkRequest);
        }
    }

    @PostMapping(value = "skd/oppdaterStatus")
    public List<String> oppdaterSkdStatus(@RequestParam String miljoe, @RequestBody List<String> identer) {
        return historikkService.oppdaterSkdStatusPaaIdenter(identer, miljoe);
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity slettHistorikk(@PathVariable String id) {
        return historikkService.slettHistorikk(id);
    }

    @DeleteMapping(value = "kilde/{id}")
    public ResponseEntity slettKilde(@PathVariable String id, @RequestParam String navnPaaKilde) {
        return historikkService.slettKilde(id, navnPaaKilde);
    }
}
