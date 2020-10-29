package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.Set;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service.ArbeidsfoholdService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/identer")
public class IdentController {

    private final ArbeidsfoholdService service;

    @PostMapping("/{ident}")
    public ResponseEntity<?> opprettPerson(@PathParam("ident") String ident) {
        service.startArbeidsforhold(ident);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Set<String>> getPersoner() {
        return ResponseEntity.ok(service.getIdenterWithArbeidsforhold());
    }
}
