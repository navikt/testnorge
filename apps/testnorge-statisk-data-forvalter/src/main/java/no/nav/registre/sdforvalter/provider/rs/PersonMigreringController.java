package no.nav.registre.sdforvalter.provider.rs;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.sdforvalter.adapter.TpsIdenterAdapter;
import no.nav.registre.sdforvalter.consumer.rs.person.PersonFasteDataConsumer;

@RestController
@RequestMapping("/api/v1/person/migrering")
@RequiredArgsConstructor
public class PersonMigreringController {
    private final TpsIdenterAdapter tpsIdenterAdapter;
    private final PersonFasteDataConsumer fasteDataConsumer;

    @PostMapping
    public ResponseEntity<HttpStatus> opprett(@RequestParam(required = false) Gruppe gruppe) {
        var eregListe = gruppe == null ? tpsIdenterAdapter.fetchAll() : tpsIdenterAdapter.fetchBy(gruppe.name());
        fasteDataConsumer.opprett(eregListe);
        return ResponseEntity.noContent().build();
    }
}
