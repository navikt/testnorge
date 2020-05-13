package no.nav.registre.frikort.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import org.springframework.web.bind.annotation.*;

import no.nav.registre.frikort.service.SyntetiseringService;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;

    @PostMapping(value = "/frikort")
    @ApiOperation(value = "Generer syntetiske frikort.")
    public HashMap<String, SyntFrikortResponse[]> genererFrikort(@RequestBody HashMap<String, Integer> request){
        return syntetiseringService.hentSyntetiskeFrikort(request);
    }

}
