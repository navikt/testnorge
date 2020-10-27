package no.nav.registre.testnorge.identservice.testdata.environments.dao;

import lombok.Data;

@Data
public class FasitResourceWithUnmappedProperties {

    private String id;
    private String type;
    private String alias;
    private String created;
    private String updated;
    private boolean dodgy;
    private FasitResourceScope scope;
    private Object properties;


}
