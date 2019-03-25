package no.nav.dolly.fasit;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FasitResourceWithUnmappedProperties {

    private String id;
    private String type;
    private String alias;
    private String created;
    private String updated;
    private boolean dodgy;
    private FasitResourceScope scope;
    private Object properties;
    private Map<String, String> links;
}
