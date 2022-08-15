package no.nav.registre.testnorge.personsearchservice.controller;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.personsearchservice.service.IdentService;
import no.nav.testnav.libs.dto.personsearchservice.v1.IdentdataDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/identer")
@RequiredArgsConstructor
public class IdentSearchController {

    private final IdentService identService;

    @GetMapping
    public List<IdentdataDTO> getIdenter(String fragment) {

        return identService.getIdenter(fragment)
                .collectList()
                .block();
    }
}
