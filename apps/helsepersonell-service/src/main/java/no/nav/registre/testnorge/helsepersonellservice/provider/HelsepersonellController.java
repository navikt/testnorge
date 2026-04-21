package no.nav.registre.testnorge.helsepersonellservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.helsepersonellservice.service.HelsepersonellService;
import no.nav.testnav.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static no.nav.registre.testnorge.helsepersonellservice.config.CachingConfig.CACHE_HELSEPERSONELL;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/helsepersonell")
public class HelsepersonellController {

    private final HelsepersonellService helsepersonellService;

    @Cacheable(CACHE_HELSEPERSONELL)
    @GetMapping
    public Mono<HelsepersonellListeDTO> getHelsepersonell() {

        return helsepersonellService.getHelsepersonell()
                .map(HelsepersonellListeDTO::new);
    }
}
