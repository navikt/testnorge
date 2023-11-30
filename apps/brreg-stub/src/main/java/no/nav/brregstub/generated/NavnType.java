
package no.nav.brregstub.generated;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for navnType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="navnType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="navn1"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="navn2" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="navn3" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="navn4" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="35"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="navn5" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="35"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "navnType", propOrder = {
    "navn1",
    "navn2",
    "navn3",
    "navn4",
    "navn5"
})
public class NavnType {

    @XmlElement(required = true)
    protected String navn1;
    protected String navn2;
    protected String navn3;
    protected String navn4;
    protected String navn5;
    @XmlAttribute(name = "ledetekst")
    protected String ledetekst;

    /**
     * Gets the value of the navn1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNavn1() {
        return navn1;
    }

    /**
     * Sets the value of the navn1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNavn1(String value) {
        this.navn1 = value;
    }

    /**
     * Gets the value of the navn2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNavn2() {
        return navn2;
    }

    /**
     * Sets the value of the navn2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNavn2(String value) {
        this.navn2 = value;
    }

    /**
     * Gets the value of the navn3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNavn3() {
        return navn3;
    }

    /**
     * Sets the value of the navn3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNavn3(String value) {
        this.navn3 = value;
    }

    /**
     * Gets the value of the navn4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNavn4() {
        return navn4;
    }

    /**
     * Sets the value of the navn4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNavn4(String value) {
        this.navn4 = value;
    }

    /**
     * Gets the value of the navn5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNavn5() {
        return navn5;
    }

    /**
     * Sets the value of the navn5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNavn5(String value) {
        this.navn5 = value;
    }

    /**
     * Gets the value of the ledetekst property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLedetekst() {
        return ledetekst;
    }

    /**
     * Sets the value of the ledetekst property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLedetekst(String value) {
        this.ledetekst = value;
    }

}
