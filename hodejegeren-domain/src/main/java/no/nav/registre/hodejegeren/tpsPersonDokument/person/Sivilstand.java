
package no.nav.registre.hodejegeren.tpsPersonDokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene i sivilstand
 *
 * <p>Java class for sivilstandType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="sivilstandType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sivilstand" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sivilstandFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sivilstandTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sivilstandKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sivilstandRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "sivilstand", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "sivilstand",
        "sivilstandFraDato",
        "sivilstandTilDato",
        "sivilstandKilde",
        "sivilstandRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sivilstand {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String sivilstand;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String sivilstandFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String sivilstandTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String sivilstandKilde;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String sivilstandRegistrertAv;

    /**
     * Gets the value of the sivilstand property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSivilstand() {
        return sivilstand;
    }

    /**
     * Sets the value of the sivilstand property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSivilstand(String value) {
        this.sivilstand = value;
    }

    /**
     * Gets the value of the sivilstandFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSivilstandFraDato() {
        return sivilstandFraDato;
    }

    /**
     * Sets the value of the sivilstandFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSivilstandFraDato(String value) {
        this.sivilstandFraDato = value;
    }

    /**
     * Gets the value of the sivilstandTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSivilstandTilDato() {
        return sivilstandTilDato;
    }

    /**
     * Sets the value of the sivilstandTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSivilstandTilDato(String value) {
        this.sivilstandTilDato = value;
    }

    /**
     * Gets the value of the sivilstandKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSivilstandKilde() {
        return sivilstandKilde;
    }

    /**
     * Sets the value of the sivilstandKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSivilstandKilde(String value) {
        this.sivilstandKilde = value;
    }

    /**
     * Gets the value of the sivilstandRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSivilstandRegistrertAv() {
        return sivilstandRegistrertAv;
    }

    /**
     * Sets the value of the sivilstandRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSivilstandRegistrertAv(String value) {
        this.sivilstandRegistrertAv = value;
    }

}
