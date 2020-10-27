package no.nav.registre.testnorge.identservice.testdata.environments.resourcetypes;

import lombok.Data;
import no.nav.registre.testnorge.identservice.testdata.environments.dao.FasitProperty;

@Data
public class FasitChannel implements FasitProperty {

    private String queueManager;
    private String name;
}
