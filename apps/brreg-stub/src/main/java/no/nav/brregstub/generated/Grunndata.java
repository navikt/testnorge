
package no.nav.brregstub.generated;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;

import javax.xml.datatype.XMLGregorianCalendar;
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
 *         &lt;element name="responseHeader"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="orgnr" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *                   &lt;element name="hovedStatus" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *                   &lt;element name="underStatus"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="underStatusMelding" maxOccurs="unbounded"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;simpleContent&gt;
 *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *                                     &lt;attribute name="kode" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *                                   &lt;/extension&gt;
 *                                 &lt;/simpleContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *                 &lt;attribute name="prossessDato" use="required" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
 *                 &lt;attribute name="tjeneste" use="required"&gt;
 *                   &lt;simpleType&gt;
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                       &lt;minLength value="1"/&gt;
 *                       &lt;maxLength value="40"/&gt;
 *                     &lt;/restriction&gt;
 *                   &lt;/simpleType&gt;
 *                 &lt;/attribute&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="melding" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="organisasjonsnummer"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;simpleContent&gt;
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *                           &lt;attribute name="registreringsDato" use="required" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
 *                         &lt;/extension&gt;
 *                       &lt;/simpleContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="kontaktperson" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;group ref="{}samendring"/&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="deltakere" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;group ref="{}samendring"/&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="komplementar" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;group ref="{}samendring"/&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="sameiere" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;group ref="{}samendring"/&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="styre" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;group ref="{}samendring"/&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="revisor" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;group ref="{}samendring"/&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="regnskapsfoerer" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;group ref="{}samendring"/&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="eierkommune" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;group ref="{}samendring"/&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *                 &lt;attribute name="tjeneste" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
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
    "responseHeader",
    "melding"
})
@XmlRootElement(name = "grunndata")
public class Grunndata {

    @XmlElement(required = true)
    protected Grunndata.ResponseHeader responseHeader;
    protected Grunndata.Melding melding;

    /**
     * Gets the value of the responseHeader property.
     * 
     * @return
     *     possible object is
     *     {@link Grunndata.ResponseHeader }
     *     
     */
    public Grunndata.ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    /**
     * Sets the value of the responseHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link Grunndata.ResponseHeader }
     *     
     */
    public void setResponseHeader(Grunndata.ResponseHeader value) {
        this.responseHeader = value;
    }

    /**
     * Gets the value of the melding property.
     * 
     * @return
     *     possible object is
     *     {@link Grunndata.Melding }
     *     
     */
    public Grunndata.Melding getMelding() {
        return melding;
    }

