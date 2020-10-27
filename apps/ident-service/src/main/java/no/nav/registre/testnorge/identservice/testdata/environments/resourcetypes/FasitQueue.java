package no.nav.registre.testnorge.identservice.testdata.environments.resourcetypes;

import lombok.Data;
import no.nav.registre.testnorge.identservice.testdata.environments.dao.FasitProperty;

@Data
public class FasitQueue implements FasitProperty {

    private String queueName;
    private String queueManager;

}
