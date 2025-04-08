package no.nav.testnav.apps.apioversiktservice.provider;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.apioversiktservice.service.ApiOversiktService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/api/v1/apioversikt")
@RestController
@RequiredArgsConstructor
public class ApiOversiktController {

    private final ApiOversiktService apiOversiktService;

    @GetMapping
    public Map<String, String> getApiOversikt() {

        apiOversiktService.getDokumeter();
    }
}
