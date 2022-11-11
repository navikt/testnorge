package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.repository.BestillingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoneService {

    private BestillingRepository bestillingRepository;
    private List<ClientRegister> implementasjoner;
    private ObjectMapper objectMapper;

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
