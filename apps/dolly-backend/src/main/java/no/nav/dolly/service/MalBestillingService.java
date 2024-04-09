package no.nav.dolly.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.repository.BestillingMalRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.util.CurrentAuthentication.getUserId;

@Slf4j
@Service
@RequiredArgsConstructor
public class MalBestillingService {

    private static final String EMPTY_JSON = "{}";

    private final BestillingRepository bestillingRepository;
    private final BestillingMalRepository malBestillingRepository;
    private final MapperFacade mapperFacade;
    private final ObjectMapper objectMapper;
    private final BrukerService brukerService;
    private final GetUserInfo getUserInfo;

    @Transactional
    public BestillingMal createFromIdent(String ident, String name) {

        var bruker = brukerService.fetchOrCreateBruker(getUserId(getUserInfo));

        var malBestillinger = malBestillingRepository.findByBruker(bruker);
        if (malBestillinger.stream().anyMatch(malbestilling -> name.equals(malbestilling.getMalNavn()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Navn \"%s\" eksisterer allerede.");
        }

        var bestillinger = bestillingRepository.findBestillingerByIdent(ident);
        var aggregertRequest = new RsDollyBestillingRequest();

        bestillinger.stream()
                .filter(bestilling -> nonNull(bestilling.getBestKriterier()) &&
                        !EMPTY_JSON.equals(bestilling.getBestKriterier()))
                .filter(bestilling -> isNull(bestilling.getOpprettetFraGruppeId()) &&
                        isNull(bestilling.getGjenopprettetFraIdent()) &&
                        isNull(bestilling.getOpprettetFraId()))
                .map(bestilling -> {
                    try {
                        return objectMapper.readValue(bestilling.getBestKriterier(), RsDollyBestillingRequest.class);
                    } catch (JsonProcessingException e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
                    }
                })
                .peek(dollyBestilling -> log.info("dollyBestilling: {}", dollyBestilling))
                .forEach(dollyBestilling -> mapperFacade.map(dollyBestilling, aggregertRequest));

//        nyMal.setMalNavn(name);
//        return malBestillingRepository.save(nyMal);
        return null;
    }
}
