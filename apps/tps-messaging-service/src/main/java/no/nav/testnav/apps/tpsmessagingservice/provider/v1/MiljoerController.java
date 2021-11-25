package no.nav.testnav.apps.tpsmessagingservice.provider.v1;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.service.MiljoerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/miljoer")
@RequiredArgsConstructor
public class MiljoerController {

    private final MiljoerService miljoerService;

    @GetMapping
    public List<String> getTilgjengeligeMiljoer() {

        return miljoerService.getMiljoer();
    }
}
