package no.nav.registre.frikort.domain.xml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class Egenandel {

    private final String egenandelskode;
    private static final long ENKELTREGNINGSID = 1234567890;
    private final String enkeltregningsstatus;
    private static final int ENKELTREGNINGSNR = 22396;
    private final double egenandelsats;
    private final double egenandelsbelop;
    private final LocalDateTime datoTjeneste;
    private final LocalDateTime datoMottatt;
    private final boolean betaltEgenandel;
    private final Borger borger;


    @XmlElement
    public String getEgenandelskode() {
        return egenandelskode;
    }

    @XmlElement
    public double getEgenandelsats(){
        return egenandelsats;
    }

    @XmlElement
    public long getENKELTREGNINGSID() {
        return ENKELTREGNINGSID;
    }

    @XmlElement
    public String getEnkeltregningsstatus() {
        return enkeltregningsstatus;
    }

    @XmlElement
    public int getENKELTREGNINGSNR() {
        return ENKELTREGNINGSNR;
    }

    @XmlElement
    public double getEgenandelsbelop() {
        return egenandelsbelop;
    }

    @XmlElement
    public String getDatoMottatt() {
        return datoMottatt == null ? "" : datoMottatt.toString();
    }

    @XmlElement
    public String getDatoTjeneste() {
        return datoTjeneste == null ? "" : datoTjeneste.toString();
    }

    @XmlElement
    public boolean isBetaltEgenandel() {
        return betaltEgenandel;
    }

    @XmlElement
    public Borger getBorger() {
        return borger;
    }
}
