
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Typen beskriver alle feltene i en Utenlandsk Bankinformasjon.
 *
 * <p>Java class for utlandbankType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="utlandbankType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="utlandbankGironummer" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandbankSwiftKode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandbankIban" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandbankBankKode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandbankBanknavn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandbankAdresse1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandbankAdresse2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandbankAdresse3" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandbankValuta" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandbankLand" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandbankFraDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandbankTilDato" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandbankKilde" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="utlandbankRegistrertAv" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "utlandbank", propOrder = {
        "utlandbankGironummer",
        "utlandbankSwiftKode",
        "utlandbankIban",
        "utlandbankBankKode",
        "utlandbankBanknavn",
        "utlandbankAdresse1",
        "utlandbankAdresse2",
        "utlandbankAdresse3",
        "utlandbankValuta",
        "utlandbankLand",
        "utlandbankFraDato",
        "utlandbankTilDato",
        "utlandbankKilde",
        "utlandbankRegistrertAv"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utlandbank {

    @XmlElement(required = true)
    protected String utlandbankGironummer;
    @XmlElement(required = true)
    protected String utlandbankSwiftKode;
    @XmlElement(required = true)
    protected String utlandbankIban;
    @XmlElement(required = true)
    protected String utlandbankBankKode;
    @XmlElement(required = true)
    protected String utlandbankBanknavn;
    @XmlElement(required = true)
    protected String utlandbankAdresse1;
    @XmlElement(required = true)
    protected String utlandbankAdresse2;
    @XmlElement(required = true)
    protected String utlandbankAdresse3;
    @XmlElement(required = true)
    protected String utlandbankValuta;
    @XmlElement(required = true)
    protected String utlandbankLand;
    @XmlElement(required = true)
    protected String utlandbankFraDato;
    @XmlElement(required = true)
    protected String utlandbankTilDato;
    @XmlElement(required = true)
    protected String utlandbankKilde;
    @XmlElement(required = true)
    protected String utlandbankRegistrertAv;

    /**
     * Gets the value of the utlandbankGironummer property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandbankGironummer() {
        return utlandbankGironummer;
    }

    /**
     * Sets the value of the utlandbankGironummer property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandbankGironummer(String value) {
        this.utlandbankGironummer = value;
    }

    /**
     * Gets the value of the utlandbankSwiftKode property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandbankSwiftKode() {
        return utlandbankSwiftKode;
    }

    /**
     * Sets the value of the utlandbankSwiftKode property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandbankSwiftKode(String value) {
        this.utlandbankSwiftKode = value;
    }

    /**
     * Gets the value of the utlandbankIban property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandbankIban() {
        return utlandbankIban;
    }

    /**
     * Sets the value of the utlandbankIban property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandbankIban(String value) {
        this.utlandbankIban = value;
    }

    /**
     * Gets the value of the utlandbankBankKode property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandbankBankKode() {
        return utlandbankBankKode;
    }

    /**
     * Sets the value of the utlandbankBankKode property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandbankBankKode(String value) {
        this.utlandbankBankKode = value;
    }

    /**
     * Gets the value of the utlandbankBanknavn property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandbankBanknavn() {
        return utlandbankBanknavn;
    }

    /**
     * Sets the value of the utlandbankBanknavn property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandbankBanknavn(String value) {
        this.utlandbankBanknavn = value;
    }

    /**
     * Gets the value of the utlandbankAdresse1 property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandbankAdresse1() {
        return utlandbankAdresse1;
    }

    /**
     * Sets the value of the utlandbankAdresse1 property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandbankAdresse1(String value) {
        this.utlandbankAdresse1 = value;
    }

    /**
     * Gets the value of the utlandbankAdresse2 property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandbankAdresse2() {
        return utlandbankAdresse2;
    }

    /**
     * Sets the value of the utlandbankAdresse2 property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandbankAdresse2(String value) {
        this.utlandbankAdresse2 = value;
    }

    /**
     * Gets the value of the utlandbankAdresse3 property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandbankAdresse3() {
        return utlandbankAdresse3;
    }

    /**
     * Sets the value of the utlandbankAdresse3 property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandbankAdresse3(String value) {
        this.utlandbankAdresse3 = value;
    }

    /**
     * Gets the value of the utlandbankValuta property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandbankValuta() {
        return utlandbankValuta;
    }

    /**
     * Sets the value of the utlandbankValuta property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandbankValuta(String value) {
        this.utlandbankValuta = value;
    }

    /**
     * Gets the value of the utlandbankLand property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandbankLand() {
        return utlandbankLand;
    }

    /**
     * Sets the value of the utlandbankLand property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandbankLand(String value) {
        this.utlandbankLand = value;
    }

    /**
     * Gets the value of the utlandbankFraDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandbankFraDato() {
        return utlandbankFraDato;
    }

    /**
     * Sets the value of the utlandbankFraDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandbankFraDato(String value) {
        this.utlandbankFraDato = value;
    }

    /**
     * Gets the value of the utlandbankTilDato property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandbankTilDato() {
        return utlandbankTilDato;
    }

    /**
     * Sets the value of the utlandbankTilDato property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandbankTilDato(String value) {
        this.utlandbankTilDato = value;
    }

    /**
     * Gets the value of the utlandbankKilde property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandbankKilde() {
        return utlandbankKilde;
    }

    /**
     * Sets the value of the utlandbankKilde property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandbankKilde(String value) {
        this.utlandbankKilde = value;
    }

    /**
     * Gets the value of the utlandbankRegistrertAv property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getUtlandbankRegistrertAv() {
        return utlandbankRegistrertAv;
    }

    /**
     * Sets the value of the utlandbankRegistrertAv property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setUtlandbankRegistrertAv(String value) {
        this.utlandbankRegistrertAv = value;
    }

}
