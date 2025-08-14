package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@UtilityClass
@Slf4j
public class EnvironmentsCrossConnect {
    
    public static Set<String> crossConnect(Set<String> environments, Type type) {
        var connected = new HashSet<>(environments);
        return switch (type) {

            case Q1_AND_Q2 -> {
                if (connected.contains("q1")) {
                    log.info("Krysskobler miljøer Q1 -> Q2");
                    connected.add("q2");
                } else if (connected.contains("q2")) {
                    log.info("Krysskobler miljøer Q2 -> Q1");
                    connected.add("q1");
                }
                yield connected;
            }
        };
    }
    
    public enum Type {
        Q1_AND_Q2
    }
}
