
package no.nav.registre.hodejegeren.tpsPersonDokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for tiltakType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tiltakType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tiltak" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tiltakFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tiltakTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tiltakKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tiltakRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tiltak", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "tiltak",
        "tiltakFraDato",
        "tiltakTilDato",
        "tiltakKilde",
        "tiltakRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tiltak {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tiltak;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tiltakFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tiltakTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tiltakKilde;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String tiltakRegistrertAv;

    /**
     * Gets the value of the tiltak property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTiltak() {
        return tiltak;
    }

    /**
     * Sets the value of the tiltak property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTiltak(String value) {
        this.tiltak = value;
    }

    /**
     * Gets the value of the tiltakFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTiltakFraDato() {
        return tiltakFraDato;
    }

    /**
     * Sets the value of the tiltakFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTiltakFraDato(String value) {
        this.tiltakFraDato = value;
    }

    /**
     * Gets the value of the tiltakTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTiltakTilDato() {
        return tiltakTilDato;
    }

    /**
     * Sets the value of the tiltakTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTiltakTilDato(String value) {
        this.tiltakTilDato = value;
    }

    /**
     * Gets the value of the tiltakKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTiltakKilde() {
        return tiltakKilde;
    }

    /**
     * Sets the value of the tiltakKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTiltakKilde(String value) {
        this.tiltakKilde = value;
    }

    /**
     * Gets the value of the tiltakRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTiltakRegistrertAv() {
        return tiltakRegistrertAv;
    }

    /**
     * Sets the value of the tiltakRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTiltakRegistrertAv(String value) {
        this.tiltakRegistrertAv = value;
    }

}
