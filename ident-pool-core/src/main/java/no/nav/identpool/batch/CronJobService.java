package no.nav.identpool.batch;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import no.nav.identpool.batch.mq.factory.MessageQueueFactory;

@Component
@RequiredArgsConstructor
public class CronJobService {
    private final MessageQueueFactory queueFactory;
    private static final String M_SECOND = "59";
    private static final String M_MINUTE = "**";
    private static final String M_HOUR = "**";
    private static final String MINUTTLIG_CRON_EXPRESSION = M_SECOND + " " + M_MINUTE + " " + M_HOUR + " * * *";

    private static final String D_SECOND = "35";
    private static final String D_MINUTE = "53";
    private static final String D_HOUR = "10";
    private static final String DAGLIG_CRON_EXPRESSION = D_SECOND + " " + D_MINUTE + " " + D_HOUR + " * * *";
}