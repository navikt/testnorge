
package no.nav.registre.hodejegeren.tpsPersonDokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene i en enkel migrering
 *
 * <p>Java class for migrasjonType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="migrasjonType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="migrasjon" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="migrasjonLand" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="migrasjonFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="migrasjonTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="migrasjonKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="migrasjonRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "migrasjon", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "migrasjon",
        "migrasjonLand",
        "migrasjonFraDato",
        "migrasjonTilDato",
        "migrasjonKilde",
        "migrasjonRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Migrasjon {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String migrasjon;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String migrasjonLand;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String migrasjonFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String migrasjonTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String migrasjonKilde;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String migrasjonRegistrertAv;

    /**
     * Gets the value of the migrasjon property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMigrasjon() {
        return migrasjon;
    }

    /**
     * Sets the value of the migrasjon property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMigrasjon(String value) {
        this.migrasjon = value;
    }

    /**
     * Gets the value of the migrasjonLand property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMigrasjonLand() {
        return migrasjonLand;
    }

    /**
     * Sets the value of the migrasjonLand property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMigrasjonLand(String value) {
        this.migrasjonLand = value;
    }

    /**
     * Gets the value of the migrasjonFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMigrasjonFraDato() {
        return migrasjonFraDato;
    }

    /**
     * Sets the value of the migrasjonFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMigrasjonFraDato(String value) {
        this.migrasjonFraDato = value;
    }

    /**
     * Gets the value of the migrasjonTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMigrasjonTilDato() {
        return migrasjonTilDato;
    }

    /**
     * Sets the value of the migrasjonTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMigrasjonTilDato(String value) {
        this.migrasjonTilDato = value;
    }

    /**
     * Gets the value of the migrasjonKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMigrasjonKilde() {
        return migrasjonKilde;
    }

    /**
     * Sets the value of the migrasjonKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMigrasjonKilde(String value) {
        this.migrasjonKilde = value;
    }

    /**
     * Gets the value of the migrasjonRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getMigrasjonRegistrertAv() {
        return migrasjonRegistrertAv;
    }

    /**
     * Sets the value of the migrasjonRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setMigrasjonRegistrertAv(String value) {
        this.migrasjonRegistrertAv = value;
    }

}
