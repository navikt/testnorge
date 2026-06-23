package no.nav.dolly.provider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.dto.DashboardBestillingerDTO;
import no.nav.dolly.domain.dto.DashboardDollyTeamsDTO;
import no.nav.dolly.domain.dto.DashboardOrganisasjonerDTO;
import no.nav.dolly.domain.dto.DashboardOversiktDTO;
import no.nav.dolly.domain.dto.DashboardTeamsDTO;
import no.nav.dolly.service.DashboardService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import tools.jackson.databind.JsonNode;

import java.time.Month;

import static no.nav.dolly.config.CachingConfig.CACHE_DASHBOARD_DOLLYTEAMS;
import static no.nav.dolly.config.CachingConfig.CACHE_DASHBOARD_ORGANISASJONER;
import static no.nav.dolly.config.CachingConfig.CACHE_DASHBOARD_OVERSIKT;
import static no.nav.dolly.config.CachingConfig.CACHE_DASHBOARD_TEAMS;

@RequestMapping("/api/v1/dashboard")
@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping(value = "/bestillinger")
    @Operation(description = "Henter antall bestillinger og personer levert (opprettet/importert og/eller gjenopprettet).")
    public Flux<DashboardBestillingerDTO> getDashboardPersoner(@RequestParam int year, @RequestParam Month month) {

        return dashboardService.getBestillingerStatus(year, month);
    }

    @GetMapping(value = "/feil/detaljert")
    @Operation(description = "Henter detaljert feilstatus for personer opprettet.")
    public Flux<JsonNode> getDashboardFeilDetaljert(@RequestParam int year,
                                                    @RequestParam Month month,
                                                    @RequestParam int day) {

        return dashboardService.getFeilstatusDetaljert(year, month, day);
    }

    @GetMapping(value = "/feil/summert")
    @Operation(description = "Henter summert feilstatus for personer opprettet.")
    public Flux<JsonNode> getDashboardFeil(@RequestParam int year, @RequestParam Month month) {

        return dashboardService.getFeilstatusSummert(year, month);
    }

    @Cacheable(value = CACHE_DASHBOARD_OVERSIKT)
    @GetMapping(value = "/oversikt")
    @Operation(description = "Henter tilgjengelige perioder.")
    public Flux<DashboardOversiktDTO> getDashboardFeilOversikt() {

        return dashboardService.getPerioderOversikt();
    }

    @Cacheable(value = CACHE_DASHBOARD_TEAMS)
    @GetMapping(value = "/teams")
    @Operation(description = "Henter status per team i hht Teamkatalogen og antall unike personer som har bestilt. Gjelder AZURE-brukere.")
    public Flux<DashboardTeamsDTO> getDashboardTeams() {

        return dashboardService.getTeamsStatus();
    }

    @Cacheable(value = CACHE_DASHBOARD_ORGANISASJONER)
    @GetMapping(value = "/organisasjoner")
    @Operation(description = "Henter status per organisasjon med antall unike personer som har bestilt. Gjelder BANKID-brukere.")
    public Flux<DashboardOrganisasjonerDTO> getDashboardOrganisasjoner() {

        return dashboardService.getOrganisasjonerStatus();
    }

    @Cacheable(value = CACHE_DASHBOARD_DOLLYTEAMS)
    @GetMapping(value = "/dollyteams")
    @Operation(description = "Henter status for bruk av Dolly-teams, og antall medlemmer per team. Gjelder TEAM-brukere.")
    public Flux<DashboardDollyTeamsDTO> getDashboardDollyTeams() {

        return dashboardService.getDollyTeamsStatus();
    }
}
