package no.nav.registre.orkestratoren.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.orkestratoren.service.SykemeldingOrkestreringsService;

@RestController
@RequestMapping("api/v1/trigger")
@RequiredArgsConstructor
public class TriggerController {

    private final SykemeldingOrkestreringsService service;

    @GetMapping
    public void trigger(){
        service.orkistrer();
    }
}
