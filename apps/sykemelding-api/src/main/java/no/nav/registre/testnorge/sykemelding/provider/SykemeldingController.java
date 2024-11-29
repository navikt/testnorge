package no.nav.registre.testnorge.sykemelding.provider;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.sykemelding.domain.ApplicationInfo;
import no.nav.registre.testnorge.sykemelding.domain.Sykemelding;
import no.nav.registre.testnorge.sykemelding.service.SykemeldingService;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingResponseDTO;
import no.nav.testnav.libs.dto.sykemelding.v1.ValidationResultDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/sykemeldinger")
@RequiredArgsConstructor
public class SykemeldingController {

    private final SykemeldingService sykemeldingService;
    private final ApplicationInfo applicationInfo;

    @PostMapping
    public SykemeldingResponseDTO create(@RequestBody SykemeldingDTO dto) {

        return sykemeldingService.send(new Sykemelding(dto, applicationInfo));
    }

    @PostMapping
    public Mono<ValidationResultDTO> validate(@RequestBody SykemeldingDTO dto) {

        return sykemeldingService.validate(new Sykemelding(dto, applicationInfo));
    }
}
