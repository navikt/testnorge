
package no.nav.registre.hodejegeren.tpsPersonDokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver en Identstatus
 *
 * <p>Java class for personIdentStatusType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="personIdentStatusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="personIdentStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personIdentStatusFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personIdentStatusTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personIdentStatusKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personIdentStatus", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "personIdentStatus",
        "personIdentStatusFraDato",
        "personIdentStatusTilDato",
        "personIdentStatusKilde"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonIdentStatus {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String personIdentStatus;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String personIdentStatusFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String personIdentStatusTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String personIdentStatusKilde;

    /**
     * Gets the value of the personIdentStatus property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonIdentStatus() {
        return personIdentStatus;
    }

    /**
     * Sets the value of the personIdentStatus property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonIdentStatus(String value) {
        this.personIdentStatus = value;
    }

    /**
     * Gets the value of the personIdentStatusFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonIdentStatusFraDato() {
        return personIdentStatusFraDato;
    }

    /**
     * Sets the value of the personIdentStatusFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonIdentStatusFraDato(String value) {
        this.personIdentStatusFraDato = value;
    }

    /**
     * Gets the value of the personIdentStatusTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonIdentStatusTilDato() {
        return personIdentStatusTilDato;
    }

    /**
     * Sets the value of the personIdentStatusTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonIdentStatusTilDato(String value) {
        this.personIdentStatusTilDato = value;
    }

    /**
     * Gets the value of the personIdentStatusKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonIdentStatusKilde() {
        return personIdentStatusKilde;
    }

    /**
     * Sets the value of the personIdentStatusKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonIdentStatusKilde(String value) {
        this.personIdentStatusKilde = value;
    }

}
