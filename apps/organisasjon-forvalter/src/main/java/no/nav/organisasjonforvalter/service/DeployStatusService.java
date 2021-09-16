package no.nav.organisasjonforvalter.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.consumer.OrganisasjonBestillingConsumer;
import no.nav.organisasjonforvalter.dto.responses.BestillingStatus;
import no.nav.organisasjonforvalter.dto.responses.DeployResponse.EnvStatus;
import no.nav.organisasjonforvalter.dto.responses.DeployResponse.Status;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static no.nav.organisasjonforvalter.dto.responses.BestillingStatus.ItemDto.ItemStatus.COMPLETED;
import static no.nav.organisasjonforvalter.dto.responses.BestillingStatus.ItemDto.ItemStatus.ERROR;
import static no.nav.organisasjonforvalter.dto.responses.BestillingStatus.ItemDto.ItemStatus.FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployStatusService {

    private static final long MAX_TIMEOUT_NO_PROGRESS = 1000L * 60 * 3;
    private static final long SLEEP_TIME_MS = 10000L;

    private final OrganisasjonBestillingConsumer bestillingStatusConsumer;

    private static boolean isDone(List<BestillingStatus.ItemDto> items) {

        return isOk(items) || isError(items);
    }

    private static boolean isOk(List<BestillingStatus.ItemDto> items) {

        return !items.isEmpty() && items.stream().allMatch(status -> status.getStatus() == COMPLETED);
    }

    private static boolean isError(List<BestillingStatus.ItemDto> items) {

        return !items.isEmpty() && items.stream().anyMatch(status ->
                status.getStatus() == ERROR || status.getStatus() == FAILED);
    }

    private static String getReadableTime(Long millis) {

        long tempSec = millis / 1000;
        long min = tempSec / 60;

        return String.format("%d minutter", min);
    }

    private static boolean isStatusChanged(DeployEntry entry, List<BestillingStatus.ItemDto> bestStatus) {
        return !(entry.getLastStatus().containsAll(bestStatus) &&
                bestStatus.containsAll(entry.getLastStatus()));
    }

    private static List<EnvStatus> getEnvStatuses(List<DeployEntry> request, long startTime) {
        return request.stream()
                .map(entry -> EnvStatus.builder()
                        .uuid(entry.getUuid())
                        .environment(entry.getEnvironment())
                        .status(isOk(entry.getLastStatus()) ? Status.OK : Status.ERROR)
                        .details(isOk(entry.getLastStatus()) ? null :
                                getErrorDetails(startTime, entry))
                        .build())
                .collect(Collectors.toList());
    }

    private static String getErrorDetails(long startTime, DeployEntry entry) {
        return isError(entry.getLastStatus()) ? "Oppretting til miljø feilet, se teknisk logg!" :
                String.format("Tidsavbrudd, ingen fremdrift etter %s. " +
                                "Oppretting er forventet ferdigstilt senere.",
                        getReadableTime(System.currentTimeMillis() - startTime));
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
                    List<BestillingStatus.ItemDto> bestStatus =
                            bestillingStatusConsumer.getBestillingStatus(entry.getUuid()).getItemDtos();
                    if (isStatusChanged(entry, bestStatus)) {
                        lastUpdate.set(System.currentTimeMillis());
                    }
                    entry.setLastStatus(bestStatus);
                    log.info("Deploystatus for {}, status {}, env {}, elapsed {} ms",
                            entry.getUuid(), entry.getLastStatus().stream()
                                    .map(BestillingStatus.ItemDto::toString)
                                    .collect(Collectors.joining(", ")),
                            entry.getEnvironment(),
                            System.currentTimeMillis() - startTime);
                }
            });
        }

        return getEnvStatuses(request, startTime);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeployEntry {

        private String environment;
        private String uuid;
        private List<BestillingStatus.ItemDto> lastStatus;

        public List<BestillingStatus.ItemDto> getLastStatus() {
            if (isNull(lastStatus)) {
                lastStatus = new ArrayList<>();
            }
            return lastStatus;
        }
    }
}
