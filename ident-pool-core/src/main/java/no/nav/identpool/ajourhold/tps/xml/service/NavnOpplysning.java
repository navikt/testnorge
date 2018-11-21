package no.nav.identpool.ajourhold.tps.xml.service;

import java.util.Collection;
import java.util.List;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;
import no.nav.identpool.ajourhold.tps.xml.ServiceRutinenavn;
import no.nav.identpool.ajourhold.tps.xml.TpsServiceRutine;

//TODO Denne ser ut til å skulle være en service basert på pakkenavn, men er ikke det. Blir brukt med new i IdentMQService
@Getter
public class NavnOpplysning extends TpsServiceRutine {

    @JacksonXmlProperty(localName = "antallFnr")
    private final int antallFnr;

    @JacksonXmlElementWrapper(localName = "nFnr")
    @JacksonXmlProperty(localName = "fnr")
    private final Collection<String> fnr;

    public NavnOpplysning(List<String> fnrs) {
        super(ServiceRutinenavn.HENT_NAVNOPPLYSNINGER, "A", "1");
        this.antallFnr = fnrs.size();
        this.fnr = fnrs;
    }
}