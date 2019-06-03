
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver en Ident med kobling
 *
 * <p>Java class for personIdentType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="personIdentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="personIdent" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personIdenttype" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personIdentneste" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personIdentFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personIdentTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personIdentKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personIdent", propOrder = {
        "personIdent",
        "personIdenttype",
        "personIdentneste",
        "personIdentFraDato",
        "personIdentTilDato",
        "personIdentKilde"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonIdent {

    @XmlElement(required = true)
    protected String personIdent;
    @XmlElement(required = true)
    protected String personIdenttype;
    @XmlElement(required = true)
    protected String personIdentneste;
    @XmlElement(required = true)
    protected String personIdentFraDato;
    @XmlElement(required = true)
    protected String personIdentTilDato;
    @XmlElement(required = true)
    protected String personIdentKilde;

    /**
     * Gets the value of the personIdent property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonIdent() {
        return personIdent;
    }

    /**
     * Sets the value of the personIdent property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonIdent(String value) {
        this.personIdent = value;
    }

    /**
     * Gets the value of the personIdenttype property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonIdenttype() {
        return personIdenttype;
    }

    /**
     * Sets the value of the personIdenttype property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonIdenttype(String value) {
        this.personIdenttype = value;
    }

    /**
     * Gets the value of the personIdentneste property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonIdentneste() {
        return personIdentneste;
    }

    /**
     * Sets the value of the personIdentneste property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonIdentneste(String value) {
        this.personIdentneste = value;
    }

    /**
     * Gets the value of the personIdentFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonIdentFraDato() {
        return personIdentFraDato;
    }

    /**
     * Sets the value of the personIdentFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonIdentFraDato(String value) {
        this.personIdentFraDato = value;
    }

    /**
     * Gets the value of the personIdentTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonIdentTilDato() {
        return personIdentTilDato;
    }

    /**
     * Sets the value of the personIdentTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonIdentTilDato(String value) {
        this.personIdentTilDato = value;
    }

    /**
     * Gets the value of the personIdentKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonIdentKilde() {
        return personIdentKilde;
    }

    /**
     * Sets the value of the personIdentKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonIdentKilde(String value) {
        this.personIdentKilde = value;
    }

}
