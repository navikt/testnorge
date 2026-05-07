package no.nav.dolly.provider;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.service.HendelseIdService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping("/api/v1/hendelseid")
@RestController
@RequiredArgsConstructor
public class HendelseIdController {

    private final HendelseIdService hendelseIdService;

    @GetMapping("/ident/{ident}")
    public Mono<String> getHendelserForIdent(@PathVariable String ident) {

        return hendelseIdService.getHendleserForIdent(ident);
    }
}
