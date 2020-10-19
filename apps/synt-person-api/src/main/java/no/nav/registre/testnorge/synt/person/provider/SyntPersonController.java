package no.nav.registre.testnorge.synt.person.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.testnorge.synt.person.consumer.dto.SyntPersonDTO;
import no.nav.registre.testnorge.synt.person.service.SyntPersonService;

@RestController
@RequestMapping("/api/v1/synt-person")
@RequiredArgsConstructor
public class SyntPersonController {
    private final SyntPersonService service;

    @PostMapping
    public ResponseEntity<?> createSyntPerson() {
        service.createSyntPerson();
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<SyntPersonDTO> getSyntPerson(@RequestParam String antall) {
        return service.getSyntPerson(antall);
    }
}