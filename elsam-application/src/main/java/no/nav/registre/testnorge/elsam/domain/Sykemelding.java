package no.nav.registre.testnorge.elsam.domain;

import no.nav.helse.eiFellesformat.XMLEIFellesformat;
import no.nav.registre.testnorge.elsam.utils.JAXB;

public class Sykemelding {
    private final XMLEIFellesformat fellesformat;

    public Sykemelding(XMLEIFellesformat fellesformat) {
        this.fellesformat = fellesformat;
    }

    public String toXml() {
        return JAXB.marshallFellesformat(fellesformat);
    }
}
