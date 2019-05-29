
package no.nav.registre.hodejegeren.tpsPersonDokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for prioritertadresseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="prioritertadresseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="prioritertAdresse" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="prioritertAdresseFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="prioritertAdresseTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="prioritertAdresseKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "prioritertadresse", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "prioritertAdresse",
        "prioritertAdresseFraDato",
        "prioritertAdresseTilDato",
        "prioritertAdresseKilde"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prioritertadresse {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String prioritertAdresse;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String prioritertAdresseFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String prioritertAdresseTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String prioritertAdresseKilde;

    /**
     * Gets the value of the prioritertAdresse property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPrioritertAdresse() {
        return prioritertAdresse;
    }

    /**
     * Sets the value of the prioritertAdresse property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPrioritertAdresse(String value) {
        this.prioritertAdresse = value;
    }

    /**
     * Gets the value of the prioritertAdresseFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPrioritertAdresseFraDato() {
        return prioritertAdresseFraDato;
    }

    /**
     * Sets the value of the prioritertAdresseFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPrioritertAdresseFraDato(String value) {
        this.prioritertAdresseFraDato = value;
    }

    /**
     * Gets the value of the prioritertAdresseTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPrioritertAdresseTilDato() {
        return prioritertAdresseTilDato;
    }

    /**
     * Sets the value of the prioritertAdresseTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPrioritertAdresseTilDato(String value) {
        this.prioritertAdresseTilDato = value;
    }

    /**
     * Gets the value of the prioritertAdresseKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPrioritertAdresseKilde() {
        return prioritertAdresseKilde;
    }

    /**
     * Sets the value of the prioritertAdresseKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPrioritertAdresseKilde(String value) {
        this.prioritertAdresseKilde = value;
    }

}
