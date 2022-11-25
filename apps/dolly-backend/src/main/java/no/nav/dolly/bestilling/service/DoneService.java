package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoneService {

    public static final Long MAX_AWAIT_CYCLES = 100L;
    private static final Long TIME_STORED_MS = 3 * 60 * 1000L;
    private static final Map<String, Long> pdlIdenter = new HashMap<>();

    private final BestillingRepository bestillingRepository;
    private final List<ClientRegister> implementasjoner;
    private final ObjectMapper objectMapper;
    private final PersonServiceConsumer personServiceConsumer;
    private final TransactionHelperService transactionHelperService;

    public boolean isDone(BestillingProgress progress) {

        transactionHelperService.persist(progress);
        var done = isDone(progress.getBestilling().getId());
        if (done) {
            transactionHelperService.oppdaterBestillingFerdig(progress.getBestilling());
        }
        return done;
    }

    public Function<String, Boolean> test() {
notifyAll();
        return var isAvail = false;
        var loops = DoneService.MAX_AWAIT_CYCLES;
        while (loops-- > 0 && !(isAvail = doneService.isPdlSync(dollyPerson.getHovedperson())))
    }

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

    public void persist(BestillingProgress progress) {

        transactionHelperService.persist(progress);
    }

    @Synchronized
    public Boolean isPdlSync(String ident) {

        if (!pdlIdenter.containsKey(ident) || System.currentTimeMillis() - pdlIdenter.get(ident) < TIME_STORED_MS) {

            pdlIdenter.remove(ident);

            try {
                Thread.sleep(100);

            } catch (InterruptedException e) {

                // ingenting
            }

            if (personServiceConsumer.isPerson(ident)) {

                pdlIdenter.put(ident,  System.currentTimeMillis());
            }
        }

        return pdlIdenter.containsKey(ident);
    }
}
