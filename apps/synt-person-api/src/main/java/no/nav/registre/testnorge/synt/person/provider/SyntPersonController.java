package no.nav.registre.testnorge.synt.person.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.libs.dto.person.v1.PersonDTO;
import no.nav.registre.testnorge.synt.person.service.SyntPersonService;

@RestController
@RequestMapping("/api/v1/synt-person")
@RequiredArgsConstructor
public class SyntPersonController {
    private final SyntPersonService service;

    @PostMapping
    public ResponseEntity<PersonDTO> createSyntPerson() {
        return ResponseEntity.ok(service.createSyntPerson().toDTO());
    }
}