package no.nav.testnav.libs.domain.dto.namespacetps;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "utlandbankType",
        propOrder = { "utlandbankGironummer", "utlandbankSwiftKode", "utlandbankIban", "utlandbankBankKode", "utlandbankBanknavn", "utlandbankAdresse1", "utlandbankAdresse2", "utlandbankAdresse3", "utlandbankValuta",
                "utlandbankLand", "utlandbankFraDato", "utlandbankTilDato", "utlandbankKilde", "utlandbankRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class UtlandbankType {

    @XmlElement(
            required = true
    )
    protected String utlandbankGironummer;
    @XmlElement(
            required = true
    )
    protected String utlandbankSwiftKode;
    @XmlElement(
            required = true
    )
    protected String utlandbankIban;
    @XmlElement(
            required = true
    )
    protected String utlandbankBankKode;
    @XmlElement(
            required = true
    )
    protected String utlandbankBanknavn;
    @XmlElement(
            required = true
    )
    protected String utlandbankAdresse1;
    @XmlElement(
            required = true
    )
    protected String utlandbankAdresse2;
    @XmlElement(
            required = true
    )
    protected String utlandbankAdresse3;
    @XmlElement(
            required = true
    )
    protected String utlandbankValuta;
    @XmlElement(
            required = true
    )
    protected String utlandbankLand;
    @XmlElement(
            required = true
    )
    protected String utlandbankFraDato;
    @XmlElement(
            required = true
    )
    protected String utlandbankTilDato;
    @XmlElement(
            required = true
    )
    protected String utlandbankKilde;
    @XmlElement(
            required = true
    )
    protected String utlandbankRegistrertAv;

    public UtlandbankType withUtlandbankGironummer(String value) {
        this.setUtlandbankGironummer(value);
        return this;
    }

    public UtlandbankType withUtlandbankSwiftKode(String value) {
        this.setUtlandbankSwiftKode(value);
        return this;
    }

    public UtlandbankType withUtlandbankIban(String value) {
        this.setUtlandbankIban(value);
        return this;
    }

    public UtlandbankType withUtlandbankBankKode(String value) {
        this.setUtlandbankBankKode(value);
        return this;
    }

    public UtlandbankType withUtlandbankBanknavn(String value) {
        this.setUtlandbankBanknavn(value);
        return this;
    }

    public UtlandbankType withUtlandbankAdresse1(String value) {
        this.setUtlandbankAdresse1(value);
        return this;
    }

    public UtlandbankType withUtlandbankAdresse2(String value) {
        this.setUtlandbankAdresse2(value);
        return this;
    }

    public UtlandbankType withUtlandbankAdresse3(String value) {
        this.setUtlandbankAdresse3(value);
        return this;
    }

    public UtlandbankType withUtlandbankValuta(String value) {
        this.setUtlandbankValuta(value);
        return this;
    }

    public UtlandbankType withUtlandbankLand(String value) {
        this.setUtlandbankLand(value);
        return this;
    }

    public UtlandbankType withUtlandbankFraDato(String value) {
        this.setUtlandbankFraDato(value);
        return this;
    }

    public UtlandbankType withUtlandbankTilDato(String value) {
        this.setUtlandbankTilDato(value);
        return this;
    }

    public UtlandbankType withUtlandbankKilde(String value) {
        this.setUtlandbankKilde(value);
        return this;
    }

    public UtlandbankType withUtlandbankRegistrertAv(String value) {
        this.setUtlandbankRegistrertAv(value);
        return this;
    }
}
