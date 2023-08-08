package no.nav.registre.sdforvalter.provider.rs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import no.nav.registre.sdforvalter.adapter.GruppeAdapter;
import no.nav.registre.sdforvalter.domain.GruppeListe;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/gruppe")
@Tag(
        name = "GruppeController",
        description = "Operasjoner på grupper lagret i database."
)
public class GruppeController {

    private final GruppeAdapter adapter;

    @GetMapping
    @Operation(
            description = "Henter ut alle grupper som er definert i tabell GRUPPE.",
            responses = {
                    @ApiResponse(description = "JSON som inneholder key 'liste', med et array av grupper.")
            }
    )
    public GruppeListe getGruppeListe() {
        return adapter.fetchGruppeListe();
    }

}
