
package no.nav.registre.hodejegeren.tpspersondokument;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import no.nav.registre.hodejegeren.tpspersondokument.person.Behov;
import no.nav.registre.hodejegeren.tpspersondokument.person.Boadresse;
import no.nav.registre.hodejegeren.tpspersondokument.person.Dod;
import no.nav.registre.hodejegeren.tpspersondokument.person.Egenansatt;
import no.nav.registre.hodejegeren.tpspersondokument.person.Foreldreansvar;
import no.nav.registre.hodejegeren.tpspersondokument.person.Geti;
import no.nav.registre.hodejegeren.tpspersondokument.person.Gironummer;
import no.nav.registre.hodejegeren.tpspersondokument.person.Migrasjon;
import no.nav.registre.hodejegeren.tpspersondokument.person.Navn;
import no.nav.registre.hodejegeren.tpspersondokument.person.Oppholdstillatelse;
import no.nav.registre.hodejegeren.tpspersondokument.person.PersonIdent;
import no.nav.registre.hodejegeren.tpspersondokument.person.PersonIdentStatus;
import no.nav.registre.hodejegeren.tpspersondokument.person.PersonInfo;
import no.nav.registre.hodejegeren.tpspersondokument.person.PersonStatus;
import no.nav.registre.hodejegeren.tpspersondokument.person.Post;
import no.nav.registre.hodejegeren.tpspersondokument.person.Prioritertadresse;
import no.nav.registre.hodejegeren.tpspersondokument.person.Sivilstand;
import no.nav.registre.hodejegeren.tpspersondokument.person.SpesReg;
import no.nav.registre.hodejegeren.tpspersondokument.person.Spraak;
import no.nav.registre.hodejegeren.tpspersondokument.person.Statsborger;
import no.nav.registre.hodejegeren.tpspersondokument.person.Telefon;
import no.nav.registre.hodejegeren.tpspersondokument.person.Tillegg;
import no.nav.registre.hodejegeren.tpspersondokument.person.Tiltak;
import no.nav.registre.hodejegeren.tpspersondokument.person.Tknr;
import no.nav.registre.hodejegeren.tpspersondokument.person.Utad;
import no.nav.registre.hodejegeren.tpspersondokument.person.Utlandbank;
import no.nav.registre.hodejegeren.tpspersondokument.person.Utlandinfo;
import no.nav.registre.hodejegeren.tpspersondokument.person.Verge;

