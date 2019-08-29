package no.nav.registre.bisys.provider.rs;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import no.nav.bidrag.ui.exception.BidragRequestProcessingException;
import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;
import no.nav.registre.bisys.provider.requests.SyntetiserBisysRequest;
import no.nav.registre.bisys.service.SyntetiseringService;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    @Autowired
    private SyntetiseringService syntetiseringService;

    @LogExceptions
    @ApiOperation(value = "Her kan man starte generering av syntetiske bidragsmeldinger på personer i en gitt TPSF-avspillergruppe i et gitt miljø.")
    @PostMapping(value = "/generer")
    public List<SyntetisertBidragsmelding> genererBidragsmeldinger(
            @RequestBody SyntetiserBisysRequest syntetiserBisysRequest) {

        return syntetiseringService.generateBidragsmeldinger(syntetiserBisysRequest);
    }

    @ApiOperation(value = "Registrerer bidragsaker-, søknader, og vedtak i Bisys basert på syntetiske bidragsmeldinger.")
    @PostMapping(value = "/lagre")
    public void lagreBidragsmeldinger(@RequestBody List<SyntetisertBidragsmelding> bidragsmeldinger)
            throws BidragRequestProcessingException {

        syntetiseringService.processBidragsmeldinger(bidragsmeldinger);
    }
}
