package no.nav.testnav.apps.statusfrontend.functionaltest;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

final class MutableClock extends Clock {

    private Instant instant;

    MutableClock(Instant instant) {
        this.instant = instant;
    }

    void setInstant(Instant instant) {
        this.instant = instant;
    }

    @Override
    public ZoneId getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return this;
    }

    @Override
    public Instant instant() {
        return instant;
    }
}
