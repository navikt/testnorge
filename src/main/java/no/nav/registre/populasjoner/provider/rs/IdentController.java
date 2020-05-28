package no.nav.registre.populasjoner.provider.rs;

import lombok.RequiredArgsConstructor;
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
    public Ident findByFnr(
            @PathVariable String fnr
    ) {
        return identService.findIdentByFnr(fnr);
    }

    @GetMapping
    public ResponseEntity<List<Ident>> findAllIdents() {
        return ResponseEntity.ok(identService.findAllIdents());
    }

    @PostMapping("{fnr}")
    public Ident save(
            @PathVariable String fnr
    ) {
        return identService.saveIdentWithFnr(fnr);
    }

    @PutMapping("{fnr}")
    public Ident updateByFnr(
            @PathVariable String fnr
    ) {
        return identService.updateIdentWithFnr(fnr);
    }

    @DeleteMapping("{fnr}")
    public Ident deleteByFnr(
            @PathVariable String fnr
    ) {
        return identService.deleteIdentWithFnr(fnr);
    }
}
