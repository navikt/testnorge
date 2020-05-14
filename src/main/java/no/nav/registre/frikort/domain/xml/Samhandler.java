package no.nav.registre.frikort.domain.xml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;
import java.time.Month;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Samhandler {

    private String type;
    private final long samhandlerid = 18015441409L;
    private final long innsendingid = 100000888955802L;
    private final String innsendingstype = "LOM";
    private final LocalDateTime datoMottattEkstern = LocalDateTime.of(2018, Month.JANUARY, 01, 00, 00, 00);
    private final LocalDateTime datoGenerert = LocalDateTime.of(2018, Month.JANUARY, 01, 00, 00, 00);
    private final String fornavn = "Ola Samhandler";
    private EgenandelListe listeAvEgenandeler;


    @XmlAttribute
    public String getType(){
        return type;
    }
    @XmlElement
    public long getSamhandlerid(){
        return samhandlerid;
    }
    @XmlElement
    public long getInnsendingid(){
        return innsendingid;
    }
    @XmlElement
    public String getInnsendingstype(){
        return innsendingstype;
    }
    @XmlElement
    public String getDatoMottattEkstern(){
        return datoMottattEkstern.toString();
    }
    @XmlElement
    public String getDatoGenerert(){
        return datoGenerert.toString();
    }
    @XmlElement
    public String getFornavn(){
        return fornavn;
    }
    @XmlElement
    public EgenandelListe getListeAvEgenandeler(){
        return listeAvEgenandeler;
    }

}
