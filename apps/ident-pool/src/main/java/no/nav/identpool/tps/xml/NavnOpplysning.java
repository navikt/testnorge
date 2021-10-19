package no.nav.identpool.tps.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

@Getter
public class NavnOpplysning extends TpsServiceRutine {

    @JacksonXmlProperty(localName = "antallFnr")
    private final int antallFnr;

    @JacksonXmlElementWrapper(localName = "nFnr")
    @JacksonXmlProperty(localName = "fnr")
    private final Collection<String> fnr;

    public NavnOpplysning(List<String> fnrs, AksjonKode aksjonKode) {
        super(ServiceRutinenavn.HENT_NAVNOPPLYSNINGER, "A", aksjonKode.getKode());
        this.antallFnr = fnrs.size();
        this.fnr = fnrs;
    }
}