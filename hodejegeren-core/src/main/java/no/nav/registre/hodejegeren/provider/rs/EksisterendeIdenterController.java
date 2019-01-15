package no.nav.registre.hodejegeren.provider.rs;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.hodejegeren.service.EksisterendeIdenterService;

@RestController
public class EksisterendeIdenterController {

    @Autowired
    EksisterendeIdenterService eksisterendeIdenterService;

    @LogExceptions
    @ApiOperation(value = "Her kan man hente et gitt antall levende personer fra en gitt avspillergruppe i TPSF.")
    @GetMapping("api/v1/eksisterende-identer/{avspillergruppeId}")
    public List<String> hentEksisterendeIdenterIGruppe(@PathVariable("avspillergruppeId") Long avspillergruppeId) {
        return eksisterendeIdenterService.finnLevendeIdenter(avspillergruppeId);
    }

    @LogExceptions
    @ApiOperation(value = "Her kan et gitt antall myndige levende personer hentes fra en gitt avspillergruppe i TPSF. "
            + "Systemet sjekker status-quo på personen i det angitte miljø.")
    @GetMapping("api/v1/eksisterende-identer")
    public List<String> hentEksisterendeMyndigeIdenter(@RequestParam("avspillergruppeId") Long avspillergruppeId, @RequestParam("miljoe") String miljoe, @RequestParam("antallPersoner") int antallPersoner) {
        return eksisterendeIdenterService.hentMyndigeIdenterIAvspillerGruppe(avspillergruppeId, miljoe, antallPersoner);
    }

    @LogExceptions
    @ApiOperation(value = "Her bestilles sykemeldinger på et gitt antall fødselsnummer som hentes tilfeldig fra TPSF. \n"
            + "Disse er føreløpig ikke garantert til å være gyldige fnr med tilhørende arbeids forhold for å få en sykemelding\n"
            + "De er garantert til å være myndige")
    @GetMapping("api/v1/fnr-med-navkontor/{avspillerGruppeId}/{miljoe}/{antallPersoner}")
    public Map<String, String> hentEksisterendeMyndigeIdenterMedNavKontor(@PathVariable("avspillerGruppeId") Long avspillerGruppeId, @PathVariable("miljoe") String miljoe,
            @PathVariable("antallPersoner") int antallPersoner) {
        List<String> myndigeIdenter = eksisterendeIdenterService.hentMyndigeIdenterIAvspillerGruppe(avspillerGruppeId, miljoe, antallPersoner);
        return eksisterendeIdenterService.hentFnrMedNavKontor(miljoe, myndigeIdenter);
    }
}
