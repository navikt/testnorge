package no.nav.registre.testnorge.sykemelding.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ApplicationInfo {
    private final String name;
    private final String version;

    @Builder
    private ApplicationInfo(String name, String version) {
        this.name = name;
        this.version = version;
    }
}
