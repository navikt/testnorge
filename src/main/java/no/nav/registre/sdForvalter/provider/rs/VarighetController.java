package no.nav.registre.sdForvalter.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Period;

import no.nav.registre.sdForvalter.database.model.Varighet;
import no.nav.registre.sdForvalter.maintainance.ValidityChecker;
import no.nav.registre.sdForvalter.service.StaticDataService;

@RestController
@RequestMapping("/api/v1/varighet")
@RequiredArgsConstructor
public class VarighetController {

    private final StaticDataService staticDataService;
    private final ValidityChecker validityChecker;

    @GetMapping("/{id}")
    public ResponseEntity<Varighet> getVarighetById(@PathVariable Long id) {
        return ResponseEntity.ok(staticDataService.getVarighet(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Varighet> updateVarighet(@PathVariable Long id, @RequestBody(required = false) Period ttl) {
        return ResponseEntity.ok(staticDataService.updateVarighet(id, ttl));
    }

    @GetMapping("/validate")
    public ResponseEntity validateVarigheter() {
        validityChecker.validatePeriods();
        return ResponseEntity.ok().build();
    }
}
