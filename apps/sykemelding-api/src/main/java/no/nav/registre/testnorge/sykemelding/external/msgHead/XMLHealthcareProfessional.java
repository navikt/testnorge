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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDate;
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
 *         &lt;element name="TypeHealthcareProfessional" type="{http://www.kith.no/xmlstds/msghead/2006-05-24}CS" minOccurs="0"/&gt;
 *         &lt;element name="RoleToPatient" type="{http://www.kith.no/xmlstds/msghead/2006-05-24}CV" minOccurs="0"/&gt;
 *         &lt;element name="FamilyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="MiddleName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="GivenName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DateOfBirth" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="Sex" type="{http://www.kith.no/xmlstds/msghead/2006-05-24}CS" minOccurs="0"/&gt;
 *         &lt;element name="Nationality" type="{http://www.kith.no/xmlstds/msghead/2006-05-24}CS" minOccurs="0"/&gt;
 *         &lt;element name="Ident" type="{http://www.kith.no/xmlstds/msghead/2006-05-24}Ident" maxOccurs="unbounded"/&gt;
 *         &lt;element name="Address" type="{http://www.kith.no/xmlstds/msghead/2006-05-24}Address" minOccurs="0"/&gt;
 *         &lt;element name="TeleCom" type="{http://www.kith.no/xmlstds/msghead/2006-05-24}TeleCom" maxOccurs="unbounded" minOccurs="0"/&gt;
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
    "typeHealthcareProfessional",
    "roleToPatient",
    "familyName",
    "middleName",
    "givenName",
    "dateOfBirth",
    "sex",
    "nationality",
    "ident",
    "address",
    "teleCom"
})
@XmlRootElement(name = "HealthcareProfessional", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24")
public class XMLHealthcareProfessional {

    @XmlElement(name = "TypeHealthcareProfessional", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24")
    protected XMLCS typeHealthcareProfessional;
    @XmlElement(name = "RoleToPatient", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24")
    protected XMLCV roleToPatient;
    @XmlElement(name = "FamilyName", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24")
    protected String familyName;
    @XmlElement(name = "MiddleName", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24")
    protected String middleName;
    @XmlElement(name = "GivenName", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24")
    protected String givenName;
    @XmlElement(name = "DateOfBirth", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24", type = String.class)
    @XmlJavaTypeAdapter(XmlAdapter.class)
    @XmlSchemaType(name = "date")
    protected LocalDate dateOfBirth;
    @XmlElement(name = "Sex", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24")
    protected XMLCS sex;
    @XmlElement(name = "Nationality", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24")
    protected XMLCS nationality;
    @XmlElement(name = "Ident", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24", required = true)
    protected List<XMLIdent> ident;
    @XmlElement(name = "Address", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24")
    protected XMLAddress address;
    @XmlElement(name = "TeleCom", namespace = "http://www.kith.no/xmlstds/msghead/2006-05-24")
    protected List<XMLTeleCom> teleCom;

    /**
     * Gets the value of the typeHealthcareProfessional property.
     * 
     * @return
     *     possible object is
     *     {@link XMLCS }
     *     
     */
    public XMLCS getTypeHealthcareProfessional() {
        return typeHealthcareProfessional;
    }

    /**
     * Sets the value of the typeHealthcareProfessional property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLCS }
     *     
     */
    public void setTypeHealthcareProfessional(XMLCS value) {
        this.typeHealthcareProfessional = value;
    }

    /**
     * Gets the value of the roleToPatient property.
     * 
     * @return
     *     possible object is
     *     {@link XMLCV }
     *     
     */
    public XMLCV getRoleToPatient() {
        return roleToPatient;
    }

    /**
     * Sets the value of the roleToPatient property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLCV }
     *     
     */
    public void setRoleToPatient(XMLCV value) {
        this.roleToPatient = value;
    }

    /**
     * Gets the value of the familyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFamilyName() {
        return familyName;
    }

    /**
     * Sets the value of the familyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFamilyName(String value) {
        this.familyName = value;
    }

    /**
     * Gets the value of the middleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the value of the middleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddleName(String value) {
        this.middleName = value;
    }

    /**
     * Gets the value of the givenName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGivenName() {
        return givenName;
    }

    /**
     * Sets the value of the givenName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGivenName(String value) {
        this.givenName = value;
    }

    /**
     * Gets the value of the dateOfBirth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the value of the dateOfBirth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateOfBirth(LocalDate value) {
        this.dateOfBirth = value;
    }

    /**
     * Gets the value of the sex property.
     * 
     * @return
     *     possible object is
     *     {@link XMLCS }
     *     
     */
    public XMLCS getSex() {
        return sex;
    }

    /**
     * Sets the value of the sex property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLCS }
     *     
     */
    public void setSex(XMLCS value) {
        this.sex = value;
    }

    /**
     * Gets the value of the nationality property.
     * 
     * @return
     *     possible object is
     *     {@link XMLCS }
     *     
     */
    public XMLCS getNationality() {
        return nationality;
    }

    /**
     * Sets the value of the nationality property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLCS }
     *     
     */
    public void setNationality(XMLCS value) {
        this.nationality = value;
    }

    /**
     * Gets the value of the ident property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ident property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XMLIdent }
     * 
     * 
     */
    public List<XMLIdent> getIdent() {
        if (ident == null) {
            ident = new ArrayList<XMLIdent>();
        }
        return this.ident;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link XMLAddress }
     *     
     */
    public XMLAddress getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLAddress }
     *     
     */
    public void setAddress(XMLAddress value) {
        this.address = value;
    }

    /**
     * Gets the value of the teleCom property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the teleCom property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTeleCom().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XMLTeleCom }
     * 
     * 
     */
    public List<XMLTeleCom> getTeleCom() {
        if (teleCom == null) {
            teleCom = new ArrayList<XMLTeleCom>();
        }
        return this.teleCom;
    }

}
