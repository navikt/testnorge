package no.nav.dolly.budpro.identities;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/navn")
public class GeneratedNameController {

    private final GeneratedNameService service;

    @GetMapping("/{number}")
    String[] getNames(@RequestParam(required = false) Long seed, @PathVariable int number) {
        return service.getNames(seed, number);
    }

}
