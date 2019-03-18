package no.nav.dolly.fasit;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
