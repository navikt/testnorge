package no.nav.dolly.provider;

import lombok.RequiredArgsConstructor;
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
    public Flux<DashboardPersonerDTO> getDashboard() {

        return dashboardService.getPersonerStatus();
    }

    @GetMapping(value = "/teams")
    public Flux<DashboardTeamsDTO> getDashboardTeams() {

        return dashboardService.getTeamsStatus();
    }
}
