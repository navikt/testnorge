//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.09.24 at 10:58:31 PM UTC 
//


package no.nav.registre.testnorge.sykemelding.external.xmlstds;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the no.kith.xmlstds package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _REF_QNAME = new QName("http://www.kith.no/xmlstds", "REF");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: no.kith.xmlstds
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link XMLCS }
     * 
     */
    public XMLCS createXMLCS() {
        return new XMLCS();
    }

    /**
     * Create an instance of {@link XMLPQ }
     * 
     */
    public XMLPQ createXMLPQ() {
        return new XMLPQ();
    }

    /**
     * Create an instance of {@link XMLREAL }
     * 
     */
    public XMLREAL createXMLREAL() {
        return new XMLREAL();
    }

    /**
     * Create an instance of {@link XMLTN }
     * 
     */
    public XMLTN createXMLTN() {
        return new XMLTN();
    }

    /**
     * Create an instance of {@link XMLBL }
     * 
     */
    public XMLBL createXMLBL() {
        return new XMLBL();
    }

    /**
     * Create an instance of {@link XMLCV }
     * 
     */
    public XMLCV createXMLCV() {
        return new XMLCV();
    }

    /**
     * Create an instance of {@link XMLRTO }
     * 
     */
    public XMLRTO createXMLRTO() {
        return new XMLRTO();
    }

    /**
     * Create an instance of {@link XMLMO }
     * 
     */
    public XMLMO createXMLMO() {
        return new XMLMO();
    }

    /**
     * Create an instance of {@link XMLED }
     * 
     */
    public XMLED createXMLED() {
        return new XMLED();
    }

    /**
     * Create an instance of {@link XMLURL }
     * 
     */
    public XMLURL createXMLURL() {
        return new XMLURL();
    }

    /**
     * Create an instance of {@link XMLTS }
     * 
     */
    public XMLTS createXMLTS() {
        return new XMLTS();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLURL }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kith.no/xmlstds", name = "REF")
    public JAXBElement<XMLURL> createREF(XMLURL value) {
        return new JAXBElement<XMLURL>(_REF_QNAME, XMLURL.class, null, value);
    }

}
