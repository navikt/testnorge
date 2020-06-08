package no.nav.registre.frikort.domain.xml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;
import java.time.Month;

@NoArgsConstructor(force=true)
@AllArgsConstructor
@Builder
public class Samhandler {

    private final String type;
    private static final long SAMHANDLERID = 0L;
    private static final long INNSENDINGID = 0L;
    private static final String INNSENDINGSTYPE = "LOM";
    private final LocalDateTime datoMottattEkstern = LocalDateTime.of(2018, Month.JANUARY, 01, 00, 00, 00);
    private final LocalDateTime datoGenerert = LocalDateTime.of(2018, Month.JANUARY, 01, 00, 00, 00);
    private static final String FORNAVN = "Ikke Satt";
    private final EgenandelListe listeAvEgenandeler;

    @XmlAttribute
    public String getType(){
        return type;
    }
    @XmlElement
    public long getSamhandlerid(){
        return SAMHANDLERID;
    }
    @XmlElement
    public long getInnsendingid(){
        return INNSENDINGID;
    }
    @XmlElement
    public String getInnsendingstype(){
        return INNSENDINGSTYPE;
    }
    @XmlElement
    public String getDatoMottattEkstern(){
        return datoMottattEkstern == null ? "" : datoMottattEkstern.toString();
    }
    @XmlElement
    public String getDatoGenerert(){
        return datoGenerert == null ? "" : datoGenerert.toString();
    }
    @XmlElement
    public String getFornavn(){
        return FORNAVN;
    }
    @XmlElement
    public EgenandelListe getListeAvEgenandeler(){
        return listeAvEgenandeler;
    }

}
