package no.nav.registre.sdforvalter.provider.rs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import no.nav.registre.sdforvalter.adapter.TpsIdenterAdapter;
import no.nav.registre.sdforvalter.consumer.rs.person.PersonFasteDataConsumer;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/person/migrering")
@RequiredArgsConstructor
@Tag(
        name = "PersonMigreringController",
        description = "Oppretter personer i testnav-person-faste-data-service."
)
public class PersonMigreringController {
    private final TpsIdenterAdapter tpsIdenterAdapter;
    private final PersonFasteDataConsumer fasteDataConsumer;

    @PostMapping
    @Operation(
            description = "Oppretter personer i tjenesten testnav-person-faste-data-service basert på identer fra tabell TPS_IDENTER for angitt gruppe."
    )
    public void opprett(@RequestParam(required = false) Gruppe gruppe) {
        var eregListe = gruppe == null ? tpsIdenterAdapter.fetchAll() : tpsIdenterAdapter.fetchBy(gruppe.name());
        fasteDataConsumer.opprett(eregListe);
    }
}
