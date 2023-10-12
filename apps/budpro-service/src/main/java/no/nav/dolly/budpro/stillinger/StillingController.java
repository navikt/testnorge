package no.nav.dolly.budpro.stillinger;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/stilling")
@RequiredArgsConstructor
public class StillingController {

    private final StillingService service;

    @GetMapping("/all")
    List<Stilling> getAll() {
        return service.getAll();
    }

}
