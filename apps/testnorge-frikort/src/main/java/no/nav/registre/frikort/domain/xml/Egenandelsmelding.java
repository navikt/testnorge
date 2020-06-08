package no.nav.registre.frikort.domain.xml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@XmlRootElement(name = ("Egenandelsmelding"))
public class Egenandelsmelding {

    private static final String XMLNS = "http://nav.no/frikort/common/domain/schema";
    private final String avsender;
    private final LocalDateTime datoSendt;
    private final SamhandlerListe listeAvSamhandlere;

    @XmlAttribute
    public String getXMLNS() {
        return XMLNS;
    }

    @XmlAttribute
    public String getAvsender() {
        return avsender;
    }

    @XmlAttribute
    public String getDatoSendt() {
        return datoSendt == null ? "" : datoSendt.withNano(0).toString();
    }

    @XmlElement
    public SamhandlerListe getListeAvSamhandlere() {
        return listeAvSamhandlere;
    }

}
