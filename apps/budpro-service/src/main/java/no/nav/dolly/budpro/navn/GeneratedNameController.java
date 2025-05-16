package no.nav.dolly.budpro.navn;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/navn")
@RequiredArgsConstructor
class GeneratedNameController {

    private final GeneratedNameService service;

    @GetMapping
    String[] get(
            @RequestParam(required = false) Long seed,
            @RequestParam(required = false, defaultValue = "10") int number
    ) {
        return service.getNames(seed, number);
    }

}
