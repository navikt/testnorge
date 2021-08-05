package no.nav.testnav.libs.domain.dto.namespacetps;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "tpsPersonDokumentType",
        propOrder = { "dokument", "person", "relasjon" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TpsPersonDokumentType {

    @XmlElement(
            required = true
    )
    protected DokumentType dokument;
    @XmlElement(
            required = true
    )
    protected PersonType person;
    @JacksonXmlElementWrapper(
            useWrapping = false
    )
    protected List<RelasjonType> relasjon;

    public TpsPersonDokumentType withDokument(DokumentType value) {
        this.setDokument(value);
        return this;
    }

    public TpsPersonDokumentType withPerson(PersonType value) {
        this.setPerson(value);
        return this;
    }

    public TpsPersonDokumentType withRelasjon(RelasjonType... values) {
        if (values != null) {
            RelasjonType[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                RelasjonType value = var2[var4];
                this.getRelasjon().add(value);
            }
        }

        return this;
    }

    public TpsPersonDokumentType withRelasjon(Collection<RelasjonType> values) {
        if (values != null) {
            this.getRelasjon().addAll(values);
        }

        return this;
    }
}
