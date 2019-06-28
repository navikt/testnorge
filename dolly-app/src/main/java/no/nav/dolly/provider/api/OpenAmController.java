package no.nav.dolly.provider.api;

import static java.lang.String.format;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsOpenAmRequest;
import no.nav.dolly.domain.resultset.RsOpenAmResponse;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.GruppeRepository;
import no.nav.dolly.service.OpenAmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@RestController
@RequestMapping(value = "/api/v1/openam", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpenAmController {

    @Autowired
    private OpenAmService openAmService;

    @Autowired
    private GruppeRepository gruppeRepository;

    @Autowired
    private BestillingRepository bestillingRepository;

    @PostMapping
    public List<RsOpenAmResponse> sendIdenterTilOpenAm(@RequestBody RsOpenAmRequest request) {
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

    @CacheEvict(value = CACHE_BESTILLING, allEntries = true)
    @PostMapping("/bestilling/{bestillingId}")
    @Transactional
    public List<RsOpenAmResponse> sendBestillingTilOpenAm(@RequestParam Long bestillingId) {
        Optional<Bestilling> bestillingOpt = bestillingRepository.findById(bestillingId);
        if (bestillingOpt.isPresent()) {
            Bestilling bestilling = bestillingOpt.get();
            List<String> identer = new ArrayList<>();
            bestilling.getGruppe().getTestidenter().forEach(ident ->
                    ident.getBestillingProgress().forEach(progress -> {
                        if (progress.getBestillingId().equals(bestillingId)) {
                            identer.add(ident.getIdent());
                        }
                    })
            );
            List<RsOpenAmResponse> responser = new ArrayList<>(bestilling.getMiljoer().split(",").length);
            StringBuilder status = new StringBuilder();
            for (String miljoe : bestilling.getMiljoer().split(",")) {
                RsOpenAmResponse openAmResponse = openAmService.opprettIdenter(identer, miljoe);
                status.append(openAmResponse.getMessage());
                status.append(',');
                responser.add(openAmResponse);
            }
            bestilling.setOpenamSent(status.substring(0, status.length() - 1));
            bestillingRepository.save(bestilling);
            return responser;
        } else {
            throw new NotFoundException(format("BestillingId %s ble ikke funnet.", bestillingId));
        }
    }
}