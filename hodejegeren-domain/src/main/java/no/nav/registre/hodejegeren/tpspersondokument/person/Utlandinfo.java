
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene i Utenlandsk Identinformasjon.
 *
 * <p>Java class for utlandinfoType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="utlandinfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="utlandinfoIdOff" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoLand" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoSektor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoInstitusjon" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoKildePin" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoFamnavnFodt" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoFornavnFodt" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoFodested" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoFarsFamnavn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoMorsFamnavn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoFarsFornavn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoMorsFornavn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoNasjonalitet" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoSedRef" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoNasjonalId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoInstitusjonNavn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandinfoKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "utlandinfo", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "utlandinfoIdOff",
        "utlandinfoLand",
        "utlandinfoSektor",
        "utlandinfoInstitusjon",
        "utlandinfoKildePin",
        "utlandinfoFamnavnFodt",
        "utlandinfoFornavnFodt",
        "utlandinfoFodested",
        "utlandinfoFarsFamnavn",
        "utlandinfoMorsFamnavn",
        "utlandinfoFarsFornavn",
        "utlandinfoMorsFornavn",
        "utlandinfoNasjonalitet",
        "utlandinfoSedRef",
        "utlandinfoNasjonalId",
        "utlandinfoInstitusjonNavn",
        "utlandinfoFraDato",
        "utlandinfoTilDato",
        "utlandinfoKilde"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utlandinfo {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoIdOff;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoLand;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoSektor;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoInstitusjon;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoKildePin;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoFamnavnFodt;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoFornavnFodt;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoFodested;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoFarsFamnavn;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoMorsFamnavn;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoFarsFornavn;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoMorsFornavn;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoNasjonalitet;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoSedRef;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoNasjonalId;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoInstitusjonNavn;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoFraDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoTilDato;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected String utlandinfoKilde;

    /**
     * Gets the value of the utlandinfoIdOff property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoIdOff() {
        return utlandinfoIdOff;
    }

    /**
     * Sets the value of the utlandinfoIdOff property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoIdOff(String value) {
        this.utlandinfoIdOff = value;
    }

    /**
     * Gets the value of the utlandinfoLand property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoLand() {
        return utlandinfoLand;
    }

    /**
     * Sets the value of the utlandinfoLand property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoLand(String value) {
        this.utlandinfoLand = value;
    }

    /**
     * Gets the value of the utlandinfoSektor property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoSektor() {
        return utlandinfoSektor;
    }

    /**
     * Sets the value of the utlandinfoSektor property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoSektor(String value) {
        this.utlandinfoSektor = value;
    }

    /**
     * Gets the value of the utlandinfoInstitusjon property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoInstitusjon() {
        return utlandinfoInstitusjon;
    }

    /**
     * Sets the value of the utlandinfoInstitusjon property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoInstitusjon(String value) {
        this.utlandinfoInstitusjon = value;
    }

    /**
     * Gets the value of the utlandinfoKildePin property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoKildePin() {
        return utlandinfoKildePin;
    }

    /**
     * Sets the value of the utlandinfoKildePin property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoKildePin(String value) {
        this.utlandinfoKildePin = value;
    }

    /**
     * Gets the value of the utlandinfoFamnavnFodt property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoFamnavnFodt() {
        return utlandinfoFamnavnFodt;
    }

    /**
     * Sets the value of the utlandinfoFamnavnFodt property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoFamnavnFodt(String value) {
        this.utlandinfoFamnavnFodt = value;
    }

    /**
     * Gets the value of the utlandinfoFornavnFodt property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoFornavnFodt() {
        return utlandinfoFornavnFodt;
    }

    /**
     * Sets the value of the utlandinfoFornavnFodt property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoFornavnFodt(String value) {
        this.utlandinfoFornavnFodt = value;
    }

    /**
     * Gets the value of the utlandinfoFodested property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoFodested() {
        return utlandinfoFodested;
    }

    /**
     * Sets the value of the utlandinfoFodested property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoFodested(String value) {
        this.utlandinfoFodested = value;
    }

    /**
     * Gets the value of the utlandinfoFarsFamnavn property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoFarsFamnavn() {
        return utlandinfoFarsFamnavn;
    }

    /**
     * Sets the value of the utlandinfoFarsFamnavn property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoFarsFamnavn(String value) {
        this.utlandinfoFarsFamnavn = value;
    }

    /**
     * Gets the value of the utlandinfoMorsFamnavn property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoMorsFamnavn() {
        return utlandinfoMorsFamnavn;
    }

    /**
     * Sets the value of the utlandinfoMorsFamnavn property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoMorsFamnavn(String value) {
        this.utlandinfoMorsFamnavn = value;
    }

    /**
     * Gets the value of the utlandinfoFarsFornavn property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoFarsFornavn() {
        return utlandinfoFarsFornavn;
    }

    /**
     * Sets the value of the utlandinfoFarsFornavn property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoFarsFornavn(String value) {
        this.utlandinfoFarsFornavn = value;
    }

    /**
     * Gets the value of the utlandinfoMorsFornavn property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoMorsFornavn() {
        return utlandinfoMorsFornavn;
    }

    /**
     * Sets the value of the utlandinfoMorsFornavn property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoMorsFornavn(String value) {
        this.utlandinfoMorsFornavn = value;
    }

    /**
     * Gets the value of the utlandinfoNasjonalitet property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoNasjonalitet() {
        return utlandinfoNasjonalitet;
    }

    /**
     * Sets the value of the utlandinfoNasjonalitet property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoNasjonalitet(String value) {
        this.utlandinfoNasjonalitet = value;
    }

    /**
     * Gets the value of the utlandinfoSedRef property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoSedRef() {
        return utlandinfoSedRef;
    }

    /**
     * Sets the value of the utlandinfoSedRef property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoSedRef(String value) {
        this.utlandinfoSedRef = value;
    }

    /**
     * Gets the value of the utlandinfoNasjonalId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoNasjonalId() {
        return utlandinfoNasjonalId;
    }

    /**
     * Sets the value of the utlandinfoNasjonalId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoNasjonalId(String value) {
        this.utlandinfoNasjonalId = value;
    }

    /**
     * Gets the value of the utlandinfoInstitusjonNavn property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoInstitusjonNavn() {
        return utlandinfoInstitusjonNavn;
    }

    /**
     * Sets the value of the utlandinfoInstitusjonNavn property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoInstitusjonNavn(String value) {
        this.utlandinfoInstitusjonNavn = value;
    }

    /**
     * Gets the value of the utlandinfoFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoFraDato() {
        return utlandinfoFraDato;
    }

    /**
     * Sets the value of the utlandinfoFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoFraDato(String value) {
        this.utlandinfoFraDato = value;
    }

    /**
     * Gets the value of the utlandinfoTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoTilDato() {
        return utlandinfoTilDato;
    }

    /**
     * Sets the value of the utlandinfoTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoTilDato(String value) {
        this.utlandinfoTilDato = value;
    }

    /**
     * Gets the value of the utlandinfoKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandinfoKilde() {
        return utlandinfoKilde;
    }

    /**
     * Sets the value of the utlandinfoKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandinfoKilde(String value) {
        this.utlandinfoKilde = value;
    }

}
