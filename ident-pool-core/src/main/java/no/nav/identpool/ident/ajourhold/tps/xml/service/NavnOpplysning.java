package no.nav.identpool.ident.ajourhold.tps.xml.service;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Getter;

import no.nav.identpool.ident.ajourhold.tps.xml.ServiceRutinenavn;
import no.nav.identpool.ident.ajourhold.tps.xml.TpsServiceRutine;

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