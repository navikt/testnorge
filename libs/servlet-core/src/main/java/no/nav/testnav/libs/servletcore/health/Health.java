package no.nav.testnav.libs.servletcore.health;

import lombok.experimental.UtilityClass;

@UtilityClass
class Health {
    static final String UP = "UP";
    static final String PAUSED = "PAUSED";
    static final String DISABLED = "DISABLED";
    static final String OUT_OF_SERVICE = "OUT_OF_SERVICE";
    static final String DOWN = "DOWN";
}