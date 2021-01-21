package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingStatusConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingStatusConsumer.ItemDto;
import no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse.Status;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingStatusConsumer.ItemStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployStatusService {

    private static final long SLEEP_TIME = 1000L;
    private static final long MAX_ITERATIONS = 60 * 10;

    private final OrganisasjonBestillingStatusConsumer bestillingStatusConsumer;

    private static boolean isDone(List<ItemDto> statusTotal) {

        return !statusTotal.isEmpty() && (isOK(statusTotal) ||
                statusTotal.stream().anyMatch(status ->
                        status.getStatus() == ERROR ||
                        status.getStatus() == FAILED));
    }

    private static boolean isOK(List<ItemDto> items) {

        return items.stream().allMatch(status -> status.getStatus() == COMPLETED);
    }

    @SneakyThrows
    public Status checkStatus(String uuid) {

        List<ItemDto> statusTotal = emptyList();
        var attemptsLeft = MAX_ITERATIONS;

        while (attemptsLeft > 0 && !isDone(statusTotal)) {

            Thread.sleep(SLEEP_TIME);
            statusTotal = bestillingStatusConsumer.getBestillingStatus(uuid);

            if (attemptsLeft-- % 5 == 0 || isDone(statusTotal)) {
                log.info("Deploystatus for {}, {}, time elapsed {} ms",
                        uuid, statusTotal.stream()
                        .map(ItemDto::toString)
                        .collect(Collectors.joining(", ")),
                        (MAX_ITERATIONS - attemptsLeft) * SLEEP_TIME);
            }
        }

        return !statusTotal.isEmpty() && isOK(statusTotal) ?
                Status.OK : Status.ERROR;
    }
}
