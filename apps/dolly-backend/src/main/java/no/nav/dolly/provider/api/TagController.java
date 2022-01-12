package no.nav.dolly.provider.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsOpenAmResponse;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.service.OpenAmService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static no.nav.dolly.config.CachingConfig.CACHE_BESTILLING;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/tags", produces = MediaType.APPLICATION_JSON_VALUE)
public class TagController {

    private final OpenAmService openAmService;
    private final BestillingRepository bestillingRepository;

    @CacheEvict(value = CACHE_BESTILLING, allEntries = true)
    @PostMapping("/gruppe/{gruppeId}")
    @Transactional
    @Operation(description = "Opprett identer i miljøer for identer tilhørende en Bestillings Testgruppe")
    public List<RsOpenAmResponse> sendBestillingTilOpenAm(@RequestParam Long gruppeId) {
        Optional<List<Testgruppe.Tags>> bestillingOpt = bestillingRepository.findById(gruppeId);
        if (bestillingOpt.isPresent()) {
            Bestilling bestilling = bestillingOpt.get();
            List<String> identer = new ArrayList<>();
            bestilling.getGruppe().getTestidenter().forEach(ident ->
                    ident.getBestillingProgress().forEach(progress -> {
                        if (progress.getBestilling().getId().equals(gruppeId)) {
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
            throw new NotFoundException(format("BestillingId %s ble ikke funnet.", gruppeId));
        }
    }
}