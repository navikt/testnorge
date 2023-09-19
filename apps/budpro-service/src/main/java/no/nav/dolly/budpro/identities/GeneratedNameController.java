package no.nav.dolly.budpro.identities;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/navn")
public class GeneratedNameController {

    private final GeneratedNameService service;

    @GetMapping("/{number}")
    NavnDTO[] getNames(@PathVariable int number) {
        return service.getNames(number);
    }

}
