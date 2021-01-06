package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingStatusConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingStatusConsumer.ItemDto;
import no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse.Status;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingStatusConsumer.ItemStatus.COMPLETED;
import static no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingStatusConsumer.ItemStatus.ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployStatusService {

    private static final long SLEEP_TIME = 1000L;
    private static final long MAX_ITERATIONS = 60 * 15;

    private final OrganisasjonBestillingStatusConsumer bestillingStatusConsumer;

    private static boolean isDone(String uuid, List<ItemDto> statusTotal) {

        log.info("Deploystatus for {}, {}", uuid, statusTotal.stream().map(ItemDto::toString).toString());
        return !statusTotal.isEmpty() && (statusTotal.stream().allMatch(status -> status.getStatus() == COMPLETED) ||
                statusTotal.stream().anyMatch(status -> status.getStatus() == ERROR));
    }

    @SneakyThrows
    public Status checkStatus(String uuid) {

        List<ItemDto> statusTotal = emptyList();
        var attemptsLeft = MAX_ITERATIONS;

        while (attemptsLeft-- > 0 && !isDone(uuid, statusTotal)) {
            Thread.sleep(SLEEP_TIME);
            statusTotal = bestillingStatusConsumer.getBestillingStatus(uuid);
        }

        return !statusTotal.isEmpty() && statusTotal.stream().allMatch(status -> status.getStatus() == COMPLETED) ?
                Status.OK : Status.ERROR;
    }
}