    /**
     * Sets the value of the melding property.
     * 
     * @param value
     *     allowed object is
     *     {@link Grunndata.Melding }
     *     
     */
    public void setMelding(Grunndata.Melding value) {
        this.melding = value;
    }


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
     *         &lt;element name="organisasjonsnummer"&gt;
     *           &lt;complexType&gt;
     *             &lt;simpleContent&gt;
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
     *                 &lt;attribute name="registreringsDato" use="required" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
     *               &lt;/extension&gt;
     *             &lt;/simpleContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="kontaktperson" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;group ref="{}samendring"/&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="deltakere" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;group ref="{}samendring"/&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="komplementar" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;group ref="{}samendring"/&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="sameiere" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;group ref="{}samendring"/&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="styre" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;group ref="{}samendring"/&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="revisor" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;group ref="{}samendring"/&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="regnskapsfoerer" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;group ref="{}samendring"/&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="eierkommune" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;group ref="{}samendring"/&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *       &lt;attribute name="tjeneste" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "organisasjonsnummer",
        "kontaktperson",
        "deltakere",
        "komplementar",
        "sameiere",
        "styre",
        "revisor",
        "regnskapsfoerer",
        "eierkommune"
    })
    public static class Melding {

        @XmlElement(required = true)
        protected Grunndata.Melding.Organisasjonsnummer organisasjonsnummer;
        protected Grunndata.Melding.Kontaktperson kontaktperson;
        protected Grunndata.Melding.Deltakere deltakere;
        protected Grunndata.Melding.Komplementar komplementar;
        protected Grunndata.Melding.Sameiere sameiere;
        protected Grunndata.Melding.Styre styre;
        protected Grunndata.Melding.Revisor revisor;
        protected Grunndata.Melding.Regnskapsfoerer regnskapsfoerer;
        protected Grunndata.Melding.Eierkommune eierkommune;
        @XmlAttribute(name = "tjeneste", required = true)
        protected String tjeneste;

        /**
         * Gets the value of the organisasjonsnummer property.
         * 
         * @return
         *     possible object is
         *     {@link Grunndata.Melding.Organisasjonsnummer }
         *     
         */
        public Grunndata.Melding.Organisasjonsnummer getOrganisasjonsnummer() {
            return organisasjonsnummer;
        }

        /**
         * Sets the value of the organisasjonsnummer property.
         * 
         * @param value
         *     allowed object is
         *     {@link Grunndata.Melding.Organisasjonsnummer }
         *     
         */
        public void setOrganisasjonsnummer(Grunndata.Melding.Organisasjonsnummer value) {
            this.organisasjonsnummer = value;
        }

        /**
         * Gets the value of the kontaktperson property.
         * 
         * @return
         *     possible object is
         *     {@link Grunndata.Melding.Kontaktperson }
         *     
         */
        public Grunndata.Melding.Kontaktperson getKontaktperson() {
            return kontaktperson;
        }

        /**
         * Sets the value of the kontaktperson property.
         * 
         * @param value
         *     allowed object is
         *     {@link Grunndata.Melding.Kontaktperson }
         *     
         */
        public void setKontaktperson(Grunndata.Melding.Kontaktperson value) {
            this.kontaktperson = value;
        }

        /**
         * Gets the value of the deltakere property.
         * 
         * @return
         *     possible object is
         *     {@link Grunndata.Melding.Deltakere }
         *     
         */
        public Grunndata.Melding.Deltakere getDeltakere() {
            return deltakere;
        }

        /**
         * Sets the value of the deltakere property.
         * 
         * @param value
         *     allowed object is
         *     {@link Grunndata.Melding.Deltakere }
         *     
         */
        public void setDeltakere(Grunndata.Melding.Deltakere value) {
            this.deltakere = value;
        }

        /**
         * Gets the value of the komplementar property.
         * 
         * @return
         *     possible object is
         *     {@link Grunndata.Melding.Komplementar }
         *     
         */
        public Grunndata.Melding.Komplementar getKomplementar() {
            return komplementar;
        }

        /**
         * Sets the value of the komplementar property.
         * 
         * @param value
         *     allowed object is
         *     {@link Grunndata.Melding.Komplementar }
         *     
         */
        public void setKomplementar(Grunndata.Melding.Komplementar value) {
            this.komplementar = value;
        }

        /**
         * Gets the value of the sameiere property.
         * 
         * @return
         *     possible object is
         *     {@link Grunndata.Melding.Sameiere }
         *     
         */
        public Grunndata.Melding.Sameiere getSameiere() {
            return sameiere;
        }

        /**
         * Sets the value of the sameiere property.
         * 
         * @param value
         *     allowed object is
         *     {@link Grunndata.Melding.Sameiere }
         *     
         */
        public void setSameiere(Grunndata.Melding.Sameiere value) {
            this.sameiere = value;
        }

        /**
         * Gets the value of the styre property.
         * 
         * @return
         *     possible object is
         *     {@link Grunndata.Melding.Styre }
         *     
         */
        public Grunndata.Melding.Styre getStyre() {
            return styre;
        }

        /**
         * Sets the value of the styre property.
         * 
         * @param value
         *     allowed object is
         *     {@link Grunndata.Melding.Styre }
         *     
         */
        public void setStyre(Grunndata.Melding.Styre value) {
            this.styre = value;
        }

        /**
         * Gets the value of the revisor property.
         * 
         * @return
         *     possible object is
         *     {@link Grunndata.Melding.Revisor }
         *     
         */
        public Grunndata.Melding.Revisor getRevisor() {
            return revisor;
        }

        /**
         * Sets the value of the revisor property.
         * 
         * @param value
         *     allowed object is
         *     {@link Grunndata.Melding.Revisor }
         *     
         */
        public void setRevisor(Grunndata.Melding.Revisor value) {
            this.revisor = value;
        }

        /**
         * Gets the value of the regnskapsfoerer property.
         * 
         * @return
         *     possible object is
         *     {@link Grunndata.Melding.Regnskapsfoerer }
         *     
         */
        public Grunndata.Melding.Regnskapsfoerer getRegnskapsfoerer() {
            return regnskapsfoerer;
        }

        /**
         * Sets the value of the regnskapsfoerer property.
         * 
         * @param value
         *     allowed object is
         *     {@link Grunndata.Melding.Regnskapsfoerer }
         *     
         */
        public void setRegnskapsfoerer(Grunndata.Melding.Regnskapsfoerer value) {
            this.regnskapsfoerer = value;
        }

        /**
         * Gets the value of the eierkommune property.
         * 
         * @return
         *     possible object is
         *     {@link Grunndata.Melding.Eierkommune }
         *     
         */
        public Grunndata.Melding.Eierkommune getEierkommune() {
            return eierkommune;
        }

        /**
         * Sets the value of the eierkommune property.
         * 
         * @param value
         *     allowed object is
         *     {@link Grunndata.Melding.Eierkommune }
         *     
         */
        public void setEierkommune(Grunndata.Melding.Eierkommune value) {
            this.eierkommune = value;
        }

        /**
         * Gets the value of the tjeneste property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTjeneste() {
            return tjeneste;
        }

        /**
         * Sets the value of the tjeneste property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTjeneste(String value) {
            this.tjeneste = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;group ref="{}samendring"/&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "samendring"
        })
        public static class Deltakere {

            @XmlElement(required = true)
            protected List<Grunndata.Melding.Eierkommune.Samendring> samendring;

            /**
             * Gets the value of the samendring property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the Jakarta XML Binding object.
             * This is why there is not a <CODE>set</CODE> method for the samendring property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getSamendring().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Grunndata.Melding.Eierkommune.Samendring }
             * 
             * 
             */
            public List<Grunndata.Melding.Eierkommune.Samendring> getSamendring() {
                if (samendring == null) {
                    samendring = new ArrayList<Grunndata.Melding.Eierkommune.Samendring>();
                }
                return this.samendring;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;group ref="{}samendring"/&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "samendring"
        })
        public static class Eierkommune {

            @XmlElement(required = true)
            protected List<Grunndata.Melding.Eierkommune.Samendring> samendring;

            /**
             * Gets the value of the samendring property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the Jakarta XML Binding object.
             * This is why there is not a <CODE>set</CODE> method for the samendring property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getSamendring().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Grunndata.Melding.Eierkommune.Samendring }
             * 
             * 
             */
            public List<Grunndata.Melding.Eierkommune.Samendring> getSamendring() {
                if (samendring == null) {
                    samendring = new ArrayList<Grunndata.Melding.Eierkommune.Samendring>();
                }
                return this.samendring;
            }


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
             *         &lt;element name="headerTekst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
             *         &lt;element name="rolle" maxOccurs="unbounded" minOccurs="0"&gt;
             *           &lt;complexType&gt;
             *             &lt;complexContent&gt;
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
             *                 &lt;sequence&gt;
             *                   &lt;element name="person" maxOccurs="unbounded" minOccurs="0"&gt;
             *                     &lt;complexType&gt;
             *                       &lt;complexContent&gt;
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
             *                           &lt;sequence&gt;
             *                             &lt;element name="vergeTekst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
             *                             &lt;choice&gt;
             *                               &lt;element name="fodselsnr"&gt;
             *                                 &lt;simpleType&gt;
             *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                     &lt;length value="11"/&gt;
             *                                   &lt;/restriction&gt;
             *                                 &lt;/simpleType&gt;
             *                               &lt;/element&gt;
             *                               &lt;element name="fodselsdato"&gt;
             *                                 &lt;simpleType&gt;
             *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                     &lt;length value="10"/&gt;
             *                                   &lt;/restriction&gt;
             *                                 &lt;/simpleType&gt;
             *                               &lt;/element&gt;
             *                             &lt;/choice&gt;
             *                             &lt;element name="fornavn"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="1"/&gt;
             *                                   &lt;maxLength value="50"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="mellomnavn" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="1"/&gt;
             *                                   &lt;maxLength value="50"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="slektsnavn"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="1"/&gt;
             *                                   &lt;maxLength value="50"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="adresse1" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="1"/&gt;
             *                                   &lt;maxLength value="35"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="adresse2" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="1"/&gt;
             *                                   &lt;maxLength value="35"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="adresse3" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="1"/&gt;
             *                                   &lt;maxLength value="35"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="postnr" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="4"/&gt;
             *                                   &lt;maxLength value="9"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="poststed" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="1"/&gt;
             *                                   &lt;maxLength value="35"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="land" minOccurs="0"&gt;
             *                               &lt;complexType&gt;
             *                                 &lt;simpleContent&gt;
             *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
             *                                     &lt;attribute name="landkode4" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
             *                                   &lt;/extension&gt;
             *                                 &lt;/simpleContent&gt;
             *                               &lt;/complexType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="valgtAv" minOccurs="0"&gt;
             *                               &lt;complexType&gt;
             *                                 &lt;simpleContent&gt;
             *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
             *                                     &lt;attribute name="kode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
             *                                   &lt;/extension&gt;
             *                                 &lt;/simpleContent&gt;
             *                               &lt;/complexType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="ansvarsandel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
             *                             &lt;element name="fratraadt" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;length value="1"/&gt;
             *                                   &lt;enumeration value="K"/&gt;
             *                                   &lt;enumeration value="R"/&gt;
             *                                   &lt;enumeration value="F"/&gt;
             *                                   &lt;enumeration value="N"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="fratraadtTekst" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;length value="8"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="revisorKategori" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
             *                             &lt;element name="revisorRkode" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
             *                             &lt;element name="reTekst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
             *                             &lt;element name="rolleFritekst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
             *                           &lt;/sequence&gt;
             *                           &lt;attribute name="beskrivelse" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
             *                           &lt;attribute name="statuskode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
             *                         &lt;/restriction&gt;
             *                       &lt;/complexContent&gt;
             *                     &lt;/complexType&gt;
             *                   &lt;/element&gt;
             *                   &lt;element name="enhet" maxOccurs="unbounded" minOccurs="0"&gt;
             *                     &lt;complexType&gt;
             *                       &lt;complexContent&gt;
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
             *                           &lt;sequence&gt;
             *                             &lt;element name="orgnr"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;length value="9"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="navn1"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="1"/&gt;
             *                                   &lt;maxLength value="35"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="navn2" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="1"/&gt;
             *                                   &lt;maxLength value="35"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="navn3" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="1"/&gt;
             *                                   &lt;maxLength value="35"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="navn4" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="1"/&gt;
             *                                   &lt;maxLength value="35"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="navn5" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="1"/&gt;
             *                                   &lt;maxLength value="35"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="adresse1" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="1"/&gt;
             *                                   &lt;maxLength value="35"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="adresse2" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="1"/&gt;
             *                                   &lt;maxLength value="35"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="adresse3" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="1"/&gt;
             *                                   &lt;maxLength value="35"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="postnr" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="4"/&gt;
             *                                   &lt;maxLength value="9"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="poststed" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;minLength value="1"/&gt;
             *                                   &lt;maxLength value="35"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="land" minOccurs="0"&gt;
             *                               &lt;complexType&gt;
             *                                 &lt;simpleContent&gt;
             *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
             *                                     &lt;attribute name="landkode4" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
             *                                   &lt;/extension&gt;
             *                                 &lt;/simpleContent&gt;
             *                               &lt;/complexType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="valgtAv" minOccurs="0"&gt;
             *                               &lt;complexType&gt;
             *                                 &lt;simpleContent&gt;
             *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
             *                                     &lt;attribute name="kode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
             *                                   &lt;/extension&gt;
             *                                 &lt;/simpleContent&gt;
             *                               &lt;/complexType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="ansvarsandel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
             *                             &lt;element name="fratraadt" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;length value="1"/&gt;
             *                                   &lt;enumeration value="K"/&gt;
             *                                   &lt;enumeration value="R"/&gt;
             *                                   &lt;enumeration value="F"/&gt;
             *                                   &lt;enumeration value="N"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="fratraadtTekst" minOccurs="0"&gt;
             *                               &lt;simpleType&gt;
             *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
             *                                   &lt;length value="8"/&gt;
             *                                 &lt;/restriction&gt;
             *                               &lt;/simpleType&gt;
             *                             &lt;/element&gt;
             *                             &lt;element name="revisorKategori" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
             *                             &lt;element name="revisorRkode" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
             *                             &lt;element name="reTekst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
             *                             &lt;element name="rolleFritekst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
             *                           &lt;/sequence&gt;
             *                           &lt;attribute name="beskrivelse" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
             *                           &lt;attribute name="statuskode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
             *                         &lt;/restriction&gt;
             *                       &lt;/complexContent&gt;
             *                     &lt;/complexType&gt;
             *                   &lt;/element&gt;
             *                 &lt;/sequence&gt;
             *                 &lt;attribute name="beskrivelse" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
             *                 &lt;attribute name="rolletype" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
             *               &lt;/restriction&gt;
             *             &lt;/complexContent&gt;
             *           &lt;/complexType&gt;
             *         &lt;/element&gt;
             *         &lt;element name="trailerTekst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
             *       &lt;/sequence&gt;
             *       &lt;attribute name="registreringsDato" use="required" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
             *       &lt;attribute name="beskrivelse" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
             *       &lt;attribute name="samendringstype" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
             *       &lt;attribute name="kjonnsrepresentasjon" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
             *     &lt;/restriction&gt;
             *   &lt;/complexContent&gt;
             * &lt;/complexType&gt;
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "headerTekst",
                "rolle",
                "trailerTekst"
            })
            public static class Samendring {

                protected String headerTekst;
                protected List<Grunndata.Melding.Eierkommune.Samendring.Rolle> rolle;
                protected String trailerTekst;
                @XmlAttribute(name = "registreringsDato", required = true)
                @XmlSchemaType(name = "date")
                protected XMLGregorianCalendar registreringsDato;
                @XmlAttribute(name = "beskrivelse", required = true)
                protected String beskrivelse;
                @XmlAttribute(name = "samendringstype", required = true)
                protected String samendringstype;
                @XmlAttribute(name = "kjonnsrepresentasjon")
                protected String kjonnsrepresentasjon;

                /**
                 * Gets the value of the headerTekst property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getHeaderTekst() {
                    return headerTekst;
                }

                /**
                 * Sets the value of the headerTekst property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setHeaderTekst(String value) {
                    this.headerTekst = value;
                }

                /**
                 * Gets the value of the rolle property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the Jakarta XML Binding object.
                 * This is why there is not a <CODE>set</CODE> method for the rolle property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getRolle().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link Grunndata.Melding.Eierkommune.Samendring.Rolle }
                 * 
                 * 
                 */
                public List<Grunndata.Melding.Eierkommune.Samendring.Rolle> getRolle() {
                    if (rolle == null) {
                        rolle = new ArrayList<Grunndata.Melding.Eierkommune.Samendring.Rolle>();
                    }
                    return this.rolle;
                }

                /**
                 * Gets the value of the trailerTekst property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getTrailerTekst() {
                    return trailerTekst;
                }

                /**
                 * Sets the value of the trailerTekst property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setTrailerTekst(String value) {
                    this.trailerTekst = value;
                }

                /**
                 * Gets the value of the registreringsDato property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public XMLGregorianCalendar getRegistreringsDato() {
                    return registreringsDato;
                }

                /**
                 * Sets the value of the registreringsDato property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public void setRegistreringsDato(XMLGregorianCalendar value) {
                    this.registreringsDato = value;
                }

                /**
                 * Gets the value of the beskrivelse property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getBeskrivelse() {
                    return beskrivelse;
                }

                /**
                 * Sets the value of the beskrivelse property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setBeskrivelse(String value) {
                    this.beskrivelse = value;
                }

                /**
                 * Gets the value of the samendringstype property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getSamendringstype() {
                    return samendringstype;
                }

                /**
                 * Sets the value of the samendringstype property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setSamendringstype(String value) {
                    this.samendringstype = value;
                }

                /**
                 * Gets the value of the kjonnsrepresentasjon property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getKjonnsrepresentasjon() {
                    return kjonnsrepresentasjon;
                }

                /**
                 * Sets the value of the kjonnsrepresentasjon property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setKjonnsrepresentasjon(String value) {
                    this.kjonnsrepresentasjon = value;
                }


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
                 *         &lt;element name="person" maxOccurs="unbounded" minOccurs="0"&gt;
                 *           &lt;complexType&gt;
                 *             &lt;complexContent&gt;
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
                 *                 &lt;sequence&gt;
                 *                   &lt;element name="vergeTekst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
                 *                   &lt;choice&gt;
                 *                     &lt;element name="fodselsnr"&gt;
                 *                       &lt;simpleType&gt;
                 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                           &lt;length value="11"/&gt;
                 *                         &lt;/restriction&gt;
                 *                       &lt;/simpleType&gt;
                 *                     &lt;/element&gt;
                 *                     &lt;element name="fodselsdato"&gt;
                 *                       &lt;simpleType&gt;
                 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                           &lt;length value="10"/&gt;
                 *                         &lt;/restriction&gt;
                 *                       &lt;/simpleType&gt;
                 *                     &lt;/element&gt;
                 *                   &lt;/choice&gt;
                 *                   &lt;element name="fornavn"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="1"/&gt;
                 *                         &lt;maxLength value="50"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="mellomnavn" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="1"/&gt;
                 *                         &lt;maxLength value="50"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="slektsnavn"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="1"/&gt;
                 *                         &lt;maxLength value="50"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="adresse1" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="1"/&gt;
                 *                         &lt;maxLength value="35"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="adresse2" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="1"/&gt;
                 *                         &lt;maxLength value="35"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="adresse3" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="1"/&gt;
                 *                         &lt;maxLength value="35"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="postnr" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="4"/&gt;
                 *                         &lt;maxLength value="9"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="poststed" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="1"/&gt;
                 *                         &lt;maxLength value="35"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="land" minOccurs="0"&gt;
                 *                     &lt;complexType&gt;
                 *                       &lt;simpleContent&gt;
                 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
                 *                           &lt;attribute name="landkode4" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                 *                         &lt;/extension&gt;
                 *                       &lt;/simpleContent&gt;
                 *                     &lt;/complexType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="valgtAv" minOccurs="0"&gt;
                 *                     &lt;complexType&gt;
                 *                       &lt;simpleContent&gt;
                 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
                 *                           &lt;attribute name="kode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                 *                         &lt;/extension&gt;
                 *                       &lt;/simpleContent&gt;
                 *                     &lt;/complexType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="ansvarsandel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
                 *                   &lt;element name="fratraadt" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;length value="1"/&gt;
                 *                         &lt;enumeration value="K"/&gt;
                 *                         &lt;enumeration value="R"/&gt;
                 *                         &lt;enumeration value="F"/&gt;
                 *                         &lt;enumeration value="N"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="fratraadtTekst" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;length value="8"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="revisorKategori" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
                 *                   &lt;element name="revisorRkode" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
                 *                   &lt;element name="reTekst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
                 *                   &lt;element name="rolleFritekst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
                 *                 &lt;/sequence&gt;
                 *                 &lt;attribute name="beskrivelse" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                 *                 &lt;attribute name="statuskode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                 *               &lt;/restriction&gt;
                 *             &lt;/complexContent&gt;
                 *           &lt;/complexType&gt;
                 *         &lt;/element&gt;
                 *         &lt;element name="enhet" maxOccurs="unbounded" minOccurs="0"&gt;
                 *           &lt;complexType&gt;
                 *             &lt;complexContent&gt;
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
                 *                 &lt;sequence&gt;
                 *                   &lt;element name="orgnr"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;length value="9"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="navn1"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="1"/&gt;
                 *                         &lt;maxLength value="35"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="navn2" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="1"/&gt;
                 *                         &lt;maxLength value="35"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="navn3" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="1"/&gt;
                 *                         &lt;maxLength value="35"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="navn4" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="1"/&gt;
                 *                         &lt;maxLength value="35"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="navn5" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="1"/&gt;
                 *                         &lt;maxLength value="35"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="adresse1" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="1"/&gt;
                 *                         &lt;maxLength value="35"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="adresse2" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="1"/&gt;
                 *                         &lt;maxLength value="35"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="adresse3" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="1"/&gt;
                 *                         &lt;maxLength value="35"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="postnr" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="4"/&gt;
                 *                         &lt;maxLength value="9"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="poststed" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;minLength value="1"/&gt;
                 *                         &lt;maxLength value="35"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="land" minOccurs="0"&gt;
                 *                     &lt;complexType&gt;
                 *                       &lt;simpleContent&gt;
                 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
                 *                           &lt;attribute name="landkode4" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                 *                         &lt;/extension&gt;
                 *                       &lt;/simpleContent&gt;
                 *                     &lt;/complexType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="valgtAv" minOccurs="0"&gt;
                 *                     &lt;complexType&gt;
                 *                       &lt;simpleContent&gt;
                 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
                 *                           &lt;attribute name="kode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                 *                         &lt;/extension&gt;
                 *                       &lt;/simpleContent&gt;
                 *                     &lt;/complexType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="ansvarsandel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
                 *                   &lt;element name="fratraadt" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;length value="1"/&gt;
                 *                         &lt;enumeration value="K"/&gt;
                 *                         &lt;enumeration value="R"/&gt;
                 *                         &lt;enumeration value="F"/&gt;
                 *                         &lt;enumeration value="N"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="fratraadtTekst" minOccurs="0"&gt;
                 *                     &lt;simpleType&gt;
                 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                 *                         &lt;length value="8"/&gt;
                 *                       &lt;/restriction&gt;
                 *                     &lt;/simpleType&gt;
                 *                   &lt;/element&gt;
                 *                   &lt;element name="revisorKategori" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
                 *                   &lt;element name="revisorRkode" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
                 *                   &lt;element name="reTekst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
                 *                   &lt;element name="rolleFritekst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
                 *                 &lt;/sequence&gt;
                 *                 &lt;attribute name="beskrivelse" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                 *                 &lt;attribute name="statuskode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                 *               &lt;/restriction&gt;
                 *             &lt;/complexContent&gt;
                 *           &lt;/complexType&gt;
                 *         &lt;/element&gt;
                 *       &lt;/sequence&gt;
                 *       &lt;attribute name="beskrivelse" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                 *       &lt;attribute name="rolletype" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                 *     &lt;/restriction&gt;
                 *   &lt;/complexContent&gt;
                 * &lt;/complexType&gt;
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "person",
                    "enhet"
                })
                public static class Rolle {

                    protected List<Grunndata.Melding.Eierkommune.Samendring.Rolle.Person> person;
                    protected List<Grunndata.Melding.Eierkommune.Samendring.Rolle.Enhet> enhet;
                    @XmlAttribute(name = "beskrivelse", required = true)
                    protected String beskrivelse;
                    @XmlAttribute(name = "rolletype", required = true)
                    protected String rolletype;

                    /**
                     * Gets the value of the person property.
                     * 
                     * <p>
                     * This accessor method returns a reference to the live list,
                     * not a snapshot. Therefore any modification you make to the
                     * returned list will be present inside the Jakarta XML Binding object.
                     * This is why there is not a <CODE>set</CODE> method for the person property.
                     * 
                     * <p>
                     * For example, to add a new item, do as follows:
                     * <pre>
                     *    getPerson().add(newItem);
                     * </pre>
                     * 
                     * 
                     * <p>
                     * Objects of the following type(s) are allowed in the list
                     * {@link Grunndata.Melding.Eierkommune.Samendring.Rolle.Person }
                     * 
                     * 
                     */
                    public List<Grunndata.Melding.Eierkommune.Samendring.Rolle.Person> getPerson() {
                        if (person == null) {
                            person = new ArrayList<Grunndata.Melding.Eierkommune.Samendring.Rolle.Person>();
                        }
                        return this.person;
                    }

                    /**
                     * Gets the value of the enhet property.
                     * 
                     * <p>
                     * This accessor method returns a reference to the live list,
                     * not a snapshot. Therefore any modification you make to the
                     * returned list will be present inside the Jakarta XML Binding object.
                     * This is why there is not a <CODE>set</CODE> method for the enhet property.
                     * 
                     * <p>
                     * For example, to add a new item, do as follows:
                     * <pre>
                     *    getEnhet().add(newItem);
                     * </pre>
                     * 
                     * 
                     * <p>
                     * Objects of the following type(s) are allowed in the list
                     * {@link Grunndata.Melding.Eierkommune.Samendring.Rolle.Enhet }
                     * 
                     * 
                     */
                    public List<Grunndata.Melding.Eierkommune.Samendring.Rolle.Enhet> getEnhet() {
                        if (enhet == null) {
                            enhet = new ArrayList<Grunndata.Melding.Eierkommune.Samendring.Rolle.Enhet>();
                        }
                        return this.enhet;
                    }

                    /**
                     * Gets the value of the beskrivelse property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getBeskrivelse() {
                        return beskrivelse;
                    }

                    /**
                     * Sets the value of the beskrivelse property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setBeskrivelse(String value) {
                        this.beskrivelse = value;
                    }

                    /**
                     * Gets the value of the rolletype property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getRolletype() {
                        return rolletype;
                    }

                    /**
                     * Sets the value of the rolletype property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setRolletype(String value) {
                        this.rolletype = value;
                    }


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
                     *         &lt;element name="orgnr"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;length value="9"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="navn1"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;minLength value="1"/&gt;
                     *               &lt;maxLength value="35"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="navn2" minOccurs="0"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;minLength value="1"/&gt;
                     *               &lt;maxLength value="35"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="navn3" minOccurs="0"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;minLength value="1"/&gt;
                     *               &lt;maxLength value="35"/&gt;
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
                     *         &lt;element name="adresse1" minOccurs="0"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;minLength value="1"/&gt;
                     *               &lt;maxLength value="35"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="adresse2" minOccurs="0"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;minLength value="1"/&gt;
                     *               &lt;maxLength value="35"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="adresse3" minOccurs="0"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;minLength value="1"/&gt;
                     *               &lt;maxLength value="35"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="postnr" minOccurs="0"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;minLength value="4"/&gt;
                     *               &lt;maxLength value="9"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="poststed" minOccurs="0"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;minLength value="1"/&gt;
                     *               &lt;maxLength value="35"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="land" minOccurs="0"&gt;
                     *           &lt;complexType&gt;
                     *             &lt;simpleContent&gt;
                     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
                     *                 &lt;attribute name="landkode4" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                     *               &lt;/extension&gt;
                     *             &lt;/simpleContent&gt;
                     *           &lt;/complexType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="valgtAv" minOccurs="0"&gt;
                     *           &lt;complexType&gt;
                     *             &lt;simpleContent&gt;
                     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
                     *                 &lt;attribute name="kode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                     *               &lt;/extension&gt;
                     *             &lt;/simpleContent&gt;
                     *           &lt;/complexType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="ansvarsandel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
                     *         &lt;element name="fratraadt" minOccurs="0"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;length value="1"/&gt;
                     *               &lt;enumeration value="K"/&gt;
                     *               &lt;enumeration value="R"/&gt;
                     *               &lt;enumeration value="F"/&gt;
                     *               &lt;enumeration value="N"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="fratraadtTekst" minOccurs="0"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;length value="8"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="revisorKategori" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
                     *         &lt;element name="revisorRkode" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
                     *         &lt;element name="reTekst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
                     *         &lt;element name="rolleFritekst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
                     *       &lt;/sequence&gt;
                     *       &lt;attribute name="beskrivelse" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                     *       &lt;attribute name="statuskode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                     *     &lt;/restriction&gt;
                     *   &lt;/complexContent&gt;
                     * &lt;/complexType&gt;
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "orgnr",
                        "navn1",
                        "navn2",
                        "navn3",
                        "navn4",
                        "navn5",
                        "adresse1",
                        "adresse2",
                        "adresse3",
                        "postnr",
                        "poststed",
                        "land",
                        "valgtAv",
                        "ansvarsandel",
                        "fratraadt",
                        "fratraadtTekst",
                        "revisorKategori",
                        "revisorRkode",
                        "reTekst",
                        "rolleFritekst"
                    })
                    public static class Enhet {

                        @XmlElement(required = true)
                        protected String orgnr;
                        @XmlElement(required = true)
                        protected String navn1;
                        protected String navn2;
                        protected String navn3;
                        protected String navn4;
                        protected String navn5;
                        protected String adresse1;
                        protected String adresse2;
                        protected String adresse3;
                        protected String postnr;
                        protected String poststed;
                        protected Grunndata.Melding.Eierkommune.Samendring.Rolle.Enhet.Land land;
                        protected Grunndata.Melding.Eierkommune.Samendring.Rolle.Enhet.ValgtAv valgtAv;
                        protected String ansvarsandel;
                        protected String fratraadt;
                        protected String fratraadtTekst;
                        protected Integer revisorKategori;
                        protected Integer revisorRkode;
                        protected String reTekst;
                        protected String rolleFritekst;
                        @XmlAttribute(name = "beskrivelse", required = true)
                        protected String beskrivelse;
                        @XmlAttribute(name = "statuskode", required = true)
                        protected String statuskode;

                        /**
                         * Gets the value of the orgnr property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getOrgnr() {
                            return orgnr;
                        }

                        /**
                         * Sets the value of the orgnr property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setOrgnr(String value) {
                            this.orgnr = value;
                        }

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
                         * Gets the value of the adresse1 property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getAdresse1() {
                            return adresse1;
                        }

                        /**
                         * Sets the value of the adresse1 property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setAdresse1(String value) {
                            this.adresse1 = value;
                        }

                        /**
                         * Gets the value of the adresse2 property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getAdresse2() {
                            return adresse2;
                        }

                        /**
                         * Sets the value of the adresse2 property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setAdresse2(String value) {
                            this.adresse2 = value;
                        }

                        /**
                         * Gets the value of the adresse3 property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getAdresse3() {
                            return adresse3;
                        }

                        /**
                         * Sets the value of the adresse3 property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setAdresse3(String value) {
                            this.adresse3 = value;
                        }

                        /**
                         * Gets the value of the postnr property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getPostnr() {
                            return postnr;
                        }

                        /**
                         * Sets the value of the postnr property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setPostnr(String value) {
                            this.postnr = value;
                        }

                        /**
                         * Gets the value of the poststed property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getPoststed() {
                            return poststed;
                        }

                        /**
                         * Sets the value of the poststed property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setPoststed(String value) {
                            this.poststed = value;
                        }

                        /**
                         * Gets the value of the land property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link Grunndata.Melding.Eierkommune.Samendring.Rolle.Enhet.Land }
                         *     
                         */
                        public Grunndata.Melding.Eierkommune.Samendring.Rolle.Enhet.Land getLand() {
                            return land;
                        }

                        /**
                         * Sets the value of the land property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link Grunndata.Melding.Eierkommune.Samendring.Rolle.Enhet.Land }
                         *     
                         */
                        public void setLand(Grunndata.Melding.Eierkommune.Samendring.Rolle.Enhet.Land value) {
                            this.land = value;
                        }

                        /**
                         * Gets the value of the valgtAv property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link Grunndata.Melding.Eierkommune.Samendring.Rolle.Enhet.ValgtAv }
                         *     
                         */
                        public Grunndata.Melding.Eierkommune.Samendring.Rolle.Enhet.ValgtAv getValgtAv() {
                            return valgtAv;
                        }

                        /**
                         * Sets the value of the valgtAv property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link Grunndata.Melding.Eierkommune.Samendring.Rolle.Enhet.ValgtAv }
                         *     
                         */
                        public void setValgtAv(Grunndata.Melding.Eierkommune.Samendring.Rolle.Enhet.ValgtAv value) {
                            this.valgtAv = value;
                        }

                        /**
                         * Gets the value of the ansvarsandel property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getAnsvarsandel() {
                            return ansvarsandel;
                        }

                        /**
                         * Sets the value of the ansvarsandel property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setAnsvarsandel(String value) {
                            this.ansvarsandel = value;
                        }

                        /**
                         * Gets the value of the fratraadt property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getFratraadt() {
                            return fratraadt;
                        }

                        /**
                         * Sets the value of the fratraadt property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setFratraadt(String value) {
                            this.fratraadt = value;
                        }

                        /**
                         * Gets the value of the fratraadtTekst property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getFratraadtTekst() {
                            return fratraadtTekst;
                        }

                        /**
                         * Sets the value of the fratraadtTekst property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setFratraadtTekst(String value) {
                            this.fratraadtTekst = value;
                        }

                        /**
                         * Gets the value of the revisorKategori property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link Integer }
                         *     
                         */
                        public Integer getRevisorKategori() {
                            return revisorKategori;
                        }

                        /**
                         * Sets the value of the revisorKategori property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link Integer }
                         *     
                         */
                        public void setRevisorKategori(Integer value) {
                            this.revisorKategori = value;
                        }

                        /**
                         * Gets the value of the revisorRkode property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link Integer }
                         *     
                         */
                        public Integer getRevisorRkode() {
                            return revisorRkode;
                        }

                        /**
                         * Sets the value of the revisorRkode property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link Integer }
                         *     
                         */
                        public void setRevisorRkode(Integer value) {
                            this.revisorRkode = value;
                        }

                        /**
                         * Gets the value of the reTekst property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getReTekst() {
                            return reTekst;
                        }

                        /**
                         * Sets the value of the reTekst property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setReTekst(String value) {
                            this.reTekst = value;
                        }

                        /**
                         * Gets the value of the rolleFritekst property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getRolleFritekst() {
                            return rolleFritekst;
                        }

                        /**
                         * Sets the value of the rolleFritekst property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setRolleFritekst(String value) {
                            this.rolleFritekst = value;
                        }

                        /**
                         * Gets the value of the beskrivelse property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getBeskrivelse() {
                            return beskrivelse;
                        }

                        /**
                         * Sets the value of the beskrivelse property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setBeskrivelse(String value) {
                            this.beskrivelse = value;
                        }

                        /**
                         * Gets the value of the statuskode property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getStatuskode() {
                            return statuskode;
                        }

                        /**
                         * Sets the value of the statuskode property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setStatuskode(String value) {
                            this.statuskode = value;
                        }


                        /**
                         * <p>Java class for anonymous complex type.
                         * 
                         * <p>The following schema fragment specifies the expected content contained within this class.
                         * 
                         * <pre>
                         * &lt;complexType&gt;
                         *   &lt;simpleContent&gt;
                         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
                         *       &lt;attribute name="landkode4" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                         *     &lt;/extension&gt;
                         *   &lt;/simpleContent&gt;
                         * &lt;/complexType&gt;
                         * </pre>
                         * 
                         * 
                         */
                        @XmlAccessorType(XmlAccessType.FIELD)
                        @XmlType(name = "", propOrder = {
                            "value"
                        })
                        public static class Land {

                            @XmlValue
                            protected String value;
                            @XmlAttribute(name = "landkode4", required = true)
                            protected String landkode4;

                            /**
                             * Gets the value of the value property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getValue() {
                                return value;
                            }

                            /**
                             * Sets the value of the value property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setValue(String value) {
                                this.value = value;
                            }

                            /**
                             * Gets the value of the landkode4 property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getLandkode4() {
                                return landkode4;
                            }

                            /**
                             * Sets the value of the landkode4 property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setLandkode4(String value) {
                                this.landkode4 = value;
                            }

                        }


                        /**
                         * <p>Java class for anonymous complex type.
                         * 
                         * <p>The following schema fragment specifies the expected content contained within this class.
                         * 
                         * <pre>
                         * &lt;complexType&gt;
                         *   &lt;simpleContent&gt;
                         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
                         *       &lt;attribute name="kode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                         *     &lt;/extension&gt;
                         *   &lt;/simpleContent&gt;
                         * &lt;/complexType&gt;
                         * </pre>
                         * 
                         * 
                         */
                        @XmlAccessorType(XmlAccessType.FIELD)
                        @XmlType(name = "", propOrder = {
                            "value"
                        })
                        public static class ValgtAv {

                            @XmlValue
                            protected String value;
                            @XmlAttribute(name = "kode", required = true)
                            protected String kode;

                            /**
                             * Gets the value of the value property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getValue() {
                                return value;
                            }

                            /**
                             * Sets the value of the value property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setValue(String value) {
                                this.value = value;
                            }

                            /**
                             * Gets the value of the kode property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getKode() {
                                return kode;
                            }

                            /**
                             * Sets the value of the kode property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setKode(String value) {
                                this.kode = value;
                            }

                        }

                    }


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
                     *         &lt;element name="vergeTekst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
                     *         &lt;choice&gt;
                     *           &lt;element name="fodselsnr"&gt;
                     *             &lt;simpleType&gt;
                     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *                 &lt;length value="11"/&gt;
                     *               &lt;/restriction&gt;
                     *             &lt;/simpleType&gt;
                     *           &lt;/element&gt;
                     *           &lt;element name="fodselsdato"&gt;
                     *             &lt;simpleType&gt;
                     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *                 &lt;length value="10"/&gt;
                     *               &lt;/restriction&gt;
                     *             &lt;/simpleType&gt;
                     *           &lt;/element&gt;
                     *         &lt;/choice&gt;
                     *         &lt;element name="fornavn"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;minLength value="1"/&gt;
                     *               &lt;maxLength value="50"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="mellomnavn" minOccurs="0"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;minLength value="1"/&gt;
                     *               &lt;maxLength value="50"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="slektsnavn"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;minLength value="1"/&gt;
                     *               &lt;maxLength value="50"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="adresse1" minOccurs="0"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;minLength value="1"/&gt;
                     *               &lt;maxLength value="35"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="adresse2" minOccurs="0"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;minLength value="1"/&gt;
                     *               &lt;maxLength value="35"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="adresse3" minOccurs="0"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;minLength value="1"/&gt;
                     *               &lt;maxLength value="35"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="postnr" minOccurs="0"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;minLength value="4"/&gt;
                     *               &lt;maxLength value="9"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="poststed" minOccurs="0"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;minLength value="1"/&gt;
                     *               &lt;maxLength value="35"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="land" minOccurs="0"&gt;
                     *           &lt;complexType&gt;
                     *             &lt;simpleContent&gt;
                     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
                     *                 &lt;attribute name="landkode4" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                     *               &lt;/extension&gt;
                     *             &lt;/simpleContent&gt;
                     *           &lt;/complexType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="valgtAv" minOccurs="0"&gt;
                     *           &lt;complexType&gt;
                     *             &lt;simpleContent&gt;
                     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
                     *                 &lt;attribute name="kode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                     *               &lt;/extension&gt;
                     *             &lt;/simpleContent&gt;
                     *           &lt;/complexType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="ansvarsandel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
                     *         &lt;element name="fratraadt" minOccurs="0"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;length value="1"/&gt;
                     *               &lt;enumeration value="K"/&gt;
                     *               &lt;enumeration value="R"/&gt;
                     *               &lt;enumeration value="F"/&gt;
                     *               &lt;enumeration value="N"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="fratraadtTekst" minOccurs="0"&gt;
                     *           &lt;simpleType&gt;
                     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
                     *               &lt;length value="8"/&gt;
                     *             &lt;/restriction&gt;
                     *           &lt;/simpleType&gt;
                     *         &lt;/element&gt;
                     *         &lt;element name="revisorKategori" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
                     *         &lt;element name="revisorRkode" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
                     *         &lt;element name="reTekst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
                     *         &lt;element name="rolleFritekst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
                     *       &lt;/sequence&gt;
                     *       &lt;attribute name="beskrivelse" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                     *       &lt;attribute name="statuskode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                     *     &lt;/restriction&gt;
                     *   &lt;/complexContent&gt;
                     * &lt;/complexType&gt;
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "vergeTekst",
                        "fodselsnr",
                        "fodselsdato",
                        "fornavn",
                        "mellomnavn",
                        "slektsnavn",
                        "adresse1",
                        "adresse2",
                        "adresse3",
                        "postnr",
                        "poststed",
                        "land",
                        "valgtAv",
                        "ansvarsandel",
                        "fratraadt",
                        "fratraadtTekst",
                        "revisorKategori",
                        "revisorRkode",
                        "reTekst",
                        "rolleFritekst"
                    })
                    public static class Person {

                        protected String vergeTekst;
                        protected String fodselsnr;
                        protected String fodselsdato;
                        @XmlElement(required = true)
                        protected String fornavn;
                        protected String mellomnavn;
                        @XmlElement(required = true)
                        protected String slektsnavn;
                        protected String adresse1;
                        protected String adresse2;
                        protected String adresse3;
                        protected String postnr;
                        protected String poststed;
                        protected Grunndata.Melding.Eierkommune.Samendring.Rolle.Person.Land land;
                        protected Grunndata.Melding.Eierkommune.Samendring.Rolle.Person.ValgtAv valgtAv;
                        protected String ansvarsandel;
                        protected String fratraadt;
                        protected String fratraadtTekst;
                        protected Integer revisorKategori;
                        protected Integer revisorRkode;
                        protected String reTekst;
                        protected String rolleFritekst;
                        @XmlAttribute(name = "beskrivelse", required = true)
                        protected String beskrivelse;
                        @XmlAttribute(name = "statuskode", required = true)
                        protected String statuskode;

                        /**
                         * Gets the value of the vergeTekst property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getVergeTekst() {
                            return vergeTekst;
                        }

                        /**
                         * Sets the value of the vergeTekst property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setVergeTekst(String value) {
                            this.vergeTekst = value;
                        }

                        /**
                         * Gets the value of the fodselsnr property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getFodselsnr() {
                            return fodselsnr;
                        }

                        /**
                         * Sets the value of the fodselsnr property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setFodselsnr(String value) {
                            this.fodselsnr = value;
                        }

                        /**
                         * Gets the value of the fodselsdato property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getFodselsdato() {
                            return fodselsdato;
                        }

                        /**
                         * Sets the value of the fodselsdato property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setFodselsdato(String value) {
                            this.fodselsdato = value;
                        }

                        /**
                         * Gets the value of the fornavn property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getFornavn() {
                            return fornavn;
                        }

                        /**
                         * Sets the value of the fornavn property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setFornavn(String value) {
                            this.fornavn = value;
                        }

                        /**
                         * Gets the value of the mellomnavn property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getMellomnavn() {
                            return mellomnavn;
                        }

                        /**
                         * Sets the value of the mellomnavn property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setMellomnavn(String value) {
                            this.mellomnavn = value;
                        }

                        /**
                         * Gets the value of the slektsnavn property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getSlektsnavn() {
                            return slektsnavn;
                        }

                        /**
                         * Sets the value of the slektsnavn property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setSlektsnavn(String value) {
                            this.slektsnavn = value;
                        }

                        /**
                         * Gets the value of the adresse1 property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getAdresse1() {
                            return adresse1;
                        }

                        /**
                         * Sets the value of the adresse1 property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setAdresse1(String value) {
                            this.adresse1 = value;
                        }

                        /**
                         * Gets the value of the adresse2 property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getAdresse2() {
                            return adresse2;
                        }

                        /**
                         * Sets the value of the adresse2 property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setAdresse2(String value) {
                            this.adresse2 = value;
                        }

                        /**
                         * Gets the value of the adresse3 property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getAdresse3() {
                            return adresse3;
                        }

                        /**
                         * Sets the value of the adresse3 property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setAdresse3(String value) {
                            this.adresse3 = value;
                        }

                        /**
                         * Gets the value of the postnr property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getPostnr() {
                            return postnr;
                        }

                        /**
                         * Sets the value of the postnr property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setPostnr(String value) {
                            this.postnr = value;
                        }

                        /**
                         * Gets the value of the poststed property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getPoststed() {
                            return poststed;
                        }

                        /**
                         * Sets the value of the poststed property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setPoststed(String value) {
                            this.poststed = value;
                        }

                        /**
                         * Gets the value of the land property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link Grunndata.Melding.Eierkommune.Samendring.Rolle.Person.Land }
                         *     
                         */
                        public Grunndata.Melding.Eierkommune.Samendring.Rolle.Person.Land getLand() {
                            return land;
                        }

                        /**
                         * Sets the value of the land property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link Grunndata.Melding.Eierkommune.Samendring.Rolle.Person.Land }
                         *     
                         */
                        public void setLand(Grunndata.Melding.Eierkommune.Samendring.Rolle.Person.Land value) {
                            this.land = value;
                        }

                        /**
                         * Gets the value of the valgtAv property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link Grunndata.Melding.Eierkommune.Samendring.Rolle.Person.ValgtAv }
                         *     
                         */
                        public Grunndata.Melding.Eierkommune.Samendring.Rolle.Person.ValgtAv getValgtAv() {
                            return valgtAv;
                        }

                        /**
                         * Sets the value of the valgtAv property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link Grunndata.Melding.Eierkommune.Samendring.Rolle.Person.ValgtAv }
                         *     
                         */
                        public void setValgtAv(Grunndata.Melding.Eierkommune.Samendring.Rolle.Person.ValgtAv value) {
                            this.valgtAv = value;
                        }

                        /**
                         * Gets the value of the ansvarsandel property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getAnsvarsandel() {
                            return ansvarsandel;
                        }

                        /**
                         * Sets the value of the ansvarsandel property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setAnsvarsandel(String value) {
                            this.ansvarsandel = value;
                        }

                        /**
                         * Gets the value of the fratraadt property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getFratraadt() {
                            return fratraadt;
                        }

                        /**
                         * Sets the value of the fratraadt property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setFratraadt(String value) {
                            this.fratraadt = value;
                        }

                        /**
                         * Gets the value of the fratraadtTekst property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getFratraadtTekst() {
                            return fratraadtTekst;
                        }

                        /**
                         * Sets the value of the fratraadtTekst property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setFratraadtTekst(String value) {
                            this.fratraadtTekst = value;
                        }

                        /**
                         * Gets the value of the revisorKategori property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link Integer }
                         *     
                         */
                        public Integer getRevisorKategori() {
                            return revisorKategori;
                        }

                        /**
                         * Sets the value of the revisorKategori property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link Integer }
                         *     
                         */
                        public void setRevisorKategori(Integer value) {
                            this.revisorKategori = value;
                        }

                        /**
                         * Gets the value of the revisorRkode property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link Integer }
                         *     
                         */
                        public Integer getRevisorRkode() {
                            return revisorRkode;
                        }

                        /**
                         * Sets the value of the revisorRkode property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link Integer }
                         *     
                         */
                        public void setRevisorRkode(Integer value) {
                            this.revisorRkode = value;
                        }

                        /**
                         * Gets the value of the reTekst property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getReTekst() {
                            return reTekst;
                        }

                        /**
                         * Sets the value of the reTekst property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setReTekst(String value) {
                            this.reTekst = value;
                        }

                        /**
                         * Gets the value of the rolleFritekst property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getRolleFritekst() {
                            return rolleFritekst;
                        }

                        /**
                         * Sets the value of the rolleFritekst property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setRolleFritekst(String value) {
                            this.rolleFritekst = value;
                        }

                        /**
                         * Gets the value of the beskrivelse property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getBeskrivelse() {
                            return beskrivelse;
                        }

                        /**
                         * Sets the value of the beskrivelse property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setBeskrivelse(String value) {
                            this.beskrivelse = value;
                        }

                        /**
                         * Gets the value of the statuskode property.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getStatuskode() {
                            return statuskode;
                        }

                        /**
                         * Sets the value of the statuskode property.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setStatuskode(String value) {
                            this.statuskode = value;
                        }


                        /**
                         * <p>Java class for anonymous complex type.
                         * 
                         * <p>The following schema fragment specifies the expected content contained within this class.
                         * 
                         * <pre>
                         * &lt;complexType&gt;
                         *   &lt;simpleContent&gt;
                         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
                         *       &lt;attribute name="landkode4" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                         *     &lt;/extension&gt;
                         *   &lt;/simpleContent&gt;
                         * &lt;/complexType&gt;
                         * </pre>
                         * 
                         * 
                         */
                        @XmlAccessorType(XmlAccessType.FIELD)
                        @XmlType(name = "", propOrder = {
                            "value"
                        })
                        public static class Land {

                            @XmlValue
                            protected String value;
                            @XmlAttribute(name = "landkode4", required = true)
                            protected String landkode4;

                            /**
                             * Gets the value of the value property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getValue() {
                                return value;
                            }

                            /**
                             * Sets the value of the value property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setValue(String value) {
                                this.value = value;
                            }

                            /**
                             * Gets the value of the landkode4 property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getLandkode4() {
                                return landkode4;
                            }

                            /**
                             * Sets the value of the landkode4 property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setLandkode4(String value) {
                                this.landkode4 = value;
                            }

                        }


                        /**
                         * <p>Java class for anonymous complex type.
                         * 
                         * <p>The following schema fragment specifies the expected content contained within this class.
                         * 
                         * <pre>
                         * &lt;complexType&gt;
                         *   &lt;simpleContent&gt;
                         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
                         *       &lt;attribute name="kode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                         *     &lt;/extension&gt;
                         *   &lt;/simpleContent&gt;
                         * &lt;/complexType&gt;
                         * </pre>
                         * 
                         * 
                         */
                        @XmlAccessorType(XmlAccessType.FIELD)
                        @XmlType(name = "", propOrder = {
                            "value"
                        })
                        public static class ValgtAv {

                            @XmlValue
                            protected String value;
                            @XmlAttribute(name = "kode", required = true)
                            protected String kode;

                            /**
                             * Gets the value of the value property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getValue() {
                                return value;
                            }

                            /**
                             * Sets the value of the value property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setValue(String value) {
                                this.value = value;
                            }

                            /**
                             * Gets the value of the kode property.
                             * 
                             * @return
                             *     possible object is
                             *     {@link String }
                             *     
                             */
                            public String getKode() {
                                return kode;
                            }

                            /**
                             * Sets the value of the kode property.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link String }
                             *     
                             */
                            public void setKode(String value) {
                                this.kode = value;
                            }

                        }

                    }

                }

            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;group ref="{}samendring"/&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "samendring"
        })
        public static class Komplementar {

            @XmlElement(required = true)
            protected List<Grunndata.Melding.Eierkommune.Samendring> samendring;

            /**
             * Gets the value of the samendring property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the Jakarta XML Binding object.
             * This is why there is not a <CODE>set</CODE> method for the samendring property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getSamendring().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Grunndata.Melding.Eierkommune.Samendring }
             * 
             * 
             */
            public List<Grunndata.Melding.Eierkommune.Samendring> getSamendring() {
                if (samendring == null) {
                    samendring = new ArrayList<Grunndata.Melding.Eierkommune.Samendring>();
                }
                return this.samendring;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;group ref="{}samendring"/&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "samendring"
        })
        public static class Kontaktperson {

            @XmlElement(required = true)
            protected List<Grunndata.Melding.Eierkommune.Samendring> samendring;

            /**
             * Gets the value of the samendring property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the Jakarta XML Binding object.
             * This is why there is not a <CODE>set</CODE> method for the samendring property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getSamendring().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Grunndata.Melding.Eierkommune.Samendring }
             * 
             * 
             */
            public List<Grunndata.Melding.Eierkommune.Samendring> getSamendring() {
                if (samendring == null) {
                    samendring = new ArrayList<Grunndata.Melding.Eierkommune.Samendring>();
                }
                return this.samendring;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;simpleContent&gt;
         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
         *       &lt;attribute name="registreringsDato" use="required" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
         *     &lt;/extension&gt;
         *   &lt;/simpleContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "value"
        })
        public static class Organisasjonsnummer {

            @XmlValue
            protected String value;
            @XmlAttribute(name = "registreringsDato", required = true)
            @XmlSchemaType(name = "date")
            protected XMLGregorianCalendar registreringsDato;

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValue(String value) {
                this.value = value;
            }

            /**
             * Gets the value of the registreringsDato property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getRegistreringsDato() {
                return registreringsDato;
            }

            /**
             * Sets the value of the registreringsDato property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setRegistreringsDato(XMLGregorianCalendar value) {
                this.registreringsDato = value;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;group ref="{}samendring"/&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "samendring"
        })
        public static class Regnskapsfoerer {

            @XmlElement(required = true)
            protected List<Grunndata.Melding.Eierkommune.Samendring> samendring;

            /**
             * Gets the value of the samendring property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the Jakarta XML Binding object.
             * This is why there is not a <CODE>set</CODE> method for the samendring property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getSamendring().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Grunndata.Melding.Eierkommune.Samendring }
             * 
             * 
             */
            public List<Grunndata.Melding.Eierkommune.Samendring> getSamendring() {
                if (samendring == null) {
                    samendring = new ArrayList<Grunndata.Melding.Eierkommune.Samendring>();
                }
                return this.samendring;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;group ref="{}samendring"/&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "samendring"
        })
        public static class Revisor {

            @XmlElement(required = true)
            protected List<Grunndata.Melding.Eierkommune.Samendring> samendring;

            /**
             * Gets the value of the samendring property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the Jakarta XML Binding object.
             * This is why there is not a <CODE>set</CODE> method for the samendring property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getSamendring().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Grunndata.Melding.Eierkommune.Samendring }
             * 
             * 
             */
            public List<Grunndata.Melding.Eierkommune.Samendring> getSamendring() {
                if (samendring == null) {
                    samendring = new ArrayList<Grunndata.Melding.Eierkommune.Samendring>();
                }
                return this.samendring;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;group ref="{}samendring"/&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "samendring"
        })
        public static class Sameiere {

            @XmlElement(required = true)
            protected List<Grunndata.Melding.Eierkommune.Samendring> samendring;

            /**
             * Gets the value of the samendring property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the Jakarta XML Binding object.
             * This is why there is not a <CODE>set</CODE> method for the samendring property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getSamendring().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Grunndata.Melding.Eierkommune.Samendring }
             * 
             * 
             */
            public List<Grunndata.Melding.Eierkommune.Samendring> getSamendring() {
                if (samendring == null) {
                    samendring = new ArrayList<Grunndata.Melding.Eierkommune.Samendring>();
                }
                return this.samendring;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;group ref="{}samendring"/&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "samendring"
        })
        public static class Styre {

            @XmlElement(required = true)
            protected List<Grunndata.Melding.Eierkommune.Samendring> samendring;

            /**
             * Gets the value of the samendring property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the Jakarta XML Binding object.
             * This is why there is not a <CODE>set</CODE> method for the samendring property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getSamendring().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Grunndata.Melding.Eierkommune.Samendring }
             * 
             * 
             */
            public List<Grunndata.Melding.Eierkommune.Samendring> getSamendring() {
                if (samendring == null) {
                    samendring = new ArrayList<Grunndata.Melding.Eierkommune.Samendring>();
                }
                return this.samendring;
            }

        }

    }


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
     *         &lt;element name="orgnr" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
     *         &lt;element name="hovedStatus" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
     *         &lt;element name="underStatus"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="underStatusMelding" maxOccurs="unbounded"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;simpleContent&gt;
     *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
     *                           &lt;attribute name="kode" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
     *                         &lt;/extension&gt;
     *                       &lt;/simpleContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *       &lt;attribute name="prossessDato" use="required" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
     *       &lt;attribute name="tjeneste" use="required"&gt;
     *         &lt;simpleType&gt;
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *             &lt;minLength value="1"/&gt;
     *             &lt;maxLength value="40"/&gt;
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
    @XmlType(name = "", propOrder = {
        "orgnr",
        "hovedStatus",
        "underStatus"
    })
    public static class ResponseHeader {

        protected int orgnr;
        protected int hovedStatus;
        @XmlElement(required = true)
        protected Grunndata.ResponseHeader.UnderStatus underStatus;
        @XmlAttribute(name = "prossessDato", required = true)
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar prossessDato;
        @XmlAttribute(name = "tjeneste", required = true)
        protected String tjeneste;

        /**
         * Gets the value of the orgnr property.
         * 
         */
        public int getOrgnr() {
            return orgnr;
        }

        /**
         * Sets the value of the orgnr property.
         * 
         */
        public void setOrgnr(int value) {
            this.orgnr = value;
        }

        /**
         * Gets the value of the hovedStatus property.
         * 
         */
        public int getHovedStatus() {
            return hovedStatus;
        }

        /**
         * Sets the value of the hovedStatus property.
         * 
         */
        public void setHovedStatus(int value) {
            this.hovedStatus = value;
        }

        /**
         * Gets the value of the underStatus property.
         * 
         * @return
         *     possible object is
         *     {@link Grunndata.ResponseHeader.UnderStatus }
         *     
         */
        public Grunndata.ResponseHeader.UnderStatus getUnderStatus() {
            return underStatus;
        }

        /**
         * Sets the value of the underStatus property.
         * 
         * @param value
         *     allowed object is
         *     {@link Grunndata.ResponseHeader.UnderStatus }
         *     
         */
        public void setUnderStatus(Grunndata.ResponseHeader.UnderStatus value) {
            this.underStatus = value;
        }

        /**
         * Gets the value of the prossessDato property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getProssessDato() {
            return prossessDato;
        }

        /**
         * Sets the value of the prossessDato property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setProssessDato(XMLGregorianCalendar value) {
            this.prossessDato = value;
        }

        /**
         * Gets the value of the tjeneste property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTjeneste() {
            return tjeneste;
        }

        /**
         * Sets the value of the tjeneste property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTjeneste(String value) {
            this.tjeneste = value;
        }


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
         *         &lt;element name="underStatusMelding" maxOccurs="unbounded"&gt;
         *           &lt;complexType&gt;
         *             &lt;simpleContent&gt;
         *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
         *                 &lt;attribute name="kode" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
         *               &lt;/extension&gt;
         *             &lt;/simpleContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
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
            "underStatusMelding"
        })
        public static class UnderStatus {

            @XmlElement(required = true)
            protected List<Grunndata.ResponseHeader.UnderStatus.UnderStatusMelding> underStatusMelding;

            /**
             * Gets the value of the underStatusMelding property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the Jakarta XML Binding object.
             * This is why there is not a <CODE>set</CODE> method for the underStatusMelding property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getUnderStatusMelding().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Grunndata.ResponseHeader.UnderStatus.UnderStatusMelding }
             * 
             * 
             */
            public List<Grunndata.ResponseHeader.UnderStatus.UnderStatusMelding> getUnderStatusMelding() {
                if (underStatusMelding == null) {
                    underStatusMelding = new ArrayList<Grunndata.ResponseHeader.UnderStatus.UnderStatusMelding>();
                }
                return this.underStatusMelding;
            }


            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType&gt;
             *   &lt;simpleContent&gt;
             *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
             *       &lt;attribute name="kode" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
             *     &lt;/extension&gt;
             *   &lt;/simpleContent&gt;
             * &lt;/complexType&gt;
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "value"
            })
            public static class UnderStatusMelding {

                @XmlValue
                protected String value;
                @XmlAttribute(name = "kode", required = true)
                protected int kode;

                /**
                 * Gets the value of the value property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getValue() {
                    return value;
                }

                /**
                 * Sets the value of the value property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setValue(String value) {
                    this.value = value;
                }

                /**
                 * Gets the value of the kode property.
                 * 
                 */
                public int getKode() {
                    return kode;
                }

                /**
                 * Sets the value of the kode property.
                 * 
                 */
                public void setKode(int value) {
                    this.kode = value;
                }

            }

        }

    }

}
