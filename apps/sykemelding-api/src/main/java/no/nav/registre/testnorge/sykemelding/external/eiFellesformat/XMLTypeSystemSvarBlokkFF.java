//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0.1 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.09.09 at 06:50:45 AM UTC 
//


package no.nav.registre.testnorge.sykemelding.external.eiFellesformat;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * Denne blokken inneholder ett sett svar/utfall fra kontrollsystemet.
 * 
 * <p>Java class for typeSystemSvarBlokkFF complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="typeSystemSvarBlokkFF"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SystemSvar" type="{http://www.trygdeetaten.no/xml/eiff/1/}typeSystemSvarFF" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="formOfLanguage" use="required" type="{http://www.trygdeetaten.no/xml/eiff/1/}typeFormOfLanguageFF" /&gt;
 *       &lt;attribute name="sendersId"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;whiteSpace value="preserve"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="receiversId"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;whiteSpace value="preserve"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="messageId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="tidsstempel" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "typeSystemSvarBlokkFF", propOrder = {
    "systemSvar"
})
public class XMLTypeSystemSvarBlokkFF {

    @XmlElement(name = "SystemSvar", required = true)
    protected List<XMLTypeSystemSvarFF> systemSvar;
    @XmlAttribute(name = "formOfLanguage", required = true)
    protected BigInteger formOfLanguage;
    @XmlAttribute(name = "sendersId")
    protected String sendersId;
    @XmlAttribute(name = "receiversId")
    protected String receiversId;
    @XmlAttribute(name = "messageId", required = true)
    protected String messageId;
    @XmlAttribute(name = "tidsstempel", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar tidsstempel;

    /**
     * Gets the value of the systemSvar property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the systemSvar property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSystemSvar().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XMLTypeSystemSvarFF }
     * 
     * 
     */
    public List<XMLTypeSystemSvarFF> getSystemSvar() {
        if (systemSvar == null) {
            systemSvar = new ArrayList<XMLTypeSystemSvarFF>();
        }
        return this.systemSvar;
    }

    /**
     * Gets the value of the formOfLanguage property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFormOfLanguage() {
        return formOfLanguage;
    }

    /**
     * Sets the value of the formOfLanguage property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFormOfLanguage(BigInteger value) {
        this.formOfLanguage = value;
    }

    /**
     * Gets the value of the sendersId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendersId() {
        return sendersId;
    }

    /**
     * Sets the value of the sendersId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendersId(String value) {
        this.sendersId = value;
    }

    /**
     * Gets the value of the receiversId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiversId() {
        return receiversId;
    }

    /**
     * Sets the value of the receiversId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiversId(String value) {
        this.receiversId = value;
    }

    /**
     * Gets the value of the messageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageId(String value) {
        this.messageId = value;
    }

    /**
     * Gets the value of the tidsstempel property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTidsstempel() {
        return tidsstempel;
    }

    /**
     * Sets the value of the tidsstempel property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTidsstempel(XMLGregorianCalendar value) {
        this.tidsstempel = value;
    }

}
