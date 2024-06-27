package no.nav.skattekortservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.skattekortservice.service.KodeverkService;
import no.nav.testnav.libs.dto.skattekortservice.v1.KodeverkTyper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/kodeverk")
@RequiredArgsConstructor
public class KodeverkController {

    private final KodeverkService kodeverkService;

    @GetMapping
    public Map<String, String> getKodeverk(@RequestParam KodeverkTyper kodeverkstype) {

        return kodeverkService.getType(kodeverkstype);
    }
}