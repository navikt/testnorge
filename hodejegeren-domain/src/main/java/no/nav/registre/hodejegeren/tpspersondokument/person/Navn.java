
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene for navn.
 *
 * <p>Java class for navnType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="navnType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="forkortetNavn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="slektsNavn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="forNavn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="mellomNavn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="slektsNavnugift" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="navnFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="navnTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="navnKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="navnRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "navn", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "forkortetNavn",
        "slektsNavn",
        "forNavn",
        "mellomNavn",
        "slektsNavnugift",
        "navnFraDato",
        "navnTilDato",
        "navnKilde",
        "navnRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Navn {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String forkortetNavn;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String slektsNavn;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String forNavn;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String mellomNavn;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String slektsNavnugift;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String navnFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String navnTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String navnKilde;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String navnRegistrertAv;

    /**
     * Gets the value of the forkortetNavn property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getForkortetNavn() {
        return forkortetNavn;
    }

    /**
     * Sets the value of the forkortetNavn property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setForkortetNavn(String value) {
        this.forkortetNavn = value;
    }

    /**
     * Gets the value of the slektsNavn property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSlektsNavn() {
        return slektsNavn;
    }

    /**
     * Sets the value of the slektsNavn property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSlektsNavn(String value) {
        this.slektsNavn = value;
    }

    /**
     * Gets the value of the forNavn property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getForNavn() {
        return forNavn;
    }

    /**
     * Sets the value of the forNavn property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setForNavn(String value) {
        this.forNavn = value;
    }

    /**
     * Gets the value of the mellomNavn property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMellomNavn() {
        return mellomNavn;
    }

    /**
     * Sets the value of the mellomNavn property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMellomNavn(String value) {
        this.mellomNavn = value;
    }

    /**
     * Gets the value of the slektsNavnugift property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getSlektsNavnugift() {
        return slektsNavnugift;
    }

    /**
     * Sets the value of the slektsNavnugift property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setSlektsNavnugift(String value) {
        this.slektsNavnugift = value;
    }

    /**
     * Gets the value of the navnFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getNavnFraDato() {
        return navnFraDato;
    }

    /**
     * Sets the value of the navnFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setNavnFraDato(String value) {
        this.navnFraDato = value;
    }

    /**
     * Gets the value of the navnTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getNavnTilDato() {
        return navnTilDato;
    }

    /**
     * Sets the value of the navnTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setNavnTilDato(String value) {
        this.navnTilDato = value;
    }

    /**
     * Gets the value of the navnKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getNavnKilde() {
        return navnKilde;
    }

    /**
     * Sets the value of the navnKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setNavnKilde(String value) {
        this.navnKilde = value;
    }

    /**
     * Gets the value of the navnRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getNavnRegistrertAv() {
        return navnRegistrertAv;
    }

    /**
     * Sets the value of the navnRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setNavnRegistrertAv(String value) {
        this.navnRegistrertAv = value;
    }

}
