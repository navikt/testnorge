package no.nav.registre.bisys.provider;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.bisys.consumer.BidragsmeldingConsumer;
import no.nav.registre.bisys.consumer.response.SyntetisertBidragsmelding;
import no.nav.registre.bisys.exception.SyntetisertBidragsmeldingException;
import no.nav.registre.bisys.provider.request.SyntetiserBisysRequest;
import no.nav.registre.bisys.service.SyntetiseringService;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;
    private final BidragsmeldingConsumer bidragsmeldingConsumer;

    @Operation(summary = "Her kan man generere syntetiske bidragsmeldinger på personer i en gitt TPSF-avspillergruppe i et gitt miljø og lagre i Bisys UI.")
    @PostMapping(value = "/opprett")
    public void genererOgLagreBidragsmeldinger(
            @RequestBody SyntetiserBisysRequest syntetiserBisysRequest
    ) throws SyntetisertBidragsmeldingException {
        List<SyntetisertBidragsmelding> syntetisertBidragsmeldinger = syntetiseringService.generateBidragsmeldinger(syntetiserBisysRequest);
        bidragsmeldingConsumer.opprett(syntetisertBidragsmeldinger);
    }
}
