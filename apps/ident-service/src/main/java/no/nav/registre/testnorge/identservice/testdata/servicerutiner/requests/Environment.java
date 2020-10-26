package no.nav.registre.testnorge.identservice.testdata.servicerutiner.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class Environment {
    
    private Set<String> environments;
    private boolean productionMode;
    private Map<String, Boolean> roles;
    
}
