package no.nav.registre.frikort.domain.xml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Samhandler {

    private Samhandlertypekode type;
    private String samhandlerid;
    private long innsendingid = 0L;
    private String innsendingstype;
    private LocalDateTime datoMottattEkstern;
    private LocalDateTime datoGenerert;
    private String fornavn;
    private EgenandelListe listeAvEgenandeler;

    @XmlAttribute
    public String getType() {
        return type.toString();
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
