
package no.nav.registre.hodejegeren;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

import no.nav.registre.hodejegeren.tpsPersonDokument.Dokument;
import no.nav.registre.hodejegeren.tpsPersonDokument.Person;
import no.nav.registre.hodejegeren.tpsPersonDokument.Relasjon;

/**
 * <p>Java class for tpsPersonDokumentType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tpsPersonDokument">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dokument" type="{http://www.rtv.no/NamespaceTPS}dokument"/>
 *         &lt;element name="person" type="{http://www.rtv.no/NamespaceTPS}person"/>
 *         &lt;element name="relasjon" type="{http://www.rtv.no/NamespaceTPS}relasjon" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tpsPersonDokument", namespace = "http://www.rtv.no/NamespaceTPS", propOrder = {
        "dokument",
        "person",
        "relasjon"
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TpsPersonDokument {

    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected Dokument dokument;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS", required = true)
    protected Person person;
    @XmlElement(namespace = "http://www.rtv.no/NamespaceTPS")
    protected List<Relasjon> relasjon;

    /**
     * Gets the value of the dokument property.
     *
     * @return possible object is
     * {@link Dokument }
     */
    public Dokument getDokument() {
        return dokument;
    }

    /**
     * Sets the value of the dokument property.
     *
     * @param value allowed object is
     *              {@link Dokument }
     */
    public void setDokument(Dokument value) {
        this.dokument = value;
    }

    /**
     * Gets the value of the person property.
     *
     * @return possible object is
     * {@link Person }
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Sets the value of the person property.
     *
     * @param value allowed object is
     *              {@link Person }
     */
    public void setPerson(Person value) {
        this.person = value;
    }

    /**
     * Gets the value of the relasjon property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relasjon property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelasjon().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Relasjon }
     */
    public List<Relasjon> getRelasjon() {
        if (relasjon == null) {
            relasjon = new ArrayList<Relasjon>();
        }
        return this.relasjon;
    }

}
