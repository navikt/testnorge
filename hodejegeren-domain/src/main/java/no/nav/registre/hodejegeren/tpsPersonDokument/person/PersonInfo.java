
package no.nav.registre.hodejegeren.tpsPersonDokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver basis informasjon for en person
 *
 * <p>Java class for personInfoType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="personInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="personKjonn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personDatofodt" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personFodtLand" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personFodtKommune" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="personKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "personInfo", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "personKjonn",
        "personDatofodt",
        "personFodtLand",
        "personFodtKommune",
        "personFraDato",
        "personTilDato",
        "personKilde"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonInfo {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String personKjonn;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String personDatofodt;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String personFodtLand;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String personFodtKommune;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String personFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String personTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String personKilde;

    /**
     * Gets the value of the personKjonn property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonKjonn() {
        return personKjonn;
    }

    /**
     * Sets the value of the personKjonn property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonKjonn(String value) {
        this.personKjonn = value;
    }

    /**
     * Gets the value of the personDatofodt property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonDatofodt() {
        return personDatofodt;
    }

    /**
     * Sets the value of the personDatofodt property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonDatofodt(String value) {
        this.personDatofodt = value;
    }

    /**
     * Gets the value of the personFodtLand property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonFodtLand() {
        return personFodtLand;
    }

    /**
     * Sets the value of the personFodtLand property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonFodtLand(String value) {
        this.personFodtLand = value;
    }

    /**
     * Gets the value of the personFodtKommune property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonFodtKommune() {
        return personFodtKommune;
    }

    /**
     * Sets the value of the personFodtKommune property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonFodtKommune(String value) {
        this.personFodtKommune = value;
    }

    /**
     * Gets the value of the personFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonFraDato() {
        return personFraDato;
    }

    /**
     * Sets the value of the personFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonFraDato(String value) {
        this.personFraDato = value;
    }

    /**
     * Gets the value of the personTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonTilDato() {
        return personTilDato;
    }

    /**
     * Sets the value of the personTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonTilDato(String value) {
        this.personTilDato = value;
    }

    /**
     * Gets the value of the personKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPersonKilde() {
        return personKilde;
    }

    /**
     * Sets the value of the personKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPersonKilde(String value) {
        this.personKilde = value;
    }

}
