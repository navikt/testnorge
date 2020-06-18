package no.nav.registre.skd.provider.rs;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.skd.consumer.requests.SendToTpsRequest;
import no.nav.registre.skd.consumer.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.skd.service.IdentService;

@RestController
@RequestMapping("api/v1/ident")
public class IdentController {

    @Autowired
    private IdentService identService;

    @ApiOperation(value = "Her kan man slette alle skd-meldinger tilhørende identer fra en gitt avspillergruppe. Returnerer en liste av melding-idene som er sendt til sletting.")
    @DeleteMapping("{avspillergruppeId}")
    public List<Long> slettIdenterFraAvspillergruppe(
            @PathVariable Long avspillergruppeId,
            @RequestParam(required = false, defaultValue = "") List<String> miljoer,
            @RequestBody List<String> identer
    ) {
        return identService.slettIdenterFraAvspillergruppe(avspillergruppeId, miljoer, identer);
    }

    @PostMapping("oppdaterKommunenummer/{avspillergruppeId}")
    public List<Long> oppdaterKommunenummerIAvspillergruppe(
            @PathVariable Long avspillergruppeId
    ) {
        return identService.oppdaterKommunenummerIAvspillergruppe(avspillergruppeId);
    }

    @PostMapping("sendMeldingerTilTps/{avspillergruppeId}")
    public SkdMeldingerTilTpsRespons sendMeldingerTilTps(
            @PathVariable Long avspillergruppeId,
            @RequestBody SendToTpsRequest sendToTpsRequest
    ) {
        return identService.sendToTps(avspillergruppeId, sendToTpsRequest);
    }
}
