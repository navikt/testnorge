package no.nav.registre.testnorge.synt.person.provider;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.synt.person.service.SyntPersonService;

@RestController
@RequestMapping("/api/v1/synt-person")
@RequiredArgsConstructor
public class SyntPersonController {
    private final SyntPersonService service;

    @PostMapping
    @ApiOperation(value = "Oppretter en syntetisk person", authorizations = @Authorization(value = "Bearer"))
    public ResponseEntity<?> createSyntPerson() {
        service.createSyntPerson();
        return ResponseEntity.ok().build();
    }
}