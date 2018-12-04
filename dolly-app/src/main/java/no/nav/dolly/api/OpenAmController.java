package no.nav.dolly.api;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsOpenAmRequest;
import no.nav.dolly.domain.resultset.RsOpenAmResponse;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.GruppeRepository;
import no.nav.dolly.service.OpenAmService;

@Transactional
@RestController
@RequestMapping(value = "/api/v1/openam", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpenAmController {

    @Autowired
    private OpenAmService openAmService;

    @Autowired
    private GruppeRepository gruppeRepository;

    @PostMapping
    public List<RsOpenAmResponse> opprettIdenter(@RequestBody RsOpenAmRequest request) {
        List<RsOpenAmResponse> response = new ArrayList<>(request.getMiljoer().size());
        for (String miljoe : request.getMiljoer()) {
            response.add(openAmService.opprettIdenter(request.getIdenter(), miljoe));
        }
        return response;
    }

    @PutMapping("/gruppe/{gruppeId}")
    public void oppdaterOpenAmSentStatus(@PathVariable(value = "gruppeId") Long gruppeId, @RequestParam Boolean isOpenAmSent) {
        Optional<Testgruppe> testgruppe = gruppeRepository.findById(gruppeId);
        if (testgruppe.isPresent()) {
            testgruppe.get().setOpenAmSent(isOpenAmSent);
            gruppeRepository.save(testgruppe.get());
        } else {
            throw new NotFoundException(format("GruppeId %s ble ikke funnet.", gruppeId));
        }
    }
}