package no.nav.registre.hodejegeren.provider.rs;

import io.swagger.annotations.ApiOperation;
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
import java.util.Set;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
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
    @ApiOperation(value = "Hent all historikk fra databasen.")
    @GetMapping(value = "")
    public List<SyntHistorikk> hentAllHistorikk() {
        return historikkService.hentAllHistorikk();
    }

    @LogExceptions
    @ApiOperation(value = "Hent all historikk med en gitt kilde fra databasen.")
    @GetMapping(value = "medKilde")
    public List<SyntHistorikk> hentHistorikkMedKilde(@RequestParam List<String> kilder) {
        return historikkService.hentHistorikkMedKilder(kilder);
    }

    @LogExceptions
    @ApiOperation(value = "Hent alle id-ene som er registrert med en gitt kilde fra databasen.")
    @GetMapping(value = "idsMedKilde")
    public Set<String> hentIdsMedKilde(@RequestParam List<String> kilder) {
        return historikkService.hentIdsMedKilder(kilder);
    }

    @LogExceptions
    @ApiOperation(value = "Hent all historikk på en gitt id fra databasen.")
    @GetMapping(value = "{id}")
    public SyntHistorikk hentHistorikkMedId(@PathVariable String id) {
        return historikkService.hentHistorikkMedId(id);
    }

    @LogExceptions
    @ApiOperation(value = "Legg til ny historikk i databasen")
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
    @ApiOperation(value = "Oppdater persondokument i databasen.")
    @PostMapping(value = "skd/oppdaterDokument/{ident}")
    public List<String> oppdaterTpsPersonDokument(@PathVariable String ident, @RequestBody TpsPersonDokumentType tpsPersonDokument) {
        return historikkService.oppdaterTpsPersonDokument(ident, tpsPersonDokument);
    }

    @LogExceptions
    @ApiOperation(value = "Slett historikk med en gitt id fra databasen.")
    @DeleteMapping(value = "{id}")
    public ResponseEntity slettHistorikk(@PathVariable String id) {
        return historikkService.slettHistorikk(id);
    }

    @LogExceptions
    @ApiOperation(value = "Slett historikk på en gitt kilde på en gitt id fra databasen.")
    @DeleteMapping(value = "kilde/{id}")
    public ResponseEntity slettKilde(@PathVariable String id, @RequestParam String navnPaaKilde) {
        return historikkService.slettKilde(id, navnPaaKilde);
    }
}
