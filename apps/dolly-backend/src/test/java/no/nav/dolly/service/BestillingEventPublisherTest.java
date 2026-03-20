package no.nav.dolly.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.Duration;

class BestillingEventPublisherTest {

    private BestillingEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new BestillingEventPublisher();
    }

    @Test
    void shouldPublishAndReceiveEvent() {
        var subscriber = publisher.subscribe(1L);

        StepVerifier.create(subscriber.take(1))
                .then(() -> publisher.publish(1L))
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void shouldFilterEventsByBestillingId() {
        var subscriber = publisher.subscribe(1L);

        StepVerifier.create(subscriber.take(1).timeout(Duration.ofSeconds(2)))
                .then(() -> {
                    publisher.publish(2L);
                    publisher.publish(3L);
                    publisher.publish(1L);
                })
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void shouldSupportMultipleSubscribers() {
        var subscriber1 = publisher.subscribe(1L);
        var subscriber2 = publisher.subscribe(1L);

        StepVerifier.create(subscriber1.take(1).mergeWith(subscriber2.take(1)))
                .then(() -> publisher.publish(1L))
                .expectNext(1L, 1L)
                .verifyComplete();
    }

    @Test
    void shouldNotEmitForNonMatchingIds() {
        var subscriber = publisher.subscribe(1L);

        StepVerifier.create(subscriber.take(Duration.ofMillis(200)))
                .then(() -> {
                    publisher.publish(2L);
                    publisher.publish(3L);
                })
                .verifyComplete();
    }

    @Test
    void shouldPublishAndReceiveOrgEvent() {
        var subscriber = publisher.subscribeOrg(1L);

        StepVerifier.create(subscriber.take(1))
                .then(() -> publisher.publishOrg(1L))
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void shouldFilterOrgEventsByBestillingId() {
        var subscriber = publisher.subscribeOrg(1L);

        StepVerifier.create(subscriber.take(1).timeout(Duration.ofSeconds(2)))
                .then(() -> {
                    publisher.publishOrg(2L);
                    publisher.publishOrg(3L);
                    publisher.publishOrg(1L);
                })
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void shouldKeepOrgAndRegularSinksIndependent() {
        var regularSubscriber = publisher.subscribe(1L);
        var orgSubscriber = publisher.subscribeOrg(1L);

        StepVerifier.create(regularSubscriber.take(1))
                .then(() -> {
                    publisher.publishOrg(1L);
                    publisher.publish(1L);
                })
                .expectNext(1L)
                .verifyComplete();

        StepVerifier.create(orgSubscriber.take(1))
                .then(() -> {
                    publisher.publish(1L);
                    publisher.publishOrg(1L);
                })
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void shouldHandleBackpressureOverflow() {
        var subscriber = publisher.subscribe(1L);

        for (long i = 0; i < 300; i++) {
            publisher.publish(1L);
        }

        StepVerifier.create(subscriber.take(1).timeout(Duration.ofSeconds(2)))
                .then(() -> publisher.publish(1L))
                .expectNext(1L)
                .verifyComplete();
    }
}
