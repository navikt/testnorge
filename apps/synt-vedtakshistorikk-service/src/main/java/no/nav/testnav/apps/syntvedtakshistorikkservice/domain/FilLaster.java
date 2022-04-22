package no.nav.testnav.apps.syntvedtakshistorikkservice.domain;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
public class FilLaster {

    private static FilLaster instans;

    public static FilLaster instans() {
        if (instans == null) {
            instans = new FilLaster();
        }
        return instans;
    }

    public InputStream lastRessurs(String path) {
        return FilLaster.class.getClassLoader().getResourceAsStream(path);
    }
}