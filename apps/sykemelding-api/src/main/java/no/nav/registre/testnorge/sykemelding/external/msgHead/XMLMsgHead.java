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
import java.util.ArrayList;
import java.util.List;


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
 *         &lt;element ref="{http://www.kith.no/xmlstds/msghead/2006-05-24}MsgInfo"/&gt;
 *         &lt;choice&gt;
 *           &lt;element ref="{http://www.kith.no/xmlstds/msghead/2006-05-24}Document" maxOccurs="unbounded"/&gt;
 *           &lt;element ref="{http://www.kith.no/xmlstds/msghead/2006-05-24}PatientReport" maxOccurs="unbounded"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}Signature" minOccurs="0"/&gt;
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
    "msgInfo",
    "document",
    "patientReport",
    "signature"
})
@XmlRootElement(name = "MsgHead", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24")
public class XMLMsgHead {

    @XmlElement(name = "MsgInfo", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24", required = true)
    protected XMLMsgInfo msgInfo;
    @XmlElement(name = "Document", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24")
    protected List<XMLDocument> document;
    @XmlElement(name = "PatientReport", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24")
    protected List<XMLPatientReport> patientReport;
    @XmlElement(name = "Signature")
    protected XMLSignatureType signature;

    /**
     * Gets the value of the msgInfo property.
     * 
     * @return
     *     possible object is
     *     {@link XMLMsgInfo }
     *     
     */
    public XMLMsgInfo getMsgInfo() {
        return msgInfo;
    }

    /**
     * Sets the value of the msgInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLMsgInfo }
     *     
     */
    public void setMsgInfo(XMLMsgInfo value) {
        this.msgInfo = value;
    }

    /**
     * Gets the value of the document property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the document property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDocument().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XMLDocument }
     * 
     * 
     */
    public List<XMLDocument> getDocument() {
        if (document == null) {
            document = new ArrayList<XMLDocument>();
        }
        return this.document;
    }

    /**
     * 
     *                                 Benyttes ved innrapportering av data til helseregistre.Gets the value of the patientReport property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the patientReport property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPatientReport().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XMLPatientReport }
     * 
     * 
     */
    public List<XMLPatientReport> getPatientReport() {
        if (patientReport == null) {
            patientReport = new ArrayList<XMLPatientReport>();
        }
        return this.patientReport;
    }

    /**
     * Gets the value of the signature property.
     * 
     * @return
     *     possible object is
     *     {@link XMLSignatureType }
     *     
     */
    public XMLSignatureType getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLSignatureType }
     *     
     */
    public void setSignature(XMLSignatureType value) {
        this.signature = value;
    }

}
