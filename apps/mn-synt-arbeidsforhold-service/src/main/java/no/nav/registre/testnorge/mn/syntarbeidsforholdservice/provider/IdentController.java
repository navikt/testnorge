package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Set;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service.ArbeidsfoholdService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/identer")
public class IdentController {

    private final ArbeidsfoholdService service;

    @PostMapping("/{ident}")
    public ResponseEntity<?> opprettArbeidsforholdForIdent(@PathVariable("ident") String ident) {
        service.startArbeidsforhold(ident);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{ident}")
                .buildAndExpand(ident)
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping
    public ResponseEntity<Set<String>> getIdenter() {
        return ResponseEntity.ok(service.getIdenterWithArbeidsforhold());
    }

    @GetMapping("/{ident}")
    public ResponseEntity<String> getIdent(@PathVariable("ident") String ident) {
        Set<String> identer = service.getIdenterWithArbeidsforhold();
        if (identer.contains(ident)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ident);
    }
}
