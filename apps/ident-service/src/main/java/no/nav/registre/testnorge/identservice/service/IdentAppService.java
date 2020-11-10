package no.nav.registre.testnorge.identservice.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class IdentAppService {

    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello World!");
    }

}
