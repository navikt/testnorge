
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene i en enkel oppholdstillatelse
 *
 * <p>Java class for oppholdstillatelseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="oppholdstillatelseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="oppholdsTillatelse" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="oppholdsTillatelseFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="oppholdsTillatelseTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="oppholdsTillatelseKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="oppholdsTillatelseRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "oppholdstillatelse", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "oppholdsTillatelse",
        "oppholdsTillatelseFraDato",
        "oppholdsTillatelseTilDato",
        "oppholdsTillatelseKilde",
        "oppholdsTillatelseRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Oppholdstillatelse {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String oppholdsTillatelse;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String oppholdsTillatelseFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String oppholdsTillatelseTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String oppholdsTillatelseKilde;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String oppholdsTillatelseRegistrertAv;

    /**
     * Gets the value of the oppholdsTillatelse property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getOppholdsTillatelse() {
        return oppholdsTillatelse;
    }

    /**
     * Sets the value of the oppholdsTillatelse property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setOppholdsTillatelse(String value) {
        this.oppholdsTillatelse = value;
    }

    /**
     * Gets the value of the oppholdsTillatelseFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getOppholdsTillatelseFraDato() {
        return oppholdsTillatelseFraDato;
    }

    /**
     * Sets the value of the oppholdsTillatelseFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setOppholdsTillatelseFraDato(String value) {
        this.oppholdsTillatelseFraDato = value;
    }

    /**
     * Gets the value of the oppholdsTillatelseTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getOppholdsTillatelseTilDato() {
        return oppholdsTillatelseTilDato;
    }

    /**
     * Sets the value of the oppholdsTillatelseTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setOppholdsTillatelseTilDato(String value) {
        this.oppholdsTillatelseTilDato = value;
    }

    /**
     * Gets the value of the oppholdsTillatelseKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getOppholdsTillatelseKilde() {
        return oppholdsTillatelseKilde;
    }

    /**
     * Sets the value of the oppholdsTillatelseKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setOppholdsTillatelseKilde(String value) {
        this.oppholdsTillatelseKilde = value;
    }

    /**
     * Gets the value of the oppholdsTillatelseRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getOppholdsTillatelseRegistrertAv() {
        return oppholdsTillatelseRegistrertAv;
    }

    /**
     * Sets the value of the oppholdsTillatelseRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setOppholdsTillatelseRegistrertAv(String value) {
        this.oppholdsTillatelseRegistrertAv = value;
    }

}
