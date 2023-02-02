package no.nav.dolly.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class EnvironmentsCrossConnect {
    
    public static List<String> crossConnect(List<String> environments, Type type) {
        var connected = new ArrayList<>(environments);
        return switch (type) {
            case Q4_TO_Q1 -> {
                // Krysskobling av miljøer Q4 -> Q1 etter ønske fra pensjon
                if (connected.contains("q4") && !connected.contains("q1")) {
                    connected.add("q1");
                }
                yield connected;
            }
            case Q1_AND_Q2 -> {
                if (connected.contains("q1") && !connected.contains("q2")) {
                    connected.add("q2");
                }
                if (connected.contains("q2") && !connected.contains("q1")) {
                    connected.add("q1");
                }
                yield connected;
            }
        };
    }
    
    public enum Type {
        Q4_TO_Q1,
        Q1_AND_Q2
    }

}
