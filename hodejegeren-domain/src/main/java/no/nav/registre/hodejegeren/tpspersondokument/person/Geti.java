
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene i geografisk tilknynting
 *
 * <p>Java class for getiType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="getiType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="getiLand" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="getiKommune" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="getiBydel" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="getiRegel" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="getiFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="getiTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="getiKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "geti", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "getiLand",
        "getiKommune",
        "getiBydel",
        "getiRegel",
        "getiFraDato",
        "getiTilDato",
        "getiKilde"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Geti {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String getiLand;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String getiKommune;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String getiBydel;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String getiRegel;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String getiFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String getiTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String getiKilde;

    /**
     * Gets the value of the getiLand property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getGetiLand() {
        return getiLand;
    }

    /**
     * Sets the value of the getiLand property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setGetiLand(String value) {
        this.getiLand = value;
    }

    /**
     * Gets the value of the getiKommune property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getGetiKommune() {
        return getiKommune;
    }

    /**
     * Sets the value of the getiKommune property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setGetiKommune(String value) {
        this.getiKommune = value;
    }

    /**
     * Gets the value of the getiBydel property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getGetiBydel() {
        return getiBydel;
    }

    /**
     * Sets the value of the getiBydel property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setGetiBydel(String value) {
        this.getiBydel = value;
    }

    /**
     * Gets the value of the getiRegel property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getGetiRegel() {
        return getiRegel;
    }

    /**
     * Sets the value of the getiRegel property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setGetiRegel(String value) {
        this.getiRegel = value;
    }

    /**
     * Gets the value of the getiFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getGetiFraDato() {
        return getiFraDato;
    }

    /**
     * Sets the value of the getiFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setGetiFraDato(String value) {
        this.getiFraDato = value;
    }

    /**
     * Gets the value of the getiTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getGetiTilDato() {
        return getiTilDato;
    }

    /**
     * Sets the value of the getiTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setGetiTilDato(String value) {
        this.getiTilDato = value;
    }

    /**
     * Gets the value of the getiKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getGetiKilde() {
        return getiKilde;
    }

    /**
     * Sets the value of the getiKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setGetiKilde(String value) {
        this.getiKilde = value;
    }

}
