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
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ComMethod" type="{http://www.kith.no/xmlstds/msghead/2006-05-24}CS" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.kith.no/xmlstds/msghead/2006-05-24}Organisation"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "comMethod",
    "organisation"
})
@XmlRootElement(name = "Receiver", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24")
public class XMLReceiver {

    @XmlElement(name = "ComMethod", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24")
    protected XMLCS comMethod;
    @XmlElement(name = "Organisation", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24", required = true)
    protected XMLOrganisation organisation;

    /**
     * Gets the value of the comMethod property.
     * 
     * @return
     *     possible object is
     *     {@link XMLCS }
     *     
     */
    public XMLCS getComMethod() {
        return comMethod;
    }

    /**
     * Sets the value of the comMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLCS }
     *     
     */
    public void setComMethod(XMLCS value) {
        this.comMethod = value;
    }

    /**
     * Gets the value of the organisation property.
     * 
     * @return
     *     possible object is
     *     {@link XMLOrganisation }
     *     
     */
    public XMLOrganisation getOrganisation() {
        return organisation;
    }

    /**
     * Sets the value of the organisation property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLOrganisation }
     *     
     */
    public void setOrganisation(XMLOrganisation value) {
        this.organisation = value;
    }

}
