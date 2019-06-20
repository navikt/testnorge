package no.nav.registre.skd.provider.rs;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.skd.service.IdentService;

@RestController
@RequestMapping("api/v1/ident")
public class IdentController {

    @Autowired
    private IdentService identService;

    @LogExceptions
    @ApiOperation(value = "Her kan man slette alle skd-meldinger tilhørende identer fra en gitt avspillergruppe. Returnerer en liste av melding-idene som er sendt til sletting.")
    @DeleteMapping("{avspillergruppeId}")
    public List<Long> slettIdenterFraAvspillergruppe(@PathVariable Long avspillergruppeId, @RequestBody List<String> identer) {
        return identService.slettIdenterFraAvspillergruppe(avspillergruppeId, identer);
    }
}
