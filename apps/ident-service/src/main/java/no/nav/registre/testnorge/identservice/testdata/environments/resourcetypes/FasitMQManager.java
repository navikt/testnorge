package no.nav.registre.testnorge.identservice.testdata.environments.resourcetypes;

import lombok.Data;
import no.nav.registre.testnorge.identservice.testdata.environments.dao.FasitProperty;

@Data
public class FasitMQManager implements FasitProperty {

    private String name;
    private String hostname;
    private String port;
}
