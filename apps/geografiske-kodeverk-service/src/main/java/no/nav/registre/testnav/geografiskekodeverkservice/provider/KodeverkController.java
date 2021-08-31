package no.nav.registre.testnav.geografiskekodeverkservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnav.geografiskekodeverkservice.service.KodeverkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class KodeverkController {

    @GetMapping(value = "/kommuner")
    public String getKommuner() {
        return KodeverkService.getKommuner();
    }

    @GetMapping(value = "/landkoder")
    public String getLandkoder() {
        return "Not implemented yet";
    }

    @GetMapping(value = "/postnummer")
    public String getPostnummer() {
        return "Not implemented yet";
    }

    @GetMapping(value = "/embeter")
    public String getEmbeter() {
        return "Not implemented yet";
    }
}
