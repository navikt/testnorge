package no.nav.dolly.budpro.generate;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api")
class BudproController {

    private final BudProService service;

    @GetMapping("/random")
    List<BudproRecord> getRandomizedBudproData(
            @RequestParam(required = false, defaultValue = "0") int seed,
            @RequestParam(required = false, defaultValue = "100") int limit
    ) {
        return service.randomize(seed, limit);
    }

}
