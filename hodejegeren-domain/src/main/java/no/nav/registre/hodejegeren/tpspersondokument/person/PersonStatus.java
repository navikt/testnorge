
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene personstatus
 *
 * <p>Java class for personStatusType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="personStatusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="personStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personStatusFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personStatusTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personStatusKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personStatus", propOrder = {
        "personStatus",
        "personStatusFraDato",
        "personStatusTilDato",
        "personStatusKilde"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonStatus {

    @XmlElement(required = true)
    protected String personStatus;
    @XmlElement(required = true)
    protected String personStatusFraDato;
    @XmlElement(required = true)
    protected String personStatusTilDato;
    @XmlElement(required = true)
    protected String personStatusKilde;

    /**
     * Gets the value of the personStatus property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonStatus() {
        return personStatus;
    }

    /**
     * Sets the value of the personStatus property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonStatus(String value) {
        this.personStatus = value;
    }

    /**
     * Gets the value of the personStatusFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonStatusFraDato() {
        return personStatusFraDato;
    }

    /**
     * Sets the value of the personStatusFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonStatusFraDato(String value) {
        this.personStatusFraDato = value;
    }

    /**
     * Gets the value of the personStatusTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonStatusTilDato() {
        return personStatusTilDato;
    }

    /**
     * Sets the value of the personStatusTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonStatusTilDato(String value) {
        this.personStatusTilDato = value;
    }

    /**
     * Gets the value of the personStatusKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonStatusKilde() {
        return personStatusKilde;
    }

    /**
     * Sets the value of the personStatusKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonStatusKilde(String value) {
        this.personStatusKilde = value;
    }

}
