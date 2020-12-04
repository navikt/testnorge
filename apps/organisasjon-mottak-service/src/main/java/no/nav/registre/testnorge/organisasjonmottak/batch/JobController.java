package no.nav.registre.testnorge.organisasjonmottak.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.organisasjonmottak.consumer.EregConsumer;

@EnableAsync
@Component
@RequiredArgsConstructor
public class JobController {
    private final EregConsumer eregConsumer;

    @Async
    @Scheduled(cron = "0 0 3 * * *")
    public void refreshEregT4Index() {
        eregConsumer.updateIndex("t4");
    }

}
