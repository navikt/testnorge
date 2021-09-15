package no.nav.testnav.libs.reactivesessionsecurity.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerProperties {
    private String url;
    private String cluster;
    private String name;
    private String namespace;
}
