package no.nav.testnav.libs.reactivecore.health;

import org.springframework.boot.actuate.health.Status;

import static org.springframework.boot.actuate.health.Health.status;

public class Health {
    public static String UP = "UP";
    public static String PAUSED = "PAUSED";
    public static String DISABLED = "DISABLED";
    public static String OUT_OF_SERVICE = "OUT_OF_SERVICE";
    public static String DOWN = "DOWN";

    public static org.springframework.boot.actuate.health.Health.Builder up() {
        return status(new Status(UP));
    }

    public static org.springframework.boot.actuate.health.Health.Builder paused() {
        return status(new Status(PAUSED));
    }

    public static org.springframework.boot.actuate.health.Health.Builder disabled() {
        return status(new Status(DISABLED));
    }

    public static org.springframework.boot.actuate.health.Health.Builder outOfService() {
        return status(new Status(OUT_OF_SERVICE));
    }

    public static org.springframework.boot.actuate.health.Health.Builder down() {
        return status(new Status(DOWN));
    }
}