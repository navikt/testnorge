package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoneService {

    private final BestillingRepository bestillingRepository;
    private final List<ClientRegister> implementasjoner;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public boolean isDone(Long bestillingId) {

        var bestilling = bestillingRepository.findById(bestillingId);

        if (bestilling.isEmpty()) {
            return false;
        }

        var kriterier = objectMapper.readValue(bestilling.get().getBestKriterier(), RsDollyBestilling.class);
        return bestilling.get().getAntallIdenter() == bestilling.get().getProgresser().size() &&
                implementasjoner.stream()
                        .allMatch(client -> client.isDone(kriterier, bestilling.get()));
    }
}
