package no.nav.registre.testnorge.generernavnservice.provider;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.generernavnservice.service.GenerateNavnService;
import no.nav.registre.testnorge.generernavnservice.service.VerifyNavnService;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/navn")
@RequiredArgsConstructor
public class GenerateNavnController {

    private final GenerateNavnService generateNavnService;
    private final VerifyNavnService verifyNavnService;

    @GetMapping
    public Mono<List<NavnDTO>> generateName(
            @RequestParam(required = false, defaultValue = "10") Integer antall,
            @RequestParam(required = false) Long seed) {

        return Mono.just(generateNavnService.getRandomNavn(seed, antall));
    }

    @PostMapping("/check")
    @Schema(description = "Verifiser om navn finnes i liste over godkjente alternativer")
    public Mono<Boolean> checkName(@RequestBody NavnDTO navn) {

        return Mono.just(verifyNavnService.verifyNavn(navn));
    }
}
