package no.nav.dolly.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class EnvironmentsCrossConnect {

    public List<String> crossConnect(List<String> environments) {

        // Krysskobling av miljøer Q4 -> Q1 etter ønske fra pensjon
        var miljoer = new ArrayList(environments);
        if (miljoer.contains("q4") && !miljoer.contains("q1")) {
            miljoer.add("q1");
        }
        return miljoer;
    }
}
