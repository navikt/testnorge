
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
 *                   &lt;choice&gt;
 *                     &lt;element name="orgnr" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *                     &lt;element name="fodselsnr"&gt;
 *                       &lt;simpleType&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                           &lt;length value="11"/&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/simpleType&gt;
 *                     &lt;/element&gt;
 *                     &lt;element name="requestID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *                   &lt;/choice&gt;
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
 *                 &lt;attribute name="tjeneste" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="melding" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="rolleInnehaver"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;choice&gt;
 *                               &lt;element name="orgnr"&gt;
 *                                 &lt;complexType&gt;
 *                                   &lt;simpleContent&gt;
 *                                     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;int"&gt;
 *                                       &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                                     &lt;/extension&gt;
 *                                   &lt;/simpleContent&gt;
 *                                 &lt;/complexType&gt;
 *                               &lt;/element&gt;
 *                               &lt;element name="fodselsnr"&gt;
 *                                 &lt;complexType&gt;
 *                                   &lt;simpleContent&gt;
 *                                     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *                                       &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                                     &lt;/extension&gt;
 *                                   &lt;/simpleContent&gt;
 *                                 &lt;/complexType&gt;
 *                               &lt;/element&gt;
 *                               &lt;element name="fodselsdato"&gt;
 *                                 &lt;complexType&gt;
 *                                   &lt;simpleContent&gt;
 *                                     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;date"&gt;
 *                                       &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                                     &lt;/extension&gt;
 *                                   &lt;/simpleContent&gt;
 *                                 &lt;/complexType&gt;
 *                               &lt;/element&gt;
 *                             &lt;/choice&gt;
 *                             &lt;element name="navn" type="{}navnType"/&gt;
 *                             &lt;element name="adresse" type="{}adresseType1"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                   &lt;element name="roller" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="enhet" maxOccurs="unbounded"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="rolleBeskrivelse"&gt;
 *                                         &lt;complexType&gt;
 *                                           &lt;simpleContent&gt;
 *                                             &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *                                               &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                                             &lt;/extension&gt;
 *                                           &lt;/simpleContent&gt;
 *                                         &lt;/complexType&gt;
 *                                       &lt;/element&gt;
 *                                       &lt;element name="orgnr"&gt;
 *                                         &lt;complexType&gt;
 *                                           &lt;simpleContent&gt;
 *                                             &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;int"&gt;
 *                                               &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                                             &lt;/extension&gt;
 *                                           &lt;/simpleContent&gt;
 *                                         &lt;/complexType&gt;
 *                                       &lt;/element&gt;
 *                                       &lt;element name="navn" type="{}navnType"/&gt;
 *                                       &lt;element name="adresse"&gt;
 *                                         &lt;complexType&gt;
 *                                           &lt;complexContent&gt;
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                               &lt;sequence&gt;
 *                                                 &lt;element name="forretningsAdresse" type="{}adresseType2" minOccurs="0"/&gt;
 *                                                 &lt;element name="postAdresse" type="{}adresseType2" minOccurs="0"/&gt;
 *                                               &lt;/sequence&gt;
 *                                             &lt;/restriction&gt;
 *                                           &lt;/complexContent&gt;
 *                                         &lt;/complexType&gt;
 *                                       &lt;/element&gt;
 *                                     &lt;/sequence&gt;
 *                                     &lt;attribute name="nr" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *                                     &lt;attribute name="registreringsDato" use="required" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                           &lt;/sequence&gt;
 *                           &lt;attribute name="ledetekst" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
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
@XmlRootElement(name = "grunndatautskrift")
public class GrunndataUtskrift {

    @XmlElement(required = true)
    protected GrunndataUtskrift.ResponseHeader responseHeader;
    protected GrunndataUtskrift.Melding melding;

    /**
     * Gets the value of the responseHeader property.
     * 
     * @return
     *     possible object is
     *     {@link GrunndataUtskrift.ResponseHeader }
     *     
     */
    public GrunndataUtskrift.ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    /**
     * Sets the value of the responseHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link GrunndataUtskrift.ResponseHeader }
     *     
     */
    public void setResponseHeader(GrunndataUtskrift.ResponseHeader value) {
        this.responseHeader = value;
    }

