
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene for en bankkonto.
 *
 * <p>Java class for gironummerType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="gironummerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="gironummer" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="gironummerFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="gironummerTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="gironummerKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="gironummeRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gironummer", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "gironummer",
        "gironummerFraDato",
        "gironummerTilDato",
        "gironummerKilde",
        "gironummeRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gironummer {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String gironummer;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String gironummerFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String gironummerTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String gironummerKilde;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String gironummeRegistrertAv;

    /**
     * Gets the value of the gironummer property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getGironummer() {
        return gironummer;
    }

    /**
     * Sets the value of the gironummer property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setGironummer(String value) {
        this.gironummer = value;
    }

    /**
     * Gets the value of the gironummerFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getGironummerFraDato() {
        return gironummerFraDato;
    }

    /**
     * Sets the value of the gironummerFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setGironummerFraDato(String value) {
        this.gironummerFraDato = value;
    }

    /**
     * Gets the value of the gironummerTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getGironummerTilDato() {
        return gironummerTilDato;
    }

    /**
     * Sets the value of the gironummerTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setGironummerTilDato(String value) {
        this.gironummerTilDato = value;
    }

    /**
     * Gets the value of the gironummerKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getGironummerKilde() {
        return gironummerKilde;
    }

    /**
     * Sets the value of the gironummerKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setGironummerKilde(String value) {
        this.gironummerKilde = value;
    }

    /**
     * Gets the value of the gironummeRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getGironummeRegistrertAv() {
        return gironummeRegistrertAv;
    }

    /**
     * Sets the value of the gironummeRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setGironummeRegistrertAv(String value) {
        this.gironummeRegistrertAv = value;
    }

}
