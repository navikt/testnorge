
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene i et enkelt brukerbehov
 *
 * <p>Java class for behovType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="behovType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="brukerBehov" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="brukerBehovFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="brukerBehovTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="brukerBehovKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="brukerBehovRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "behov", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "brukerBehov",
        "brukerBehovFraDato",
        "brukerBehovTilDato",
        "brukerBehovKilde",
        "brukerBehovRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Behov {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String brukerBehov;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String brukerBehovFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String brukerBehovTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String brukerBehovKilde;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String brukerBehovRegistrertAv;

    /**
     * Gets the value of the brukerBehov property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBrukerBehov() {
        return brukerBehov;
    }

    /**
     * Sets the value of the brukerBehov property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBrukerBehov(String value) {
        this.brukerBehov = value;
    }

    /**
     * Gets the value of the brukerBehovFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBrukerBehovFraDato() {
        return brukerBehovFraDato;
    }

    /**
     * Sets the value of the brukerBehovFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBrukerBehovFraDato(String value) {
        this.brukerBehovFraDato = value;
    }

    /**
     * Gets the value of the brukerBehovTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBrukerBehovTilDato() {
        return brukerBehovTilDato;
    }

    /**
     * Sets the value of the brukerBehovTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBrukerBehovTilDato(String value) {
        this.brukerBehovTilDato = value;
    }

    /**
     * Gets the value of the brukerBehovKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBrukerBehovKilde() {
        return brukerBehovKilde;
    }

    /**
     * Sets the value of the brukerBehovKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBrukerBehovKilde(String value) {
        this.brukerBehovKilde = value;
    }

    /**
     * Gets the value of the brukerBehovRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getBrukerBehovRegistrertAv() {
        return brukerBehovRegistrertAv;
    }

    /**
     * Sets the value of the brukerBehovRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setBrukerBehovRegistrertAv(String value) {
        this.brukerBehovRegistrertAv = value;
    }

}
