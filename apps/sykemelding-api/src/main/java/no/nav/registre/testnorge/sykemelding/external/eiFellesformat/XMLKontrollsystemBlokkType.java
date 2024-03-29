//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0.1 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.09.09 at 06:50:45 AM UTC 
//


package no.nav.registre.testnorge.sykemelding.external.eiFellesformat;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * Denne blokken inneholder resultater fra kontrollsystemets regelbehandling av den motatte meldingen og annen informasjon som er innhentet gjennom prosesseringen av en melding
 * 
 * <p>Java class for KontrollsystemBlokk_type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KontrollsystemBlokk_type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NavnFraFolkeregister" type="{http://www.trygdeetaten.no/xml/eiff/1/}typeNavnFF" minOccurs="0"/&gt;
 *         &lt;element name="AvsenderNavn" type="{http://www.trygdeetaten.no/xml/eiff/1/}typeNavnFF" minOccurs="0"/&gt;
 *         &lt;element name="InfotrygdBlokk" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;extension base="{http://www.trygdeetaten.no/xml/eiff/1/}typeInfotrygdBlokk"&gt;
 *               &lt;/extension&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="KontrollsystemSvarBlokk" type="{http://www.trygdeetaten.no/xml/eiff/1/}typeSystemSvarBlokkFF" minOccurs="0"/&gt;
 *         &lt;element name="ArkivBlokk" type="{http://www.trygdeetaten.no/xml/eiff/1/}typeArkivBlokk" minOccurs="0"/&gt;
 *         &lt;element name="Prosessdata" type="{http://www.trygdeetaten.no/xml/eiff/1/}ProsessdataElement_type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KontrollsystemBlokk_type", propOrder = {
    "navnFraFolkeregister",
    "avsenderNavn",
    "infotrygdBlokk",
    "kontrollsystemSvarBlokk",
    "arkivBlokk",
    "prosessdata"
})
@XmlSeeAlso({
    XMLKontrollSystemBlokk.class
})
public class XMLKontrollsystemBlokkType {

    @XmlElement(name = "NavnFraFolkeregister")
    protected XMLTypeNavnFF navnFraFolkeregister;
    @XmlElement(name = "AvsenderNavn")
    protected XMLTypeNavnFF avsenderNavn;
    @XmlElement(name = "InfotrygdBlokk")
    protected List<InfotrygdBlokk> infotrygdBlokk;
    @XmlElement(name = "KontrollsystemSvarBlokk")
    protected XMLTypeSystemSvarBlokkFF kontrollsystemSvarBlokk;
    @XmlElement(name = "ArkivBlokk")
    protected XMLTypeArkivBlokk arkivBlokk;
    @XmlElement(name = "Prosessdata")
    protected List<XMLProsessdataElementType> prosessdata;

    /**
     * Gets the value of the navnFraFolkeregister property.
     * 
     * @return
     *     possible object is
     *     {@link XMLTypeNavnFF }
     *     
     */
    public XMLTypeNavnFF getNavnFraFolkeregister() {
        return navnFraFolkeregister;
    }

    /**
     * Sets the value of the navnFraFolkeregister property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLTypeNavnFF }
     *     
     */
    public void setNavnFraFolkeregister(XMLTypeNavnFF value) {
        this.navnFraFolkeregister = value;
    }

    /**
     * Gets the value of the avsenderNavn property.
     * 
     * @return
     *     possible object is
     *     {@link XMLTypeNavnFF }
     *     
     */
    public XMLTypeNavnFF getAvsenderNavn() {
        return avsenderNavn;
    }

    /**
     * Sets the value of the avsenderNavn property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLTypeNavnFF }
     *     
     */
    public void setAvsenderNavn(XMLTypeNavnFF value) {
        this.avsenderNavn = value;
    }

    /**
     * Gets the value of the infotrygdBlokk property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the infotrygdBlokk property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInfotrygdBlokk().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InfotrygdBlokk }
     * 
     * 
     */
    public List<InfotrygdBlokk> getInfotrygdBlokk() {
        if (infotrygdBlokk == null) {
            infotrygdBlokk = new ArrayList<InfotrygdBlokk>();
        }
        return this.infotrygdBlokk;
    }

    /**
     * Gets the value of the kontrollsystemSvarBlokk property.
     * 
     * @return
     *     possible object is
     *     {@link XMLTypeSystemSvarBlokkFF }
     *     
     */
    public XMLTypeSystemSvarBlokkFF getKontrollsystemSvarBlokk() {
        return kontrollsystemSvarBlokk;
    }

    /**
     * Sets the value of the kontrollsystemSvarBlokk property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLTypeSystemSvarBlokkFF }
     *     
     */
    public void setKontrollsystemSvarBlokk(XMLTypeSystemSvarBlokkFF value) {
        this.kontrollsystemSvarBlokk = value;
    }

    /**
     * Gets the value of the arkivBlokk property.
     * 
     * @return
     *     possible object is
     *     {@link XMLTypeArkivBlokk }
     *     
     */
    public XMLTypeArkivBlokk getArkivBlokk() {
        return arkivBlokk;
    }

    /**
     * Sets the value of the arkivBlokk property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLTypeArkivBlokk }
     *     
     */
    public void setArkivBlokk(XMLTypeArkivBlokk value) {
        this.arkivBlokk = value;
    }

    /**
     * Gets the value of the prosessdata property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the prosessdata property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProsessdata().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XMLProsessdataElementType }
     * 
     * 
     */
    public List<XMLProsessdataElementType> getProsessdata() {
        if (prosessdata == null) {
            prosessdata = new ArrayList<XMLProsessdataElementType>();
        }
        return this.prosessdata;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;extension base="{http://www.trygdeetaten.no/xml/eiff/1/}typeInfotrygdBlokk"&gt;
     *     &lt;/extension&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class InfotrygdBlokk
        extends XMLTypeInfotrygdBlokk
    {


    }

}
