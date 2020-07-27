package no.nav.registre.populasjoner.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.populasjoner.domain.Ident;
import no.nav.registre.populasjoner.service.IdentService;

@RestController
@RequestMapping("/api/v1/ident")
@RequiredArgsConstructor
public class IdentController {

    private final IdentService identService;

    @GetMapping("{fnr}")
    public ResponseEntity<Ident> findByFnr(
            @PathVariable String fnr
    ) {
        var ident = identService.findIdentByFnr(fnr);
        if (ident != null) {
            return ResponseEntity.ok(ident);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<Ident>> findAllIdents() {
        var idents = identService.findAllIdents();
        if (idents != null && !idents.isEmpty()) {
            return ResponseEntity.ok(identService.findAllIdents());
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(idents);
    }

    @PostMapping("{fnr}")
    public ResponseEntity<Ident> save(
            @PathVariable String fnr
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(identService.saveIdent(fnr));
    }

    @PutMapping("{fnr}")
    public ResponseEntity<Ident> updateByFnr(
            @PathVariable String fnr
    ) {
        var ident = identService.updateIdent(fnr);
        if (ident != null) {
            return ResponseEntity.ok(ident);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("{fnr}")
    public ResponseEntity<Ident> deleteByFnr(
            @PathVariable String fnr
    ) {
        var ident = identService.deleteIdent(fnr);
        if (ident != null) {
            return ResponseEntity.ok(ident);
        }
        return ResponseEntity.notFound().build();
    }
}
