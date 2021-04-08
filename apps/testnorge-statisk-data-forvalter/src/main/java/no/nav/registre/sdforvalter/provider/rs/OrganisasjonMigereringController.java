package no.nav.registre.sdforvalter.provider.rs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.sdforvalter.adapter.EregAdapter;
import no.nav.registre.sdforvalter.consumer.rs.OrganisasjonFasteDataConsumer;

@RestController
@RequestMapping("/api/v1/organisasjon/migrering")
@RequiredArgsConstructor
public class OrganisasjonMigereringController {
    private final EregAdapter eregAdapter;
    private final OrganisasjonFasteDataConsumer fasteDataConsumer;

    @PostMapping
    public ResponseEntity<HttpStatus> opprett(@RequestParam(required = false) String gruppe) {
        var eregListe = gruppe == null ? eregAdapter.fetchAll() : eregAdapter.fetchBy(gruppe);
        fasteDataConsumer.opprett(eregListe);
        return ResponseEntity.noContent().build();
    }
}
