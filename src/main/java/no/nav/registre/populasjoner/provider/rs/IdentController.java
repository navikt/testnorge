package no.nav.registre.populasjoner.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.populasjoner.domain.Ident;
import no.nav.registre.populasjoner.service.IdentService;

@RestController
@RequestMapping("/api/v1/ident")
@RequiredArgsConstructor
public class IdentController {

    private final IdentService identService;

    @GetMapping("{id}")
    public Ident find(
            @PathVariable Long id
    ) {
        return identService.findIdentById(id);
    }

    @PostMapping("{fnr}")
    public Ident save(
            @PathVariable String fnr
    ) {
        return identService.saveIdentWithFnr(fnr);
    }

    @PutMapping("{id}")
    public Ident update(
            @PathVariable Long id
    ) {
        return identService.updateIdentWithId(id);
    }

    @DeleteMapping("{id}€")
    public Ident delete(
            @PathVariable Long id
    ) {
        return identService.deleteIdentWithId(id);
    }
}