/**
 * Typen beskriver alle feltene i persondokumentet.
 *
 * <p>Java class for person complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="person">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="meldingId" type="{http://www.rtv.no/NamespaceTPS}meldingid"/>
 *         &lt;element name="personidTPS" type="{http://www.rtv.no/NamespaceTPS}personidTPS"/>
 *         &lt;element name="personIdent" type="{http://www.rtv.no/NamespaceTPS}personIdent" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="personIdentstatus" type="{http://www.rtv.no/NamespaceTPS}personIdentStatus" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="personInfo" type="{http://www.rtv.no/NamespaceTPS}personInfo" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="personStatus" type="{http://www.rtv.no/NamespaceTPS}personStatus" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="navn" type="{http://www.rtv.no/NamespaceTPS}navn" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="spraak" type="{http://www.rtv.no/NamespaceTPS}spraak" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="sivilstand" type="{http://www.rtv.no/NamespaceTPS}sivilstand" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="statsborger" type="{http://www.rtv.no/NamespaceTPS}statsborger" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="dod" type="{http://www.rtv.no/NamespaceTPS}dod" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="telefon" type="{http://www.rtv.no/NamespaceTPS}telefon" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="tknr" type="{http://www.rtv.no/NamespaceTPS}tknr" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="spesReg" type="{http://www.rtv.no/NamespaceTPS}spesReg" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="tiltak" type="{http://www.rtv.no/NamespaceTPS}tiltak" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="egenansatt" type="{http://www.rtv.no/NamespaceTPS}egenansatt" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="boadresse" type="{http://www.rtv.no/NamespaceTPS}boadresse" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="prioritertadresse" type="{http://www.rtv.no/NamespaceTPS}prioritertadresse" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="geti" type="{http://www.rtv.no/NamespaceTPS}geti" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="foreldreansvar" type="{http://www.rtv.no/NamespaceTPS}foreldreansvar" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="oppholdstillatelse" type="{http://www.rtv.no/NamespaceTPS}oppholdstillatelse" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="gironummer" type="{http://www.rtv.no/NamespaceTPS}gironummer" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="tillegg" type="{http://www.rtv.no/NamespaceTPS}tillegg" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="utad" type="{http://www.rtv.no/NamespaceTPS}utad" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="post" type="{http://www.rtv.no/NamespaceTPS}Post" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="migrasjon" type="{http://www.rtv.no/NamespaceTPS}migrasjon" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="vergemaal" type="{http://www.rtv.no/NamespaceTPS}verge" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="behov" type="{http://www.rtv.no/NamespaceTPS}behov" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="utlandbank" type="{http://www.rtv.no/NamespaceTPS}utlandbank" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="utlandinfo" type="{http://www.rtv.no/NamespaceTPS}utlandinfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "person", propOrder = {
        "meldingId",
        "personidTPS",
        "personIdent",
        "personIdentstatus",
        "personInfo",
        "personStatus",
        "navn",
        "spraak",
        "sivilstand",
        "statsborger",
        "dod",
        "telefon",
        "tknr",
        "spesReg",
        "tiltak",
        "egenansatt",
        "boadresse",
        "prioritertadresse",
        "geti",
        "foreldreansvar",
        "oppholdstillatelse",
        "gironummer",
        "tillegg",
        "utad",
        "post",
        "migrasjon",
        "vergemaal",
        "behov",
        "utlandbank",
        "utlandinfo"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {

    @XmlElement(required = true)
    protected BigInteger meldingId;
    @XmlElement(required = true)
    protected BigInteger personidTPS;
    @XmlElement()
    protected List<PersonIdent> personIdent;
    @XmlElement()
    protected List<PersonIdentStatus> personIdentstatus;
    @XmlElement()
    protected List<PersonInfo> personInfo;
    @XmlElement()
    protected List<PersonStatus> personStatus;
    @XmlElement()
    protected List<Navn> navn;
    @XmlElement()
    protected List<Spraak> spraak;
    @XmlElement()
    protected List<Sivilstand> sivilstand;
    @XmlElement()
    protected List<Statsborger> statsborger;
    @XmlElement()
    protected List<Dod> dod;
    @XmlElement()
    protected List<Telefon> telefon;
    @XmlElement()
    protected List<Tknr> tknr;
    @XmlElement()
    protected List<SpesReg> spesReg;
    @XmlElement()
    protected List<Tiltak> tiltak;
    @XmlElement()
    protected List<Egenansatt> egenansatt;
    @XmlElement()
    protected List<Boadresse> boadresse;
    @XmlElement()
    protected List<Prioritertadresse> prioritertadresse;
    @XmlElement()
    protected List<Geti> geti;
    @XmlElement()
    protected List<Foreldreansvar> foreldreansvar;
    @XmlElement()
    protected List<Oppholdstillatelse> oppholdstillatelse;
    @XmlElement()
    protected List<Gironummer> gironummer;
    @XmlElement()
    protected List<Tillegg> tillegg;
    @XmlElement()
    protected List<Utad> utad;
    @XmlElement()
    protected List<Post> post;
    @XmlElement()
    protected List<Migrasjon> migrasjon;
    @XmlElement()
    protected List<Verge> vergemaal;
    @XmlElement()
    protected List<Behov> behov;
    @XmlElement()
    protected List<Utlandbank> utlandbank;
    @XmlElement()
    protected List<Utlandinfo> utlandinfo;

    /**
     * Gets the value of the meldingId property.
     *
     * @return possible object is
     * {@link BigInteger }
     */
    public BigInteger getMeldingId() {
        return meldingId;
    }

    /**
     * Sets the value of the meldingId property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setMeldingId(BigInteger value) {
        this.meldingId = value;
    }

    /**
     * Gets the value of the personidTPS property.
     *
     * @return possible object is
     * {@link BigInteger }
     */
    public BigInteger getPersonidTPS() {
        return personidTPS;
    }

    /**
     * Sets the value of the personidTPS property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setPersonidTPS(BigInteger value) {
        this.personidTPS = value;
    }

    /**
     * Gets the value of the personIdent property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the personIdent property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPersonIdent().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonIdent }
     */
    public List<PersonIdent> getPersonIdent() {
        if (personIdent == null) {
            personIdent = new ArrayList<PersonIdent>();
        }
        return this.personIdent;
    }

    /**
     * Gets the value of the personIdentstatus property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the personIdentstatus property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPersonIdentstatus().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonIdentStatus }
     */
    public List<PersonIdentStatus> getPersonIdentstatus() {
        if (personIdentstatus == null) {
            personIdentstatus = new ArrayList<PersonIdentStatus>();
        }
        return this.personIdentstatus;
    }

    /**
     * Gets the value of the personInfo property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the personInfo property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPersonInfo().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonInfo }
     */
    public List<PersonInfo> getPersonInfo() {
        if (personInfo == null) {
            personInfo = new ArrayList<PersonInfo>();
        }
        return this.personInfo;
    }

    /**
     * Gets the value of the personStatus property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the personStatus property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPersonStatus().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonStatus }
     */
    public List<PersonStatus> getPersonStatus() {
        if (personStatus == null) {
            personStatus = new ArrayList<PersonStatus>();
        }
        return this.personStatus;
    }

    /**
     * Gets the value of the navn property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the navn property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNavn().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Navn }
     */
    public List<Navn> getNavn() {
        if (navn == null) {
            navn = new ArrayList<Navn>();
        }
        return this.navn;
    }

    /**
     * Gets the value of the spraak property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spraak property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpraak().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Spraak }
     */
    public List<Spraak> getSpraak() {
        if (spraak == null) {
            spraak = new ArrayList<Spraak>();
        }
        return this.spraak;
    }

    /**
     * Gets the value of the sivilstand property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sivilstand property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSivilstand().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Sivilstand }
     */
    public List<Sivilstand> getSivilstand() {
        if (sivilstand == null) {
            sivilstand = new ArrayList<Sivilstand>();
        }
        return this.sivilstand;
    }

    /**
     * Gets the value of the statsborger property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the statsborger property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStatsborger().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Statsborger }
     */
    public List<Statsborger> getStatsborger() {
        if (statsborger == null) {
            statsborger = new ArrayList<Statsborger>();
        }
        return this.statsborger;
    }

    /**
     * Gets the value of the dod property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dod property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDod().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Dod }
     */
    public List<Dod> getDod() {
        if (dod == null) {
            dod = new ArrayList<Dod>();
        }
        return this.dod;
    }

    /**
     * Gets the value of the telefon property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the telefon property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTelefon().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Telefon }
     */
    public List<Telefon> getTelefon() {
        if (telefon == null) {
            telefon = new ArrayList<Telefon>();
        }
        return this.telefon;
    }

    /**
     * Gets the value of the tknr property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tknr property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTknr().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Tknr }
     */
    public List<Tknr> getTknr() {
        if (tknr == null) {
            tknr = new ArrayList<Tknr>();
        }
        return this.tknr;
    }

    /**
     * Gets the value of the spesReg property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spesReg property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpesReg().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpesReg }
     */
    public List<SpesReg> getSpesReg() {
        if (spesReg == null) {
            spesReg = new ArrayList<SpesReg>();
        }
        return this.spesReg;
    }

    /**
     * Gets the value of the tiltak property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tiltak property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTiltak().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Tiltak }
     */
    public List<Tiltak> getTiltak() {
        if (tiltak == null) {
            tiltak = new ArrayList<Tiltak>();
        }
        return this.tiltak;
    }

    /**
     * Gets the value of the egenansatt property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the egenansatt property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEgenansatt().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Egenansatt }
     */
    public List<Egenansatt> getEgenansatt() {
        if (egenansatt == null) {
            egenansatt = new ArrayList<Egenansatt>();
        }
        return this.egenansatt;
    }

    /**
     * Gets the value of the boadresse property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the boadresse property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBoadresse().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Boadresse }
     */
    public List<Boadresse> getBoadresse() {
        if (boadresse == null) {
            boadresse = new ArrayList<Boadresse>();
        }
        return this.boadresse;
    }

    /**
     * Gets the value of the prioritertadresse property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the prioritertadresse property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrioritertadresse().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Prioritertadresse }
     */
    public List<Prioritertadresse> getPrioritertadresse() {
        if (prioritertadresse == null) {
            prioritertadresse = new ArrayList<Prioritertadresse>();
        }
        return this.prioritertadresse;
    }

    /**
     * Gets the value of the geti property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the geti property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGeti().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Geti }
     */
    public List<Geti> getGeti() {
        if (geti == null) {
            geti = new ArrayList<Geti>();
        }
        return this.geti;
    }

    /**
     * Gets the value of the foreldreansvar property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the foreldreansvar property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getForeldreansvar().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Foreldreansvar }
     */
    public List<Foreldreansvar> getForeldreansvar() {
        if (foreldreansvar == null) {
            foreldreansvar = new ArrayList<Foreldreansvar>();
        }
        return this.foreldreansvar;
    }

    /**
     * Gets the value of the oppholdstillatelse property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the oppholdstillatelse property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOppholdstillatelse().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Oppholdstillatelse }
     */
    public List<Oppholdstillatelse> getOppholdstillatelse() {
        if (oppholdstillatelse == null) {
            oppholdstillatelse = new ArrayList<Oppholdstillatelse>();
        }
        return this.oppholdstillatelse;
    }

    /**
     * Gets the value of the gironummer property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the gironummer property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGironummer().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Gironummer }
     */
    public List<Gironummer> getGironummer() {
        if (gironummer == null) {
            gironummer = new ArrayList<Gironummer>();
        }
        return this.gironummer;
    }

    /**
     * Gets the value of the tillegg property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tillegg property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTillegg().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Tillegg }
     */
    public List<Tillegg> getTillegg() {
        if (tillegg == null) {
            tillegg = new ArrayList<Tillegg>();
        }
        return this.tillegg;
    }

    /**
     * Gets the value of the utad property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the utad property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUtad().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Utad }
     */
    public List<Utad> getUtad() {
        if (utad == null) {
            utad = new ArrayList<Utad>();
        }
        return this.utad;
    }

    /**
     * Gets the value of the post property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the post property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPost().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Post }
     */
    public List<Post> getPost() {
        if (post == null) {
            post = new ArrayList<Post>();
        }
        return this.post;
    }

    /**
     * Gets the value of the migrasjon property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the migrasjon property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMigrasjon().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Migrasjon }
     */
    public List<Migrasjon> getMigrasjon() {
        if (migrasjon == null) {
            migrasjon = new ArrayList<Migrasjon>();
        }
        return this.migrasjon;
    }

    /**
     * Gets the value of the vergemaal property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vergemaal property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVergemaal().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Verge }
     */
    public List<Verge> getVergemaal() {
        if (vergemaal == null) {
            vergemaal = new ArrayList<Verge>();
        }
        return this.vergemaal;
    }

    /**
     * Gets the value of the behov property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the behov property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBehov().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Behov }
     */
    public List<Behov> getBehov() {
        if (behov == null) {
            behov = new ArrayList<Behov>();
        }
        return this.behov;
    }

    /**
     * Gets the value of the utlandbank property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the utlandbank property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUtlandbank().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Utlandbank }
     */
    public List<Utlandbank> getUtlandbank() {
        if (utlandbank == null) {
            utlandbank = new ArrayList<Utlandbank>();
        }
        return this.utlandbank;
    }

    /**
     * Gets the value of the utlandinfo property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the utlandinfo property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUtlandinfo().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Utlandinfo }
     */
    public List<Utlandinfo> getUtlandinfo() {
        if (utlandinfo == null) {
            utlandinfo = new ArrayList<Utlandinfo>();
        }
        return this.utlandinfo;
    }

}
