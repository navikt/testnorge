package no.nav.organisasjonforvalter.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingStatusConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingStatusConsumer.ItemDto;
import no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse.EnvStatus;
import no.nav.organisasjonforvalter.provider.rs.responses.DeployResponse.Status;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingStatusConsumer.ItemStatus.COMPLETED;
import static no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingStatusConsumer.ItemStatus.ERROR;
import static no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingStatusConsumer.ItemStatus.FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployStatusService {

    private static final long MAX_TIMEOUT_NO_PROGRESS = 1000L * 60 * 3;
    private static final long SLEEP_TIME_MS = 10000L;

    private final OrganisasjonBestillingStatusConsumer bestillingStatusConsumer;

    private static boolean isDone(List<ItemDto> items) {

        return isOk(items) || isError(items);
    }

    private static boolean isOk(List<ItemDto> items) {

        return !items.isEmpty() && items.stream().allMatch(status -> status.getStatus() == COMPLETED);
    }

    private static boolean isError(List<ItemDto> items) {

        return !items.isEmpty() && items.stream().anyMatch(status ->
                status.getStatus() == ERROR || status.getStatus() == FAILED);
    }

    private static String getReadableTime(Long millis) {

        long tempSec = millis / 1000;
        long sec = tempSec % 60;
        long min = (tempSec / 60) % 60;

        return String.format("%d minutter og %d sekunder", min, sec);
    }

    @SneakyThrows
    public List<EnvStatus> awaitDeployedDone(List<DeployEntry> request) {

        var startTime = System.currentTimeMillis();
        AtomicLong lastUpdate = new AtomicLong(System.currentTimeMillis());

        while (!request.stream().allMatch(status -> isDone(status.getLastStatus())) &&
                System.currentTimeMillis() - lastUpdate.get() < MAX_TIMEOUT_NO_PROGRESS) {

            Thread.sleep(SLEEP_TIME_MS);
            request.forEach(entry -> {
                if (!isDone(entry.getLastStatus())) {
                    List<ItemDto> bestStatus;
                    try {
                        bestStatus = bestillingStatusConsumer.getBestillingStatus(entry.getUuid());
                        if (!(entry.getLastStatus().containsAll(bestStatus) &&
                                bestStatus.containsAll(entry.getLastStatus()))) {
                            lastUpdate.set(System.currentTimeMillis());
                        }
                        entry.setLastStatus(bestStatus);
                        log.info("Deploystatus for {}, status {}, env {}, elapsed {} ms",
                                entry.getUuid(), entry.getLastStatus().stream()
                                        .map(ItemDto::toString)
                                        .collect(Collectors.joining(", ")),
                                entry.getEnvironment(),
                                System.currentTimeMillis() - startTime);
                    } catch (HttpClientErrorException e) {
                        // Silently discard
                    }
                }
            });
        }

        return request.stream()
                .map(entry -> EnvStatus.builder()
                        .uuid(entry.getUuid())
                        .environment(entry.getEnvironment())
                        .status(isOk(entry.getLastStatus()) ? Status.OK : Status.ERROR)
                        .details(isOk(entry.getLastStatus()) ? null :
                                isError(entry.getLastStatus()) ? "Oppretting til miljø feilet, se teknisk logg!" :
                                        "Tidsavbrudd, ingen fremdrift i oppretting etter " +
                                                getReadableTime(System.currentTimeMillis() - startTime))
                        .build())
                .collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeployEntry {

        private String environment;
        private String uuid;
        private List<ItemDto> lastStatus;

        public List<ItemDto> getLastStatus() {
            if (isNull(lastStatus)) {
                lastStatus = new ArrayList<>();
            }
            return lastStatus;
        }
    }
}
