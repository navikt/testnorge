package no.nav.testnav.apps.statusfrontend.fagsystem.instdata;

import java.util.List;

public record InstdataSearchRequest(String personident, List<String> environments) {

    public InstdataSearchRequest {
        environments = List.copyOf(environments);
    }
}
