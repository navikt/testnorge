package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.dto.DashboardDollyTeamsDTO;
import no.nav.dolly.domain.dto.DashboardOrganisasjonerDTO;
import no.nav.dolly.domain.dto.DashboardPersonerDTO;
import no.nav.dolly.domain.dto.DashboardTeamsDTO;
import no.nav.dolly.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequestMapping("/api/v1/dashboard")
@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping(value = "/personer")
    @Operation(description = "Henter status for personer levert")
    public Flux<DashboardPersonerDTO> getDashboardPersoner() {

        return dashboardService.getPersonerStatus();
    }

    @GetMapping(value = "/teams")
    @Operation(description = "Henter status for team fra teamkatalogen, og antall unike personer som har bestilt")
    public Flux<DashboardTeamsDTO> getDashboardTeams() {

        return dashboardService.getTeamsStatus();
    }

    @GetMapping(value = "/organisasjoner")
    @Operation(description = "Henter status for organisasjoner fra Altinn, og antall unike personer som har bestilt")
    public Flux<DashboardOrganisasjonerDTO> getDashboardOrganisasjoner() {

        return dashboardService.getOrganisasjonerStatus();
    }

    @GetMapping(value = "/dollyteams")
    @Operation(description = "Henter status for bruk av Dolly-teams, og antall unike personer som har bestilt")
    public Flux<DashboardDollyTeamsDTO> getDashboardDollyTeams() {

        return dashboardService.getDollyTeamsStatus();
    }
}
