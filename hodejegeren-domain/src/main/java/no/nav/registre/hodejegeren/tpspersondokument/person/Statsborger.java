
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene i et statsborgerskap.
 *
 * <p>Java class for statsborgerType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="statsborgerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="statsborger" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="statsborgerFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="statsborgerTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="statsborgerKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="statsborgerRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "statsborger", propOrder = {
        "statsborger",
        "statsborgerFraDato",
        "statsborgerTilDato",
        "statsborgerKilde",
        "statsborgerRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statsborger {

    @XmlElement(required = true)
    protected String statsborger;
    @XmlElement(required = true)
    protected String statsborgerFraDato;
    @XmlElement(required = true)
    protected String statsborgerTilDato;
    @XmlElement(required = true)
    protected String statsborgerKilde;
    @XmlElement(required = true)
    protected String statsborgerRegistrertAv;

    /**
     * Gets the value of the statsborger property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getStatsborger() {
        return statsborger;
    }

    /**
     * Sets the value of the statsborger property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStatsborger(String value) {
        this.statsborger = value;
    }

    /**
     * Gets the value of the statsborgerFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getStatsborgerFraDato() {
        return statsborgerFraDato;
    }

    /**
     * Sets the value of the statsborgerFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStatsborgerFraDato(String value) {
        this.statsborgerFraDato = value;
    }

    /**
     * Gets the value of the statsborgerTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getStatsborgerTilDato() {
        return statsborgerTilDato;
    }

    /**
     * Sets the value of the statsborgerTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStatsborgerTilDato(String value) {
        this.statsborgerTilDato = value;
    }

    /**
     * Gets the value of the statsborgerKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getStatsborgerKilde() {
        return statsborgerKilde;
    }

    /**
     * Sets the value of the statsborgerKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStatsborgerKilde(String value) {
        this.statsborgerKilde = value;
    }

    /**
     * Gets the value of the statsborgerRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getStatsborgerRegistrertAv() {
        return statsborgerRegistrertAv;
    }

    /**
     * Sets the value of the statsborgerRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStatsborgerRegistrertAv(String value) {
        this.statsborgerRegistrertAv = value;
    }

}
