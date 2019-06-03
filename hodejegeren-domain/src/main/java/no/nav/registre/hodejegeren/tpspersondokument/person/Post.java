
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene i Postadressegruppa.
 *
 * <p>Java class for Post complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="Post">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="postAdresse1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="postAdresse2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="postAdresse3" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="postpostnr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="postLand" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="postAdresseFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="postAdresseTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="postKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="postRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Post", propOrder = {
        "postAdresse1",
        "postAdresse2",
        "postAdresse3",
        "postpostnr",
        "postLand",
        "postAdresseFraDato",
        "postAdresseTilDato",
        "postKilde",
        "postRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @XmlElement(required = true)
    protected String postAdresse1;
    @XmlElement(required = true)
    protected String postAdresse2;
    @XmlElement(required = true)
    protected String postAdresse3;
    @XmlElement(required = true)
    protected String postpostnr;
    @XmlElement(required = true)
    protected String postLand;
    @XmlElement(required = true)
    protected String postAdresseFraDato;
    @XmlElement(required = true)
    protected String postAdresseTilDato;
    @XmlElement(required = true)
    protected String postKilde;
    @XmlElement(required = true)
    protected String postRegistrertAv;

    /**
     * Gets the value of the postAdresse1 property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPostAdresse1() {
        return postAdresse1;
    }

    /**
     * Sets the value of the postAdresse1 property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPostAdresse1(String value) {
        this.postAdresse1 = value;
    }

    /**
     * Gets the value of the postAdresse2 property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPostAdresse2() {
        return postAdresse2;
    }

    /**
     * Sets the value of the postAdresse2 property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPostAdresse2(String value) {
        this.postAdresse2 = value;
    }

    /**
     * Gets the value of the postAdresse3 property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPostAdresse3() {
        return postAdresse3;
    }

    /**
     * Sets the value of the postAdresse3 property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPostAdresse3(String value) {
        this.postAdresse3 = value;
    }

    /**
     * Gets the value of the postpostnr property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPostpostnr() {
        return postpostnr;
    }

    /**
     * Sets the value of the postpostnr property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPostpostnr(String value) {
        this.postpostnr = value;
    }

    /**
     * Gets the value of the postLand property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPostLand() {
        return postLand;
    }

    /**
     * Sets the value of the postLand property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPostLand(String value) {
        this.postLand = value;
    }

    /**
     * Gets the value of the postAdresseFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPostAdresseFraDato() {
        return postAdresseFraDato;
    }

    /**
     * Sets the value of the postAdresseFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPostAdresseFraDato(String value) {
        this.postAdresseFraDato = value;
    }

    /**
     * Gets the value of the postAdresseTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPostAdresseTilDato() {
        return postAdresseTilDato;
    }

    /**
     * Sets the value of the postAdresseTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPostAdresseTilDato(String value) {
        this.postAdresseTilDato = value;
    }

    /**
     * Gets the value of the postKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPostKilde() {
        return postKilde;
    }

    /**
     * Sets the value of the postKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPostKilde(String value) {
        this.postKilde = value;
    }

    /**
     * Gets the value of the postRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPostRegistrertAv() {
        return postRegistrertAv;
    }

    /**
     * Sets the value of the postRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPostRegistrertAv(String value) {
        this.postRegistrertAv = value;
    }

}
