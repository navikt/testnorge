package no.nav.registre.frikort.provider.rs;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import org.springframework.web.bind.annotation.*;

import no.nav.registre.frikort.service.SyntetiseringService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v1/syntetisering")
@RequiredArgsConstructor
public class SyntetiseringController {

    private final SyntetiseringService syntetiseringService;

    @PostMapping(value = "/generer")
    @ApiOperation(value = "Generer syntetiske frikort.")
    public Map<String, List<SyntFrikortResponse>> genererFrikort(@RequestBody Map<String, Integer> request) {
        return syntetiseringService.hentSyntetiskeFrikort(request);
    }

}
