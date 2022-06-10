package no.nav.testnav.libs.reactivecore.health;

import lombok.experimental.UtilityClass;
import org.springframework.boot.actuate.health.Status;

import static org.springframework.boot.actuate.health.Health.status;

@UtilityClass
public class Health {

    public static final String UP = "UP";
    public static final String PAUSED = "PAUSED";
    public static final String DISABLED = "DISABLED";
    public static final String OUT_OF_SERVICE = "OUT_OF_SERVICE";
    public static final String DOWN = "DOWN";
}