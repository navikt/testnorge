//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0.1 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.09.09 at 06:50:39 AM UTC 
//


package no.nav.registre.testnorge.sykemelding.external.msgHead;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 *                 Inneholder opplysninger om telekommunikasjonsadresse, inklusive kommunikasjonstype. Denne klassen benyttes for aa registrere telefonnummer, telefaks, personsoker etc., knyttes opp mot de registrerte adressene.
 *             
 * 
 * <p>Java class for TeleCom complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TeleCom"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TypeTelecom" type="{http://www.kith.no/xmlstds/msghead/2006-05-24}CS" minOccurs="0"/&gt;
 *         &lt;element name="TeleAddress" type="{http://www.kith.no/xmlstds/msghead/2006-05-24}URL"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TeleCom", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24", propOrder = {
    "typeTelecom",
    "teleAddress"
})
public class XMLTeleCom {

    @XmlElement(name = "TypeTelecom")
    protected XMLCS typeTelecom;
    @XmlElement(name = "TeleAddress", required = true)
    protected XMLURL teleAddress;

    /**
     * Gets the value of the typeTelecom property.
     * 
     * @return
     *     possible object is
     *     {@link XMLCS }
     *     
     */
    public XMLCS getTypeTelecom() {
        return typeTelecom;
    }

    /**
     * Sets the value of the typeTelecom property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLCS }
     *     
     */
    public void setTypeTelecom(XMLCS value) {
        this.typeTelecom = value;
    }

    /**
     * Gets the value of the teleAddress property.
     * 
     * @return
     *     possible object is
     *     {@link XMLURL }
     *     
     */
    public XMLURL getTeleAddress() {
        return teleAddress;
    }

    /**
     * Sets the value of the teleAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLURL }
     *     
     */
    public void setTeleAddress(XMLURL value) {
        this.teleAddress = value;
    }

}
