package no.nav.registre.hodejegeren.provider.rs;

import lombok.extern.slf4j.Slf4j;
import no.rtv.namespacetps.TpsPersonDokumentType;
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

import java.util.ArrayList;
import java.util.List;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.hodejegeren.TpsPersonDokument;
import no.nav.registre.hodejegeren.mongodb.SyntHistorikk;
import no.nav.registre.hodejegeren.provider.rs.requests.HistorikkRequest;
import no.nav.registre.hodejegeren.service.HistorikkService;

@Slf4j
@RestController
@RequestMapping("api/v1/historikk")
public class HistorikkController {

    @Autowired
    private HistorikkService historikkService;

    @LogExceptions
    @GetMapping(value = "")
    public List<SyntHistorikk> hentAllHistorikk() {
        return historikkService.hentAllHistorikk();
    }

    @LogExceptions
    @GetMapping(value = "medKilde")
    public List<SyntHistorikk> hentHistorikkMedKilde(@RequestParam String kilde) {
        return historikkService.hentHistorikkMedKilde(kilde);
    }

    @LogExceptions
    @GetMapping(value = "idsMedKilde")
    public List<String> hentIdsMedKilde(@RequestParam String kilde) {
        return historikkService.hentIdsMedKilde(kilde);
    }

    @LogExceptions
    @GetMapping(value = "{id}")
    public SyntHistorikk hentHistorikkMedId(@PathVariable String id) {
        return historikkService.hentHistorikkMedId(id);
    }

    @LogExceptions
    @PostMapping(value = "")
    public List<String> leggTilHistorikk(@RequestBody HistorikkRequest historikkRequest) {
        if ("skd".equals(historikkRequest.getKilde())) {
            log.error("Skd historikk opprettes gjennom persondokumenter fra orkestratoren.");
            return new ArrayList<>();
        } else {
            return historikkService.leggTilHistorikkPaaIdent(historikkRequest);
        }
    }

    @LogExceptions
    @PostMapping(value = "skd/oppdaterDokument/{ident}")
    public List<String> oppdaterTpsPersonDokument(@PathVariable String ident, @RequestBody TpsPersonDokumentType tpsPersonDokument) {
        return historikkService.oppdaterTpsPersonDokument(ident, tpsPersonDokument);
    }

    @LogExceptions
    @DeleteMapping(value = "{id}")
    public ResponseEntity slettHistorikk(@PathVariable String id) {
        return historikkService.slettHistorikk(id);
    }

    @LogExceptions
    @DeleteMapping(value = "kilde/{id}")
    public ResponseEntity slettKilde(@PathVariable String id, @RequestParam String navnPaaKilde) {
        return historikkService.slettKilde(id, navnPaaKilde);
    }
}