    /**
     * Gets the value of the melding property.
     * 
     * @return
     *     possible object is
     *     {@link GrunndataUtskrift.Melding }
     *     
     */
    public GrunndataUtskrift.Melding getMelding() {
        return melding;
    }

    /**
     * Sets the value of the melding property.
     * 
     * @param value
     *     allowed object is
     *     {@link GrunndataUtskrift.Melding }
     *     
     */
    public void setMelding(GrunndataUtskrift.Melding value) {
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
     *         &lt;element name="rolleInnehaver"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;choice&gt;
     *                     &lt;element name="orgnr"&gt;
     *                       &lt;complexType&gt;
     *                         &lt;simpleContent&gt;
     *                           &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;int"&gt;
     *                             &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                           &lt;/extension&gt;
     *                         &lt;/simpleContent&gt;
     *                       &lt;/complexType&gt;
     *                     &lt;/element&gt;
     *                     &lt;element name="fodselsnr"&gt;
     *                       &lt;complexType&gt;
     *                         &lt;simpleContent&gt;
     *                           &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
     *                             &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                           &lt;/extension&gt;
     *                         &lt;/simpleContent&gt;
     *                       &lt;/complexType&gt;
     *                     &lt;/element&gt;
     *                     &lt;element name="fodselsdato"&gt;
     *                       &lt;complexType&gt;
     *                         &lt;simpleContent&gt;
     *                           &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;date"&gt;
     *                             &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                           &lt;/extension&gt;
     *                         &lt;/simpleContent&gt;
     *                       &lt;/complexType&gt;
     *                     &lt;/element&gt;
     *                   &lt;/choice&gt;
     *                   &lt;element name="navn" type="{}navnType"/&gt;
     *                   &lt;element name="adresse" type="{}adresseType1"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *         &lt;element name="roller" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="enhet" maxOccurs="unbounded"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="rolleBeskrivelse"&gt;
     *                               &lt;complexType&gt;
     *                                 &lt;simpleContent&gt;
     *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
     *                                     &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                                   &lt;/extension&gt;
     *                                 &lt;/simpleContent&gt;
     *                               &lt;/complexType&gt;
     *                             &lt;/element&gt;
     *                             &lt;element name="orgnr"&gt;
     *                               &lt;complexType&gt;
     *                                 &lt;simpleContent&gt;
     *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;int"&gt;
     *                                     &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                                   &lt;/extension&gt;
     *                                 &lt;/simpleContent&gt;
     *                               &lt;/complexType&gt;
     *                             &lt;/element&gt;
     *                             &lt;element name="navn" type="{}navnType"/&gt;
     *                             &lt;element name="adresse"&gt;
     *                               &lt;complexType&gt;
     *                                 &lt;complexContent&gt;
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                                     &lt;sequence&gt;
     *                                       &lt;element name="forretningsAdresse" type="{}adresseType2" minOccurs="0"/&gt;
     *                                       &lt;element name="postAdresse" type="{}adresseType2" minOccurs="0"/&gt;
     *                                     &lt;/sequence&gt;
     *                                   &lt;/restriction&gt;
     *                                 &lt;/complexContent&gt;
     *                               &lt;/complexType&gt;
     *                             &lt;/element&gt;
     *                           &lt;/sequence&gt;
     *                           &lt;attribute name="nr" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
     *                           &lt;attribute name="registreringsDato" use="required" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                 &lt;/sequence&gt;
     *                 &lt;attribute name="ledetekst" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
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
        "rolleInnehaver",
        "roller"
    })
    public static class Melding {

        @XmlElement(required = true)
        protected GrunndataUtskrift.Melding.RolleInnehaver rolleInnehaver;
        protected GrunndataUtskrift.Melding.Roller roller;
        @XmlAttribute(name = "tjeneste", required = true)
        protected String tjeneste;

        /**
         * Gets the value of the rolleInnehaver property.
         * 
         * @return
         *     possible object is
         *     {@link GrunndataUtskrift.Melding.RolleInnehaver }
         *     
         */
        public GrunndataUtskrift.Melding.RolleInnehaver getRolleInnehaver() {
            return rolleInnehaver;
        }

        /**
         * Sets the value of the rolleInnehaver property.
         * 
         * @param value
         *     allowed object is
         *     {@link GrunndataUtskrift.Melding.RolleInnehaver }
         *     
         */
        public void setRolleInnehaver(GrunndataUtskrift.Melding.RolleInnehaver value) {
            this.rolleInnehaver = value;
        }

        /**
         * Gets the value of the roller property.
         * 
         * @return
         *     possible object is
         *     {@link GrunndataUtskrift.Melding.Roller }
         *     
         */
        public GrunndataUtskrift.Melding.Roller getRoller() {
            return roller;
        }

        /**
         * Sets the value of the roller property.
         * 
         * @param value
         *     allowed object is
         *     {@link GrunndataUtskrift.Melding.Roller }
         *     
         */
        public void setRoller(GrunndataUtskrift.Melding.Roller value) {
            this.roller = value;
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
         *         &lt;choice&gt;
         *           &lt;element name="orgnr"&gt;
         *             &lt;complexType&gt;
         *               &lt;simpleContent&gt;
         *                 &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;int"&gt;
         *                   &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *                 &lt;/extension&gt;
         *               &lt;/simpleContent&gt;
         *             &lt;/complexType&gt;
         *           &lt;/element&gt;
         *           &lt;element name="fodselsnr"&gt;
         *             &lt;complexType&gt;
         *               &lt;simpleContent&gt;
         *                 &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
         *                   &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *                 &lt;/extension&gt;
         *               &lt;/simpleContent&gt;
         *             &lt;/complexType&gt;
         *           &lt;/element&gt;
         *           &lt;element name="fodselsdato"&gt;
         *             &lt;complexType&gt;
         *               &lt;simpleContent&gt;
         *                 &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;date"&gt;
         *                   &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *                 &lt;/extension&gt;
         *               &lt;/simpleContent&gt;
         *             &lt;/complexType&gt;
         *           &lt;/element&gt;
         *         &lt;/choice&gt;
         *         &lt;element name="navn" type="{}navnType"/&gt;
         *         &lt;element name="adresse" type="{}adresseType1"/&gt;
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
            "orgnr",
            "fodselsnr",
            "fodselsdato",
            "navn",
            "adresse"
        })
        public static class RolleInnehaver {

            protected GrunndataUtskrift.Melding.RolleInnehaver.Orgnr orgnr;
            protected GrunndataUtskrift.Melding.RolleInnehaver.Fodselsnr fodselsnr;
            protected GrunndataUtskrift.Melding.RolleInnehaver.Fodselsdato fodselsdato;
            @XmlElement(required = true)
            protected NavnType navn;
            @XmlElement(required = true)
            protected AdresseType1 adresse;

            /**
             * Gets the value of the orgnr property.
             * 
             * @return
             *     possible object is
             *     {@link GrunndataUtskrift.Melding.RolleInnehaver.Orgnr }
             *     
             */
            public GrunndataUtskrift.Melding.RolleInnehaver.Orgnr getOrgnr() {
                return orgnr;
            }

            /**
             * Sets the value of the orgnr property.
             * 
             * @param value
             *     allowed object is
             *     {@link GrunndataUtskrift.Melding.RolleInnehaver.Orgnr }
             *     
             */
            public void setOrgnr(GrunndataUtskrift.Melding.RolleInnehaver.Orgnr value) {
                this.orgnr = value;
            }

            /**
             * Gets the value of the fodselsnr property.
             * 
             * @return
             *     possible object is
             *     {@link GrunndataUtskrift.Melding.RolleInnehaver.Fodselsnr }
             *     
             */
            public GrunndataUtskrift.Melding.RolleInnehaver.Fodselsnr getFodselsnr() {
                return fodselsnr;
            }

            /**
             * Sets the value of the fodselsnr property.
             * 
             * @param value
             *     allowed object is
             *     {@link GrunndataUtskrift.Melding.RolleInnehaver.Fodselsnr }
             *     
             */
            public void setFodselsnr(GrunndataUtskrift.Melding.RolleInnehaver.Fodselsnr value) {
                this.fodselsnr = value;
            }

            /**
             * Gets the value of the fodselsdato property.
             * 
             * @return
             *     possible object is
             *     {@link GrunndataUtskrift.Melding.RolleInnehaver.Fodselsdato }
             *     
             */
            public GrunndataUtskrift.Melding.RolleInnehaver.Fodselsdato getFodselsdato() {
                return fodselsdato;
            }

            /**
             * Sets the value of the fodselsdato property.
             * 
             * @param value
             *     allowed object is
             *     {@link GrunndataUtskrift.Melding.RolleInnehaver.Fodselsdato }
             *     
             */
            public void setFodselsdato(GrunndataUtskrift.Melding.RolleInnehaver.Fodselsdato value) {
                this.fodselsdato = value;
            }

            /**
             * Gets the value of the navn property.
             * 
             * @return
             *     possible object is
             *     {@link NavnType }
             *     
             */
            public NavnType getNavn() {
                return navn;
            }

            /**
             * Sets the value of the navn property.
             * 
             * @param value
             *     allowed object is
             *     {@link NavnType }
             *     
             */
            public void setNavn(NavnType value) {
                this.navn = value;
            }

            /**
             * Gets the value of the adresse property.
             * 
             * @return
             *     possible object is
             *     {@link AdresseType1 }
             *     
             */
            public AdresseType1 getAdresse() {
                return adresse;
            }

            /**
             * Sets the value of the adresse property.
             * 
             * @param value
             *     allowed object is
             *     {@link AdresseType1 }
             *     
             */
            public void setAdresse(AdresseType1 value) {
                this.adresse = value;
            }


            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType&gt;
             *   &lt;simpleContent&gt;
             *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;date"&gt;
             *       &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
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
            public static class Fodselsdato {

                @XmlValue
                @XmlSchemaType(name = "date")
                protected XMLGregorianCalendar value;
                @XmlAttribute(name = "ledetekst")
                protected String ledetekst;

                /**
                 * Gets the value of the value property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public XMLGregorianCalendar getValue() {
                    return value;
                }

                /**
                 * Sets the value of the value property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public void setValue(XMLGregorianCalendar value) {
                    this.value = value;
                }

                /**
                 * Gets the value of the ledetekst property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getLedetekst() {
                    return ledetekst;
                }

                /**
                 * Sets the value of the ledetekst property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setLedetekst(String value) {
                    this.ledetekst = value;
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
             *       &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
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
            public static class Fodselsnr {

                @XmlValue
                protected String value;
                @XmlAttribute(name = "ledetekst")
                protected String ledetekst;

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
                 * Gets the value of the ledetekst property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getLedetekst() {
                    return ledetekst;
                }

                /**
                 * Sets the value of the ledetekst property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setLedetekst(String value) {
                    this.ledetekst = value;
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
             *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;int"&gt;
             *       &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
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
            public static class Orgnr {

                @XmlValue
                protected int value;
                @XmlAttribute(name = "ledetekst")
                protected String ledetekst;

                /**
                 * Gets the value of the value property.
                 * 
                 */
                public int getValue() {
                    return value;
                }

                /**
                 * Sets the value of the value property.
                 * 
                 */
                public void setValue(int value) {
                    this.value = value;
                }

                /**
                 * Gets the value of the ledetekst property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getLedetekst() {
                    return ledetekst;
                }

                /**
                 * Sets the value of the ledetekst property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setLedetekst(String value) {
                    this.ledetekst = value;
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
         *         &lt;element name="enhet" maxOccurs="unbounded"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="rolleBeskrivelse"&gt;
         *                     &lt;complexType&gt;
         *                       &lt;simpleContent&gt;
         *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
         *                           &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *                         &lt;/extension&gt;
         *                       &lt;/simpleContent&gt;
         *                     &lt;/complexType&gt;
         *                   &lt;/element&gt;
         *                   &lt;element name="orgnr"&gt;
         *                     &lt;complexType&gt;
         *                       &lt;simpleContent&gt;
         *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;int"&gt;
         *                           &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *                         &lt;/extension&gt;
         *                       &lt;/simpleContent&gt;
         *                     &lt;/complexType&gt;
         *                   &lt;/element&gt;
         *                   &lt;element name="navn" type="{}navnType"/&gt;
         *                   &lt;element name="adresse"&gt;
         *                     &lt;complexType&gt;
         *                       &lt;complexContent&gt;
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                           &lt;sequence&gt;
         *                             &lt;element name="forretningsAdresse" type="{}adresseType2" minOccurs="0"/&gt;
         *                             &lt;element name="postAdresse" type="{}adresseType2" minOccurs="0"/&gt;
         *                           &lt;/sequence&gt;
         *                         &lt;/restriction&gt;
         *                       &lt;/complexContent&gt;
         *                     &lt;/complexType&gt;
         *                   &lt;/element&gt;
         *                 &lt;/sequence&gt;
         *                 &lt;attribute name="nr" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
         *                 &lt;attribute name="registreringsDato" use="required" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *       &lt;/sequence&gt;
         *       &lt;attribute name="ledetekst" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "enhet"
        })
        public static class Roller {

            @XmlElement(required = true)
            protected List<GrunndataUtskrift.Melding.Roller.Enhet> enhet;
            @XmlAttribute(name = "ledetekst", required = true)
            protected String ledetekst;

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
             * {@link GrunndataUtskrift.Melding.Roller.Enhet }
             * 
             * 
             */
            public List<GrunndataUtskrift.Melding.Roller.Enhet> getEnhet() {
                if (enhet == null) {
                    enhet = new ArrayList<GrunndataUtskrift.Melding.Roller.Enhet>();
                }
                return this.enhet;
            }

            /**
             * Gets the value of the ledetekst property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getLedetekst() {
                return ledetekst;
            }

            /**
             * Sets the value of the ledetekst property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setLedetekst(String value) {
                this.ledetekst = value;
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
             *         &lt;element name="rolleBeskrivelse"&gt;
             *           &lt;complexType&gt;
             *             &lt;simpleContent&gt;
             *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
             *                 &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
             *               &lt;/extension&gt;
             *             &lt;/simpleContent&gt;
             *           &lt;/complexType&gt;
             *         &lt;/element&gt;
             *         &lt;element name="orgnr"&gt;
             *           &lt;complexType&gt;
             *             &lt;simpleContent&gt;
             *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;int"&gt;
             *                 &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
             *               &lt;/extension&gt;
             *             &lt;/simpleContent&gt;
             *           &lt;/complexType&gt;
             *         &lt;/element&gt;
             *         &lt;element name="navn" type="{}navnType"/&gt;
             *         &lt;element name="adresse"&gt;
             *           &lt;complexType&gt;
             *             &lt;complexContent&gt;
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
             *                 &lt;sequence&gt;
             *                   &lt;element name="forretningsAdresse" type="{}adresseType2" minOccurs="0"/&gt;
             *                   &lt;element name="postAdresse" type="{}adresseType2" minOccurs="0"/&gt;
             *                 &lt;/sequence&gt;
             *               &lt;/restriction&gt;
             *             &lt;/complexContent&gt;
             *           &lt;/complexType&gt;
             *         &lt;/element&gt;
             *       &lt;/sequence&gt;
             *       &lt;attribute name="nr" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
             *       &lt;attribute name="registreringsDato" use="required" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
             *     &lt;/restriction&gt;
             *   &lt;/complexContent&gt;
             * &lt;/complexType&gt;
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "rolleBeskrivelse",
                "orgnr",
                "navn",
                "adresse"
            })
            public static class Enhet {

                @XmlElement(required = true)
                protected GrunndataUtskrift.Melding.Roller.Enhet.RolleBeskrivelse rolleBeskrivelse;
                @XmlElement(required = true)
                protected GrunndataUtskrift.Melding.Roller.Enhet.Orgnr orgnr;
                @XmlElement(required = true)
                protected NavnType navn;
                @XmlElement(required = true)
                protected GrunndataUtskrift.Melding.Roller.Enhet.Adresse adresse;
                @XmlAttribute(name = "nr", required = true)
                protected int nr;
                @XmlAttribute(name = "registreringsDato", required = true)
                @XmlSchemaType(name = "date")
                protected XMLGregorianCalendar registreringsDato;

                /**
                 * Gets the value of the rolleBeskrivelse property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link GrunndataUtskrift.Melding.Roller.Enhet.RolleBeskrivelse }
                 *     
                 */
                public GrunndataUtskrift.Melding.Roller.Enhet.RolleBeskrivelse getRolleBeskrivelse() {
                    return rolleBeskrivelse;
                }

                /**
                 * Sets the value of the rolleBeskrivelse property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link GrunndataUtskrift.Melding.Roller.Enhet.RolleBeskrivelse }
                 *     
                 */
                public void setRolleBeskrivelse(GrunndataUtskrift.Melding.Roller.Enhet.RolleBeskrivelse value) {
                    this.rolleBeskrivelse = value;
                }

                /**
                 * Gets the value of the orgnr property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link GrunndataUtskrift.Melding.Roller.Enhet.Orgnr }
                 *     
                 */
                public GrunndataUtskrift.Melding.Roller.Enhet.Orgnr getOrgnr() {
                    return orgnr;
                }

                /**
                 * Sets the value of the orgnr property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link GrunndataUtskrift.Melding.Roller.Enhet.Orgnr }
                 *     
                 */
                public void setOrgnr(GrunndataUtskrift.Melding.Roller.Enhet.Orgnr value) {
                    this.orgnr = value;
                }

                /**
                 * Gets the value of the navn property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link NavnType }
                 *     
                 */
                public NavnType getNavn() {
                    return navn;
                }

                /**
                 * Sets the value of the navn property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link NavnType }
                 *     
                 */
                public void setNavn(NavnType value) {
                    this.navn = value;
                }

                /**
                 * Gets the value of the adresse property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link GrunndataUtskrift.Melding.Roller.Enhet.Adresse }
                 *     
                 */
                public GrunndataUtskrift.Melding.Roller.Enhet.Adresse getAdresse() {
                    return adresse;
                }

                /**
                 * Sets the value of the adresse property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link GrunndataUtskrift.Melding.Roller.Enhet.Adresse }
                 *     
                 */
                public void setAdresse(GrunndataUtskrift.Melding.Roller.Enhet.Adresse value) {
                    this.adresse = value;
                }

                /**
                 * Gets the value of the nr property.
                 * 
                 */
                public int getNr() {
                    return nr;
                }

                /**
                 * Sets the value of the nr property.
                 * 
                 */
                public void setNr(int value) {
                    this.nr = value;
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
                 * <p>Java class for anonymous complex type.
                 * 
                 * <p>The following schema fragment specifies the expected content contained within this class.
                 * 
                 * <pre>
                 * &lt;complexType&gt;
                 *   &lt;complexContent&gt;
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
                 *       &lt;sequence&gt;
                 *         &lt;element name="forretningsAdresse" type="{}adresseType2" minOccurs="0"/&gt;
                 *         &lt;element name="postAdresse" type="{}adresseType2" minOccurs="0"/&gt;
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
                    "forretningsAdresse",
                    "postAdresse"
                })
                public static class Adresse {

                    protected AdresseType2 forretningsAdresse;
                    protected AdresseType2 postAdresse;

                    /**
                     * Gets the value of the forretningsAdresse property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link AdresseType2 }
                     *     
                     */
                    public AdresseType2 getForretningsAdresse() {
                        return forretningsAdresse;
                    }

                    /**
                     * Sets the value of the forretningsAdresse property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link AdresseType2 }
                     *     
                     */
                    public void setForretningsAdresse(AdresseType2 value) {
                        this.forretningsAdresse = value;
                    }

                    /**
                     * Gets the value of the postAdresse property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link AdresseType2 }
                     *     
                     */
                    public AdresseType2 getPostAdresse() {
                        return postAdresse;
                    }

                    /**
                     * Sets the value of the postAdresse property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link AdresseType2 }
                     *     
                     */
                    public void setPostAdresse(AdresseType2 value) {
                        this.postAdresse = value;
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
                 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;int"&gt;
                 *       &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
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
                public static class Orgnr {

                    @XmlValue
                    protected int value;
                    @XmlAttribute(name = "ledetekst")
                    protected String ledetekst;

                    /**
                     * Gets the value of the value property.
                     * 
                     */
                    public int getValue() {
                        return value;
                    }

                    /**
                     * Sets the value of the value property.
                     * 
                     */
                    public void setValue(int value) {
                        this.value = value;
                    }

                    /**
                     * Gets the value of the ledetekst property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getLedetekst() {
                        return ledetekst;
                    }

                    /**
                     * Sets the value of the ledetekst property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setLedetekst(String value) {
                        this.ledetekst = value;
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
                 *       &lt;attribute name="ledetekst" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
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
                public static class RolleBeskrivelse {

                    @XmlValue
                    protected String value;
                    @XmlAttribute(name = "ledetekst")
                    protected String ledetekst;

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
                     * Gets the value of the ledetekst property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getLedetekst() {
                        return ledetekst;
                    }

                    /**
                     * Sets the value of the ledetekst property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setLedetekst(String value) {
                        this.ledetekst = value;
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
     *       &lt;sequence&gt;
     *         &lt;choice&gt;
     *           &lt;element name="orgnr" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
     *           &lt;element name="fodselsnr"&gt;
     *             &lt;simpleType&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *                 &lt;length value="11"/&gt;
     *               &lt;/restriction&gt;
     *             &lt;/simpleType&gt;
     *           &lt;/element&gt;
     *           &lt;element name="requestID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
     *         &lt;/choice&gt;
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
        "orgnr",
        "fodselsnr",
        "requestID",
        "hovedStatus",
        "underStatus"
    })
    public static class ResponseHeader {

        protected Integer orgnr;
        protected String fodselsnr;
        protected String requestID;
        protected int hovedStatus;
        @XmlElement(required = true)
        protected GrunndataUtskrift.ResponseHeader.UnderStatus underStatus;
        @XmlAttribute(name = "prossessDato", required = true)
        @XmlSchemaType(name = "date")
        protected XMLGregorianCalendar prossessDato;
        @XmlAttribute(name = "tjeneste", required = true)
        protected String tjeneste;

        /**
         * Gets the value of the orgnr property.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getOrgnr() {
            return orgnr;
        }

        /**
         * Sets the value of the orgnr property.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setOrgnr(Integer value) {
            this.orgnr = value;
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
         * Gets the value of the requestID property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRequestID() {
            return requestID;
        }

        /**
         * Sets the value of the requestID property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRequestID(String value) {
            this.requestID = value;
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
         *     {@link GrunndataUtskrift.ResponseHeader.UnderStatus }
         *     
         */
        public GrunndataUtskrift.ResponseHeader.UnderStatus getUnderStatus() {
            return underStatus;
        }

        /**
         * Sets the value of the underStatus property.
         * 
         * @param value
         *     allowed object is
         *     {@link GrunndataUtskrift.ResponseHeader.UnderStatus }
         *     
         */
        public void setUnderStatus(GrunndataUtskrift.ResponseHeader.UnderStatus value) {
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
            protected List<GrunndataUtskrift.ResponseHeader.UnderStatus.UnderStatusMelding> underStatusMelding;

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
             * {@link GrunndataUtskrift.ResponseHeader.UnderStatus.UnderStatusMelding }
             * 
             * 
             */
            public List<GrunndataUtskrift.ResponseHeader.UnderStatus.UnderStatusMelding> getUnderStatusMelding() {
                if (underStatusMelding == null) {
                    underStatusMelding = new ArrayList<GrunndataUtskrift.ResponseHeader.UnderStatus.UnderStatusMelding>();
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
