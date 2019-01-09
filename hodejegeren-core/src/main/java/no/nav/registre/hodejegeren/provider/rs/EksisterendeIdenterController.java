package no.nav.registre.hodejegeren.provider.rs;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.hodejegeren.service.EksisterendeIdenterService;

@RestController
public class EksisterendeIdenterController {

    @Autowired
    EksisterendeIdenterService eksisterendeIdenterService;

    @LogExceptions
    @ApiOperation(value = "Her bestilles sykemeldinger på et gitt antall fødselsnummer som hentes tilfeldig fra TPSF. \n"
            + "Disse er føreløpig ikke garantert til å være gyldige fnr med tilhørende arbeids forhold for å få en sykemelding\n"
            + "De er garantert til å være myndige")
    @GetMapping("api/v1/eksisterende-identer")
    public List<String> hentEksisterendeIdenter(@RequestParam("avspillerGruppeId") Long avspillerGruppeId, @RequestParam("miljoe") String miljoe, @RequestParam("antallPersoner") int antallPersoner) {
        return eksisterendeIdenterService.hentMyndigeIdenterIAvspillerGruppe(avspillerGruppeId, miljoe, antallPersoner);
    }

}
