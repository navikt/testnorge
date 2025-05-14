package no.nav.dolly.libs.logging;

import lombok.experimental.UtilityClass;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@UtilityClass
public class Markers {

    /**
     * Just a default marker, for consistency. Should not be required in normal usage.
     */
    public static final Marker DEFAULT = MarkerFactory.getMarker("DEFAULT");

    /**
     * Use this marker to log to Team Logs. These are secure logs.
     */
    public static final Marker SECURE = MarkerFactory.getMarker("TEAM_LOGS");

}
