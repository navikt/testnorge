package no.nav.registre.testnorge.generernavnservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.generernavnservice.service.GenerateNavnService;
import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;

@RestController
@RequestMapping("/api/v1/navn")
@RequiredArgsConstructor
public class GenerateNavnController {

    private final GenerateNavnService service;

    @GetMapping
    public ResponseEntity<NavnDTO> generateName() {
        NavnDTO dto = service.getRandomNavn().toDTO();
        return ResponseEntity.ok(dto);
    }
}
