package no.nav.dolly.api;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.dolly.domain.resultset.RsOpenAmRequest;
import no.nav.dolly.domain.resultset.RsOpenAmResponse;
import no.nav.dolly.service.OpenAmService;

@RestController
@RequestMapping(value = "/api/v1/openam", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpenAmController {

    @Autowired
    private OpenAmService openAmService;

    @PostMapping
    public List<RsOpenAmResponse> opprettIdenter(@RequestBody RsOpenAmRequest request) {
        List<RsOpenAmResponse> response = new ArrayList<>(request.getMiljoer().size());
        for (String miljoe : request.getMiljoer()) {
            response.add(openAmService.opprettIdenter(request.getIdenter(), miljoe));
        }
        return response;
    }
}