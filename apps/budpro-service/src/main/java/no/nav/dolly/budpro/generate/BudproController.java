package no.nav.dolly.budpro.generate;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api")
class BudproController {

    private final BudProService service;

    @GetMapping("/random")
    List<BudproRecord> getRandomizedBudproData(
            @RequestParam(required = false) Long seed,
            @RequestParam(required = false, defaultValue = "100") int limit
    ) {
        return service.randomize(seed, limit);
    }

    @PostMapping("/random")
    List<BudproRecord> getOverriddenBudproData(
            @RequestParam(required = false) Long seed,
            @RequestParam(required = false, defaultValue = "100") int limit,
            @RequestBody BudproRecord override
    ) {
        return service.override(seed, limit, override);
    }

}
