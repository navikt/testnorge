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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Denne blokken samler informasjon generert i mottaket. Det er ikke beregnet at denne informasjonen skal endres etter at SM/LE har kommet ut av mottaket.
 * 
 * <p>Java class for MottakenhetBlokk_type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MottakenhetBlokk_type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="ediLoggId" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;whiteSpace value="preserve"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="avsender" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="ebXMLSamtaleId" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="mottaksId" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="meldingsType" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="avsenderRef" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="avsenderFnrFraDigSignatur"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;whiteSpace value="collapse"/&gt;
 *             &lt;minLength value="11"/&gt;
 *             &lt;maxLength value="11"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="mottattDatotid" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="orgNummer"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;minLength value="9"/&gt;
 *             &lt;maxLength value="9"/&gt;
 *             &lt;whiteSpace value="collapse"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="avsenderOrgNrFraDigSignatur"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;whiteSpace value="collapse"/&gt;
 *             &lt;minLength value="9"/&gt;
 *             &lt;maxLength value="9"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="partnerReferanse"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;whiteSpace value="collapse"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="herIdentifikator"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;whiteSpace value="collapse"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="ebRole"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;whiteSpace value="collapse"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="ebService"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;whiteSpace value="collapse"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="ebAction"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;whiteSpace value="collapse"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MottakenhetBlokk_type")
@XmlSeeAlso({
    XMLMottakenhetBlokk.class
})
public class XMLMottakenhetBlokkType {

    @XmlAttribute(name = "ediLoggId", required = true)
    protected String ediLoggId;
    @XmlAttribute(name = "avsender")
    protected String avsender;
    @XmlAttribute(name = "ebXMLSamtaleId")
    protected String ebXMLSamtaleId;
    @XmlAttribute(name = "mottaksId")
    protected String mottaksId;
    @XmlAttribute(name = "meldingsType")
    protected String meldingsType;
    @XmlAttribute(name = "avsenderRef")
    protected String avsenderRef;
    @XmlAttribute(name = "avsenderFnrFraDigSignatur")
    protected String avsenderFnrFraDigSignatur;
    @XmlAttribute(name = "mottattDatotid")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar mottattDatotid;
    @XmlAttribute(name = "orgNummer")
    protected String orgNummer;
    @XmlAttribute(name = "avsenderOrgNrFraDigSignatur")
    protected String avsenderOrgNrFraDigSignatur;
    @XmlAttribute(name = "partnerReferanse")
    protected String partnerReferanse;
    @XmlAttribute(name = "herIdentifikator")
    protected String herIdentifikator;
    @XmlAttribute(name = "ebRole")
    protected String ebRole;
    @XmlAttribute(name = "ebService")
    protected String ebService;
    @XmlAttribute(name = "ebAction")
    protected String ebAction;

    /**
     * Gets the value of the ediLoggId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEdiLoggId() {
        return ediLoggId;
    }

    /**
     * Sets the value of the ediLoggId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEdiLoggId(String value) {
        this.ediLoggId = value;
    }

    /**
     * Gets the value of the avsender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvsender() {
        return avsender;
    }

    /**
     * Sets the value of the avsender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvsender(String value) {
        this.avsender = value;
    }

    /**
     * Gets the value of the ebXMLSamtaleId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEbXMLSamtaleId() {
        return ebXMLSamtaleId;
    }

    /**
     * Sets the value of the ebXMLSamtaleId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEbXMLSamtaleId(String value) {
        this.ebXMLSamtaleId = value;
    }

    /**
     * Gets the value of the mottaksId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMottaksId() {
        return mottaksId;
    }

    /**
     * Sets the value of the mottaksId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMottaksId(String value) {
        this.mottaksId = value;
    }

    /**
     * Gets the value of the meldingsType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeldingsType() {
        return meldingsType;
    }

    /**
     * Sets the value of the meldingsType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeldingsType(String value) {
        this.meldingsType = value;
    }

    /**
     * Gets the value of the avsenderRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvsenderRef() {
        return avsenderRef;
    }

    /**
     * Sets the value of the avsenderRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvsenderRef(String value) {
        this.avsenderRef = value;
    }

    /**
     * Gets the value of the avsenderFnrFraDigSignatur property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvsenderFnrFraDigSignatur() {
        return avsenderFnrFraDigSignatur;
    }

    /**
     * Sets the value of the avsenderFnrFraDigSignatur property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvsenderFnrFraDigSignatur(String value) {
        this.avsenderFnrFraDigSignatur = value;
    }

    /**
     * Gets the value of the mottattDatotid property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMottattDatotid() {
        return mottattDatotid;
    }

    /**
     * Sets the value of the mottattDatotid property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMottattDatotid(XMLGregorianCalendar value) {
        this.mottattDatotid = value;
    }

    /**
     * Gets the value of the orgNummer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgNummer() {
        return orgNummer;
    }

    /**
     * Sets the value of the orgNummer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgNummer(String value) {
        this.orgNummer = value;
    }

    /**
     * Gets the value of the avsenderOrgNrFraDigSignatur property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAvsenderOrgNrFraDigSignatur() {
        return avsenderOrgNrFraDigSignatur;
    }

    /**
     * Sets the value of the avsenderOrgNrFraDigSignatur property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAvsenderOrgNrFraDigSignatur(String value) {
        this.avsenderOrgNrFraDigSignatur = value;
    }

    /**
     * Gets the value of the partnerReferanse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartnerReferanse() {
        return partnerReferanse;
    }

    /**
     * Sets the value of the partnerReferanse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartnerReferanse(String value) {
        this.partnerReferanse = value;
    }

    /**
     * Gets the value of the herIdentifikator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHerIdentifikator() {
        return herIdentifikator;
    }

    /**
     * Sets the value of the herIdentifikator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHerIdentifikator(String value) {
        this.herIdentifikator = value;
    }

    /**
     * Gets the value of the ebRole property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEbRole() {
        return ebRole;
    }

    /**
     * Sets the value of the ebRole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEbRole(String value) {
        this.ebRole = value;
    }

    /**
     * Gets the value of the ebService property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEbService() {
        return ebService;
    }

    /**
     * Sets the value of the ebService property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEbService(String value) {
        this.ebService = value;
    }

    /**
     * Gets the value of the ebAction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEbAction() {
        return ebAction;
    }

    /**
     * Sets the value of the ebAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEbAction(String value) {
        this.ebAction = value;
    }

}
