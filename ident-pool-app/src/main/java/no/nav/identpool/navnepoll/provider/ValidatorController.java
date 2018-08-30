package no.nav.identpool.navnepoll.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.identpool.navnepoll.NavnepollService;
import no.nav.identpool.navnepoll.domain.Navn;

@RestController
@RequestMapping("/navnepoll")
public class ValidatorController {
    @Autowired
    private NavnepollService service;

    @PostMapping("/valider")
    public ResponseEntity<Boolean> validate(@RequestBody Navn navn) {
        return ResponseEntity.ok(service.isValid(navn));
    }
}
