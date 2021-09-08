package no.nav.registre.testnav.geografiskekodeverkservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnav.geografiskekodeverkservice.domain.Kodeverk;
import no.nav.registre.testnav.geografiskekodeverkservice.service.KodeverkService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class KodeverkController {

    @GetMapping(value = "/kommuner")
    public List<Kodeverk> getKommuner(@RequestParam(required = false) String kommunenr) {
        return KodeverkService.getKommuner(kommunenr);
    }

    @GetMapping(value = "/landkoder")
    public List<Kodeverk> getLandkoder(@RequestParam(required = false) String land) {
        return KodeverkService.getLandkoder(land);
    }

    @GetMapping(value = "/postnummer")
    public List<Kodeverk> getPostnummer(@RequestParam(required = false) String poststed) {
        return KodeverkService.getPostnummer(poststed);
    }

    @GetMapping(value = "/embeter")
    public List<Kodeverk> getEmbeter() {
        return KodeverkService.getEmbeter();
    }
}
