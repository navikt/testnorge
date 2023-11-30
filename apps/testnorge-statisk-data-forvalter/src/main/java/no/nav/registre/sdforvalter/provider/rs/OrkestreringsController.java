package no.nav.registre.sdforvalter.provider.rs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.adapter.TpsIdenterAdapter;
import no.nav.registre.sdforvalter.consumer.rs.person.PersonConsumer;
import no.nav.registre.sdforvalter.domain.TpsIdentListe;
import no.nav.registre.sdforvalter.provider.rs.dto.Orgnummer;
import no.nav.registre.sdforvalter.service.EnvironmentInitializationService;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orkestrering")
@Tag(
        name = "OrkestreingController",
        description = "Initialiserer ulike miljøer med statiske data."
)
public class OrkestreringsController {

    private final EnvironmentInitializationService environmentInitializationService;
    private final TpsIdenterAdapter tpsIdenterAdapter;
    private final PersonConsumer personConsumer;

    @PostMapping(value = "/{miljoe}")
    @Operation(
            description = "Orkestrerer testdata i TPS/PDL, testnorge-tp, KRR, AAREG, EREG."
    )
    public void initializeInEnvironment(@PathVariable String miljoe, @RequestParam(name = "gruppe", required = false) Gruppe gruppe) {
        environmentInitializationService.initializeEnvironmentWithStaticData(miljoe, gruppe != null ? gruppe.name() : null);
    }

    @PostMapping(value = "/tps/{miljoe}")
    @Operation(
            description = "Identisk med POST /{miljoe}."
    )
    public void initializeTps(@PathVariable String miljoe, @RequestParam(name = "gruppe", required = false) Gruppe gruppe) {
        environmentInitializationService.initializeIdent(miljoe, gruppe != null ? gruppe.name() : null);
    }

    @PostMapping(value = "/pdl")
    @Operation(
            description = "Oppretter personer i TPS basert på angitt gruppe."
    )
    public void initializePdlGruppe(@RequestParam(name = "gruppe") Gruppe gruppe) {
        TpsIdentListe liste = tpsIdenterAdapter.fetchBy(gruppe.name());
        personConsumer.opprettPersoner(liste);
    }

    @PostMapping(value = "/aareg/{miljoe}")
    @Operation(
            description = "Oppretter arbeidsforhold i AAREG basert på angitt gruppe."
    )
    public void initializeAareg(@PathVariable String miljoe, @RequestParam(name = "gruppe", required = false) Gruppe gruppe) {
        environmentInitializationService.initializeAareg(miljoe, gruppe != null ? gruppe.name() : null);
    }

    @PostMapping(value = "/krr")
    @Operation(
            description = "Oppretter kontaktinformasjon i KRR basert på angitt gruppe."
    )
    public void initializeKrr(@RequestParam(name = "gruppe", required = false) Gruppe gruppe) {
        environmentInitializationService.initializeKrr(gruppe != null ? gruppe.name() : null);
    }

    @PostMapping(value = "/ereg/{miljoe}")
    @Operation(
            description = "Oppretter organisasjoner i EREG basert på angitt gruppe."
    )
    public void initializeEreg(@PathVariable String miljoe, @RequestParam(name = "gruppe", required = false) Gruppe gruppe) {
        environmentInitializationService.initializeEreg(miljoe, gruppe != null ? gruppe.name() : null);
    }

    @PostMapping(value = "/ereg/{miljoe}/update/{regnr}")
    @Operation(
            description = "Oppdaterer organisasjon i EREG basert på angitt organisasjonsnummer."
    )
    public void updateEreg(@PathVariable String miljoe, @PathVariable("regnr") String regnr) {
        environmentInitializationService.updateEregByOrgnr(miljoe, regnr);
    }

    @PostMapping(value = "/ereg/{miljoe}/update")
    @Operation(
            description = "Oppdaterer organisasjoner i EREG basert på angitt gruppe."
    )
    public void updateEregByGruppe(@PathVariable String miljoe, @RequestParam(name = "gruppe") Gruppe gruppe) {
        environmentInitializationService.updateEregByGruppe(miljoe, gruppe != null ? gruppe.name() : null);
    }

    @PostMapping(value = "/organisasjoner")
    @Operation(
            description = "Oppretter organisasjoner i EREG basert på angitt liste med organisasjonsnummer."
    )
    public void saveBy(@RequestParam String miljoe, @RequestBody Orgnummer orgnummer) {
        environmentInitializationService.opprett(miljoe, orgnummer.getListe());
    }

}
