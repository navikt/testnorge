package no.nav.registre.testnorge.generernavnservice.provider;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.generernavnservice.service.GenerateNavnService;
import no.nav.registre.testnorge.generernavnservice.service.VerifyNavnService;
import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/navn")
@RequiredArgsConstructor
public class GenerateNavnController {

    private final GenerateNavnService generateNavnService;
    private final VerifyNavnService verifyNavnService;

    @GetMapping
    public List<NavnDTO> generateName(@RequestParam Integer antall) {

        return generateNavnService.getRandomNavn(antall);
    }

    @PostMapping("/check")
    @Schema(description = "Verifiser om navn finnes i liste over godkjente alternativer")
    public boolean checkName(@RequestBody NavnDTO navn) {

        return verifyNavnService.verifyNavn(navn);
    }
}
