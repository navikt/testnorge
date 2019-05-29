
package no.nav.registre.hodejegeren.tpsPersonDokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene i et enkelt foreldreansvar
 *
 * <p>Java class for foreldreansvarType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="foreldreansvarType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="foreldreAnsvar" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="foreldreAnsvarFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="foreldreAnsvarTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="foreldreAnsvarKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="forreldreAnsvarRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "foreldreansvar", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "foreldreAnsvar",
        "foreldreAnsvarFraDato",
        "foreldreAnsvarTilDato",
        "foreldreAnsvarKilde",
        "forreldreAnsvarRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Foreldreansvar {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String foreldreAnsvar;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String foreldreAnsvarFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String foreldreAnsvarTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String foreldreAnsvarKilde;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String forreldreAnsvarRegistrertAv;

    /**
     * Gets the value of the foreldreAnsvar property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getForeldreAnsvar() {
        return foreldreAnsvar;
    }

    /**
     * Sets the value of the foreldreAnsvar property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setForeldreAnsvar(String value) {
        this.foreldreAnsvar = value;
    }

    /**
     * Gets the value of the foreldreAnsvarFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getForeldreAnsvarFraDato() {
        return foreldreAnsvarFraDato;
    }

    /**
     * Sets the value of the foreldreAnsvarFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setForeldreAnsvarFraDato(String value) {
        this.foreldreAnsvarFraDato = value;
    }

    /**
     * Gets the value of the foreldreAnsvarTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getForeldreAnsvarTilDato() {
        return foreldreAnsvarTilDato;
    }

    /**
     * Sets the value of the foreldreAnsvarTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setForeldreAnsvarTilDato(String value) {
        this.foreldreAnsvarTilDato = value;
    }

    /**
     * Gets the value of the foreldreAnsvarKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getForeldreAnsvarKilde() {
        return foreldreAnsvarKilde;
    }

    /**
     * Sets the value of the foreldreAnsvarKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setForeldreAnsvarKilde(String value) {
        this.foreldreAnsvarKilde = value;
    }

    /**
     * Gets the value of the forreldreAnsvarRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getForreldreAnsvarRegistrertAv() {
        return forreldreAnsvarRegistrertAv;
    }

    /**
     * Sets the value of the forreldreAnsvarRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setForreldreAnsvarRegistrertAv(String value) {
        this.forreldreAnsvarRegistrertAv = value;
    }

}
