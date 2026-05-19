package no.nav.dolly.provider;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.dto.DashboardDTO;
import no.nav.dolly.service.DashboardService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequestMapping("/api/v1/dashboard")
@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping(value = "/bestillinger", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<DashboardDTO> getDashboard() {

        return dashboardService.getBestillingStatus();
    }
}
