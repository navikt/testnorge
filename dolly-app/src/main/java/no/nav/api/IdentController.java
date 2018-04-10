package no.nav.api;

import no.nav.repository.IdentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IdentController {

    @Autowired
    IdentRepository identRepository;

    @GetMapping("/identer")
    ResponseEntity alleIdenter(){
        return ResponseEntity.ok(identRepository.findAll().toString());
    }
}
