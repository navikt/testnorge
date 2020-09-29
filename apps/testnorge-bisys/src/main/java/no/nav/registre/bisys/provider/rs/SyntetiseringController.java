package no.nav.registre.bisys.provider.rs;


import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;
import no.nav.registre.bisys.exception.SyntetisertBidragsmeldingException;
import no.nav.registre.bisys.provider.requests.SyntetiserBisysRequest;
import no.nav.registre.bisys.service.SyntetiseringService;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    @Autowired
    private SyntetiseringService syntetiseringService;


    @Operation(summary = "Her kan man starte generering av syntetiske bidragsmeldinger på personer i en gitt TPSF-avspillergruppe i et gitt miljø.")
    @PostMapping(value = "/generer")
    public List<SyntetisertBidragsmelding> genererBidragsmeldinger(
            @RequestBody SyntetiserBisysRequest syntetiserBisysRequest
    ) throws SyntetisertBidragsmeldingException {

        return syntetiseringService.generateBidragsmeldinger(syntetiserBisysRequest);
    }


    @Operation(summary = "Her kan man generere syntetiske bidragsmeldinger på personer i en gitt TPSF-avspillergruppe i et gitt miljø og lagre i Bisys UI.")
    @PostMapping(value = "/genererOgLagre")
    public List<SyntetisertBidragsmelding> genererOgLagreBidragsmeldinger(
            @RequestBody SyntetiserBisysRequest syntetiserBisysRequest
    ) throws SyntetisertBidragsmeldingException {
        List<SyntetisertBidragsmelding> syntetisertBidragsmeldinger = syntetiseringService.generateBidragsmeldinger(syntetiserBisysRequest);
        syntetiseringService.processBidragsmeldinger(syntetisertBidragsmeldinger);
        return syntetisertBidragsmeldinger;
    }

    @Operation(summary = "Registrerer bidragsaker-, søknader, og vedtak i Bisys basert på syntetiske bidragsmeldinger.")
    @PostMapping(value = "/lagre")
    public void lagreBidragsmeldinger(@RequestBody List<SyntetisertBidragsmelding> bidragsmeldinger) {
        syntetiseringService.processBidragsmeldinger(bidragsmeldinger);
    }
}
