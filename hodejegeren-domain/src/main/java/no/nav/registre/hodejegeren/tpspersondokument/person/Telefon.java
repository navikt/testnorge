
package no.nav.registre.hodejegeren.tpspersondokument.person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import no.nav.registre.hodejegeren.tpspersondokument.person.telefon.TelefonJobb;
import no.nav.registre.hodejegeren.tpspersondokument.person.telefon.TelefonMobil;
import no.nav.registre.hodejegeren.tpspersondokument.person.telefon.TelefonPrivat;

/**
 * Typen beskriver alle feltene for telefon.
 *
 * <p>Java class for telefonType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="telefonType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tlfPrivat" type="{http://www.rtv.no/NamespaceTPS}telefonPrivat" minOccurs="0"/>
 *         &lt;element name="tlfJobb" type="{http://www.rtv.no/NamespaceTPS}telefonJobb" minOccurs="0"/>
 *         &lt;element name="tlfMobil" type="{http://www.rtv.no/NamespaceTPS}telefonMobil" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "telefon", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "tlfPrivat",
        "tlfJobb",
        "tlfMobil"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Telefon {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS")
    protected TelefonPrivat tlfPrivat;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS")
    protected TelefonJobb tlfJobb;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS")
    protected TelefonMobil tlfMobil;

    /**
     * Gets the value of the tlfPrivat property.
     *
     * @return possible object is
     * {@link TelefonPrivat }
     */
    public TelefonPrivat getTlfPrivat() {
        return tlfPrivat;
    }

    /**
     * Sets the value of the tlfPrivat property.
     *
     * @param value allowed object is
     *              {@link TelefonPrivat }
     */
    public void setTlfPrivat(TelefonPrivat value) {
        this.tlfPrivat = value;
    }

    /**
     * Gets the value of the tlfJobb property.
     *
     * @return possible object is
     * {@link TelefonJobb }
     */
    public TelefonJobb getTlfJobb() {
        return tlfJobb;
    }

    /**
     * Sets the value of the tlfJobb property.
     *
     * @param value allowed object is
     *              {@link TelefonJobb }
     */
    public void setTlfJobb(TelefonJobb value) {
        this.tlfJobb = value;
    }

    /**
     * Gets the value of the tlfMobil property.
     *
     * @return possible object is
     * {@link TelefonMobil }
     */
    public TelefonMobil getTlfMobil() {
        return tlfMobil;
    }

    /**
     * Sets the value of the tlfMobil property.
     *
     * @param value allowed object is
     *              {@link TelefonMobil }
     */
    public void setTlfMobil(TelefonMobil value) {
        this.tlfMobil = value;
    }

}
