package no.nav.dolly.api;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.dolly.domain.resultset.RsOpenAmResponse;
import no.nav.dolly.service.OpenAmService;

@RestController
@RequestMapping(value = "/api/v1/openam", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpenAmController {

    @Autowired
    private OpenAmService openAmService;

    @GetMapping ("/opprett")
    public List<RsOpenAmResponse> opprettIdenter(@RequestParam("identliste") List<String> identliste, @RequestParam("miljoer") List<String> miljoer) {
        List<RsOpenAmResponse> response = new ArrayList<>(miljoer.size());
        for (String miljoe : miljoer) {
            response.add(openAmService.opprettIdenter(identliste, miljoe));
        }
        return response;
    }
}