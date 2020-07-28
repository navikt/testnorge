package no.nav.registre.populasjoner.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import no.nav.registre.populasjoner.domain.Populasjon;
import no.nav.registre.populasjoner.service.IdentService;

@RestController
@RequestMapping("/api/v1/populasjoner")
@RequiredArgsConstructor
public class PopulasjonerController {

    private final IdentService identService;

    @GetMapping
    public ResponseEntity<Populasjon[]> getPopulasjoner() {
        return ResponseEntity.ok(Populasjon.values());
    }

    @GetMapping("{populasjon}/identer")
    public ResponseEntity<Set<String>> getIdenter(@PathVariable("populasjon") Populasjon populasjon) {
        var identer = identService.getIdenter(populasjon);

        if (!identer.isEmpty()) {
            return ResponseEntity.ok(identer);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
