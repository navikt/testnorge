
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene i utenlandsk postadressegruppa.
 *
 * <p>Java class for utadType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="utadType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="utadAdresse1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utadAdresse2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utadAdresse3" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utadAdresseDatoTom" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utadLand" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utadAdresseFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utadAdresseTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utadKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utadRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "utad", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "utadAdresse1",
        "utadAdresse2",
        "utadAdresse3",
        "utadAdresseDatoTom",
        "utadLand",
        "utadAdresseFraDato",
        "utadAdresseTilDato",
        "utadKilde",
        "utadRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utad {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utadAdresse1;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utadAdresse2;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utadAdresse3;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utadAdresseDatoTom;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utadLand;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utadAdresseFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utadAdresseTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utadKilde;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utadRegistrertAv;

    /**
     * Gets the value of the utadAdresse1 property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtadAdresse1() {
        return utadAdresse1;
    }

    /**
     * Sets the value of the utadAdresse1 property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtadAdresse1(String value) {
        this.utadAdresse1 = value;
    }

    /**
     * Gets the value of the utadAdresse2 property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtadAdresse2() {
        return utadAdresse2;
    }

    /**
     * Sets the value of the utadAdresse2 property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtadAdresse2(String value) {
        this.utadAdresse2 = value;
    }

    /**
     * Gets the value of the utadAdresse3 property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtadAdresse3() {
        return utadAdresse3;
    }

    /**
     * Sets the value of the utadAdresse3 property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtadAdresse3(String value) {
        this.utadAdresse3 = value;
    }

    /**
     * Gets the value of the utadAdresseDatoTom property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtadAdresseDatoTom() {
        return utadAdresseDatoTom;
    }

    /**
     * Sets the value of the utadAdresseDatoTom property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtadAdresseDatoTom(String value) {
        this.utadAdresseDatoTom = value;
    }

    /**
     * Gets the value of the utadLand property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtadLand() {
        return utadLand;
    }

    /**
     * Sets the value of the utadLand property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtadLand(String value) {
        this.utadLand = value;
    }

    /**
     * Gets the value of the utadAdresseFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtadAdresseFraDato() {
        return utadAdresseFraDato;
    }

    /**
     * Sets the value of the utadAdresseFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtadAdresseFraDato(String value) {
        this.utadAdresseFraDato = value;
    }

    /**
     * Gets the value of the utadAdresseTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtadAdresseTilDato() {
        return utadAdresseTilDato;
    }

    /**
     * Sets the value of the utadAdresseTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtadAdresseTilDato(String value) {
        this.utadAdresseTilDato = value;
    }

    /**
     * Gets the value of the utadKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtadKilde() {
        return utadKilde;
    }

    /**
     * Sets the value of the utadKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtadKilde(String value) {
        this.utadKilde = value;
    }

    /**
     * Gets the value of the utadRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtadRegistrertAv() {
        return utadRegistrertAv;
    }

    /**
     * Sets the value of the utadRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtadRegistrertAv(String value) {
        this.utadRegistrertAv = value;
    }

}
