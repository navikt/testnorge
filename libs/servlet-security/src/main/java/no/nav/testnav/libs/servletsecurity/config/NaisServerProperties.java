package no.nav.testnav.libs.servletsecurity.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaisServerProperties implements Scopeable {
    private String url;
    private String cluster;
    private String name;
    private String namespace;

    @Override
    public String toScope() {
        return "api://" + cluster + "." +  namespace + "." + name + "/.default";
    }
}
