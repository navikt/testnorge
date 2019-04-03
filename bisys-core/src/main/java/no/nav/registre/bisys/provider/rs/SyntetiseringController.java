package no.nav.registre.bisys.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;
import no.nav.registre.bisys.consumer.rs.responses.BidragsResponse;
import no.nav.registre.bisys.provider.requests.SyntetiserBisysRequest;
import no.nav.registre.bisys.service.SyntetiseringService;

@RestController
@RequestMapping("api/v1/syntetisering")
public class SyntetiseringController {

    @Autowired
    private SyntetiseringService syntetiseringService;

    @LogExceptions
    @PostMapping(value = "/generer")
    public List<BidragsResponse> genererBidragsmeldinger(@RequestBody SyntetiserBisysRequest syntetiserBisysRequest) {
        return syntetiseringService.genererBidragsmeldinger(syntetiserBisysRequest);
    }
}
