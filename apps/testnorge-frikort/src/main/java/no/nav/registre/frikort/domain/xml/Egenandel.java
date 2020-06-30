package no.nav.registre.frikort.domain.xml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlElement;
import java.time.LocalDateTime;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Egenandel {

    private String egenandelskode;
    private long enkeltregningsid = 1234567890;
    private String enkeltregningsstatus;
    private int enkeltregningsnr = 22396;
    private double egenandelsats;
    private double egenandelsbelop;
    private LocalDateTime datoTjeneste;
    private LocalDateTime datoMottatt;
    private boolean betaltEgenandel;
    private Borger borger;

    @XmlElement
    public String getEgenandelskode() {
        return egenandelskode;
    }

    @XmlElement
    public double getEgenandelsats() {
        return egenandelsats;
    }

    @XmlElement
    public long getEnkeltregningsid() {
        return enkeltregningsid;
    }

    @XmlElement
    public String getEnkeltregningsstatus() {
        return enkeltregningsstatus;
    }

    @XmlElement
    public int getEnkeltregningsnr() {
        return enkeltregningsnr;
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
