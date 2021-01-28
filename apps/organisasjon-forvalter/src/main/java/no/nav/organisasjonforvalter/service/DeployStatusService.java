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

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingStatusConsumer.ItemStatus.COMPLETED;
import static no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingStatusConsumer.ItemStatus.ERROR;
import static no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingStatusConsumer.ItemStatus.FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployStatusService {

    private static final long SLEEP_TIME_MS = 1000L;
    private static final long MAX_ITERATIONS = 60 * 15;

    private final OrganisasjonBestillingStatusConsumer bestillingStatusConsumer;

    private static boolean isDone(List<ItemDto> statusTotal, Long lastUpdate, Long maxTimeWithoutUpdate) {

        var elapsedTime = System.currentTimeMillis() - lastUpdate;
        if (elapsedTime > maxTimeWithoutUpdate) {
            log.warn(format("Status ikke oppdatert på %d ms. Deploy avbrytes.", maxTimeWithoutUpdate));
        }

        return !statusTotal.isEmpty() && (isOK(statusTotal, lastUpdate, maxTimeWithoutUpdate) ||
                statusTotal.stream().anyMatch(status ->
                        status.getStatus() == ERROR ||
                                status.getStatus() == FAILED)) ||
                elapsedTime > maxTimeWithoutUpdate;
    }

    private static boolean isOK(List<ItemDto> items, Long lastUpdate, long maxTimeWithoutUpdate) {

        return items.stream().allMatch(status -> status.getStatus() == COMPLETED) &&
                System.currentTimeMillis() - lastUpdate < maxTimeWithoutUpdate;
    }

    @SneakyThrows
    public Status checkStatus(String uuid, long maxTimeWithoutUpdate) {

        List<ItemDto> statusTotal = emptyList();
        var attemptsLeft = MAX_ITERATIONS;
        var statusLength = 0;
        var lastUpdate = System.currentTimeMillis();

        while (attemptsLeft > 0 && !isDone(statusTotal, lastUpdate, maxTimeWithoutUpdate)) {

            Thread.sleep(SLEEP_TIME_MS);
            statusTotal = bestillingStatusConsumer.getBestillingStatus(uuid);

            if (statusLength != statusTotal.size()) {
                statusLength = statusTotal.size();
                lastUpdate = System.currentTimeMillis();
            }
            if (attemptsLeft-- % 5 == 0 || isDone(statusTotal, lastUpdate, maxTimeWithoutUpdate)) {
                log.info("Deploystatus for {}, {}, time elapsed {} ms",
                        uuid, statusTotal.stream()
                                .map(ItemDto::toString)
                                .collect(Collectors.joining(", ")),
                        (MAX_ITERATIONS - attemptsLeft) * SLEEP_TIME_MS);
            }
        }

        return !statusTotal.isEmpty() && isOK(statusTotal, lastUpdate, maxTimeWithoutUpdate) ?
                Status.OK : Status.ERROR;
    }
}
