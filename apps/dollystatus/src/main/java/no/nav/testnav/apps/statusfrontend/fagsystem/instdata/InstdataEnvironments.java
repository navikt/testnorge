package no.nav.testnav.apps.statusfrontend.fagsystem.instdata;

import java.util.List;

public record InstdataEnvironments(
        List<String> institusjonsoppholdEnvironments,
        List<String> kdiEnvironments
) {

    public InstdataEnvironments {
        institusjonsoppholdEnvironments = List.copyOf(institusjonsoppholdEnvironments);
        kdiEnvironments = List.copyOf(kdiEnvironments);
    }
}
