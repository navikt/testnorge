package no.nav.registre.frikort.domain.xml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class Samhandler {

    private final String type;
    private final String samhandlerid;
    private final long innsendingid = 0L;
    private final String innsendingstype;
    private final LocalDateTime datoMottattEkstern;
    private final LocalDateTime datoGenerert;
    private final String fornavn;
    private final EgenandelListe listeAvEgenandeler;

    @XmlAttribute
    public String getType() {
        return type;
    }

    @XmlElement
    public String getSamhandlerid() {
        return samhandlerid;
    }

    @XmlElement
    public long getInnsendingid() {
        return innsendingid;
    }

    @XmlElement
    public String getInnsendingstype() {
        return innsendingstype;
    }

    @XmlElement
    public String getDatoMottattEkstern() {
        return datoMottattEkstern == null ? "" : datoMottattEkstern.toString();
    }

    @XmlElement
    public String getDatoGenerert() {
        return datoGenerert == null ? "" : datoGenerert.toString();
    }

    @XmlElement
    public String getFornavn() {
        return fornavn;
    }

    @XmlElement
    public EgenandelListe getListeAvEgenandeler() {
        return listeAvEgenandeler;
    }

}
