package no.nav.testnav.apps.apioversiktservice.provider;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.apioversiktservice.service.ApiOversiktService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/apioversikt")
@RestController
@RequiredArgsConstructor
public class ApiOversiktController {

    private final ApiOversiktService apiOversiktService;

    @GetMapping
    public JsonNode getApiOversikt() {

        return apiOversiktService.getDokumeter();
    }
}
