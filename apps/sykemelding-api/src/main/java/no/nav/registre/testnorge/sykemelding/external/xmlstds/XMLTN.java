//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.09.24 at 10:58:31 PM UTC 
//


package no.nav.registre.testnorge.sykemelding.external.xmlstds;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for TN complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TN">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.kith.no/xmlstds}ED">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.kith.no/xmlstds}REF" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.kith.no/xmlstds}COMPN"/>
 *       &lt;attribute ref="{http://www.kith.no/xmlstds}IC"/>
 *       &lt;attribute ref="{http://www.kith.no/xmlstds}ICA"/>
 *       &lt;attribute ref="{http://www.kith.no/xmlstds}NULL"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TN", propOrder = {
    "ref"
})
public class XMLTN
    extends XMLED
{

    @XmlElement(name = "REF", namespace = "http://www.kith.no/xmlstds")
    protected XMLURL ref;
    @XmlAttribute(name = "COMPN", namespace = "http://www.kith.no/xmlstds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String compn;
    @XmlAttribute(name = "IC", namespace = "http://www.kith.no/xmlstds")
    protected byte[] ic;
    @XmlAttribute(name = "ICA", namespace = "http://www.kith.no/xmlstds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String ica;
    @XmlAttribute(name = "NULL", namespace = "http://www.kith.no/xmlstds")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String _null;

    /**
     * Default no-arg constructor
     * 
     */
    public XMLTN() {
        super();
    }

    /**
     * Fully-initialising value constructor
     * 
     */
    public XMLTN(final String mt, final XMLURL ref, final String compn, final byte[] ic, final String ica, final String _null) {
        super(mt);
        this.ref = ref;
        this.compn = compn;
        this.ic = ic;
        this.ica = ica;
        this._null = _null;
    }

    /**
     * Gets the value of the ref property.
     * 
     * @return
     *     possible object is
     *     {@link XMLURL }
     *     
     */
    public XMLURL getREF() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLURL }
     *     
     */
    public void setREF(XMLURL value) {
        this.ref = value;
    }

    /**
     * Gets the value of the compn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOMPN() {
        return compn;
    }

    /**
     * Sets the value of the compn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOMPN(String value) {
        this.compn = value;
    }

    /**
     * Gets the value of the ic property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getIC() {
        return ic;
    }

    /**
     * Sets the value of the ic property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setIC(byte[] value) {
        this.ic = ((byte[]) value);
    }

    /**
     * Gets the value of the ica property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getICA() {
        if (ica == null) {
            return "SHA-1";
        } else {
            return ica;
        }
    }

    /**
     * Sets the value of the ica property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setICA(String value) {
        this.ica = value;
    }

    /**
     * Gets the value of the null property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNULL() {
        return _null;
    }

    /**
     * Sets the value of the null property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNULL(String value) {
        this._null = value;
    }

    public XMLTN withREF(XMLURL value) {
        setREF(value);
        return this;
    }

    public XMLTN withCOMPN(String value) {
        setCOMPN(value);
        return this;
    }

    public XMLTN withIC(byte[] value) {
        setIC(value);
        return this;
    }

    public XMLTN withICA(String value) {
        setICA(value);
        return this;
    }

    public XMLTN withNULL(String value) {
        setNULL(value);
        return this;
    }

    @Override
    public XMLTN withMT(String value) {
        setMT(value);
        return this;
    }

}
