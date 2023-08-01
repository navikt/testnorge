package no.nav.registre.sdforvalter.provider.rs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import no.nav.registre.sdforvalter.adapter.EregAdapter;
import no.nav.registre.sdforvalter.consumer.rs.organisasjon.OrganisasjonFasteDataConsumer;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/organisasjon/migrering")
@RequiredArgsConstructor
@Tag(
        name = "OrganisasjonMigreringController",
        description = "Operasjoner knyttet til databasetabell EREG."
)
public class OrganisasjonMigreringController {

    private final EregAdapter eregAdapter;
    private final OrganisasjonFasteDataConsumer fasteDataConsumer;

    @PostMapping
    @Operation(
            description = "Migrerer organisasjoner som er definert i databasetabell EREG til tjenesten testnav-organisasjon-faste-data-service."
    )
    public void opprett(@RequestParam(required = false) Gruppe gruppe) {
        var eregListe = gruppe == null ? eregAdapter.fetchAll() : eregAdapter.fetchBy(gruppe.name());
        fasteDataConsumer.opprett(eregListe);
    }
}
