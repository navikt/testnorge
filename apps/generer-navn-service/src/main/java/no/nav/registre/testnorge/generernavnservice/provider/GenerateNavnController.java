package no.nav.registre.testnorge.generernavnservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.generernavnservice.domain.Navn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.generernavnservice.service.GenerateNavnService;
import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/navn")
@RequiredArgsConstructor
public class GenerateNavnController {

    private final GenerateNavnService service;

    @GetMapping
    public List<NavnDTO> generateName(@RequestParam Integer antall) {

        List<Navn> navnList = service.getRandomNavn(antall);
        return navnList.stream().map(value -> NavnDTO
                .builder()
                .substantiv(value.getSubstantiv())
                .adjektiv(value.getAdjektiv())
                .build()
        ).collect(Collectors.toList());
    }
}
