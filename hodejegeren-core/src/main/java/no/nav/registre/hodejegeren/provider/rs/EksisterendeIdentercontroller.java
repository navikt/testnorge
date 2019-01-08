package no.nav.registre.hodejegeren.provider.rs;

import io.swagger.annotations.ApiOperation;
import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.hodejegeren.service.EksisterendeIdenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EksisterendeIdentercontroller {

    @Autowired
    EksisterendeIdenterService eksisterendeIdenterService;

    @LogExceptions
    @ApiOperation(value = "Her bestilles sykemeldinger på et gitt antall fødselsnummer som hentes tilfeldig fra TPSF. \nDisse er føreløpig ikke garantert til å ha vært ansatt tidligere eller ikke ha en fødselsmelding fra før av")
    @GetMapping("api/v1/eksisterende-identer")
    public List<String> hentEksisterendeIdenter(@RequestParam("gruppe") Long gruppe, @RequestParam("miljoe") String miljoe, @RequestParam("antallPersoner") int antallPersoner) {
        return eksisterendeIdenterService.hentVokseneIdenterIGruppe(gruppe, miljoe, antallPersoner);
    }


}
