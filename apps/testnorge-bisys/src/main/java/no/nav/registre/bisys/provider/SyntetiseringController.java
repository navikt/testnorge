package no.nav.registre.bisys.provider;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.registre.bisys.service.SyntetiseringService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.bisys.consumer.BidragsmeldingConsumer;
import no.nav.registre.bisys.provider.request.SyntetiserBisysRequest;

@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;
    private final BidragsmeldingConsumer bidragsmeldingConsumer;

    @Operation(summary = "Her kan man generere syntetiske bidragsmeldinger på Testnorge personer og lagre i Bisys UI.")
    @PostMapping(value = "/opprett")
    public void genererOgLagreBidragsmeldinger(
            @RequestBody SyntetiserBisysRequest syntetiserBisysRequest
    ){
        var syntetisertBidragsmeldinger = syntetiseringService.generateBidragsmeldinger(syntetiserBisysRequest.getAntallNyeIdenter());
        if (!syntetisertBidragsmeldinger.isEmpty()){
            bidragsmeldingConsumer.opprett(syntetisertBidragsmeldinger, syntetiserBisysRequest.getMiljoe());
        }
    }
}
