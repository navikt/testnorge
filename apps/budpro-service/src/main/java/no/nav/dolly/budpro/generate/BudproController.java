package no.nav.dolly.budpro.generate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api")
@Slf4j
class BudproController {

    private final BudProService service;

    @GetMapping("/random")
    Flux<BudproRecord> getRandomizedBudproData(
            @RequestParam(required = false) Long seed,
            @RequestParam(required = false, defaultValue = "100") int limit
    ) {
        var generated = service.randomize(seed, limit);
        log.info("Returning {} generated record(s)", generated.size());
        return Flux.fromIterable(generated);
    }

    @PostMapping("/random")
    Flux<BudproRecord> getOverriddenBudproData(
            @RequestParam(required = false) Long seed,
            @RequestParam(required = false, defaultValue = "100") int limit,
            @RequestBody BudproRecord override
    ) {
        var generated = service.override(seed, limit, override);
        log.info("Returning {} generated record(s), with overrides", generated.size());
        return Flux.fromIterable(generated);
    }

}
