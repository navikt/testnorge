package no.nav.registre.frikort.domain.xml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlRootElement(name=("egenandelsmelding"))
public class Egenandelsmelding {

    private final String xmlns="http://nav.no/frikort/common/domain/schema";
    private String avsender;
    private LocalDateTime datoSendt;
    private SamhandlerListe listeAvSamhandlere;

    @XmlAttribute
    public String getXmlns(){
        return xmlns;
    }

    @XmlAttribute
    public String getAvsender(){
        return avsender;
    }

    @XmlAttribute
    public String getDatoSendt(){
        return datoSendt.withNano(0).toString();
    }

    @XmlElement
    public SamhandlerListe getListeAvSamhandlere(){
         return listeAvSamhandlere;
    }

}
