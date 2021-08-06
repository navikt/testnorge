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
        name = "PostType",
        propOrder = { "postAdresse1", "postAdresse2", "postAdresse3", "postpostnr", "postLand", "postAdresseFraDato", "postAdresseTilDato", "postKilde", "postRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class PostType {

    @XmlElement(
            required = true
    )
    protected String postAdresse1;
    @XmlElement(
            required = true
    )
    protected String postAdresse2;
    @XmlElement(
            required = true
    )
    protected String postAdresse3;
    @XmlElement(
            required = true
    )
    protected String postpostnr;
    @XmlElement(
            required = true
    )
    protected String postLand;
    @XmlElement(
            required = true
    )
    protected String postAdresseFraDato;
    @XmlElement(
            required = true
    )
    protected String postAdresseTilDato;
    @XmlElement(
            required = true
    )
    protected String postKilde;
    @XmlElement(
            required = true
    )
    protected String postRegistrertAv;

    public PostType withPostAdresse1(String value) {
        this.setPostAdresse1(value);
        return this;
    }

    public PostType withPostAdresse2(String value) {
        this.setPostAdresse2(value);
        return this;
    }

    public PostType withPostAdresse3(String value) {
        this.setPostAdresse3(value);
        return this;
    }

    public PostType withPostpostnr(String value) {
        this.setPostpostnr(value);
        return this;
    }

    public PostType withPostLand(String value) {
        this.setPostLand(value);
        return this;
    }

    public PostType withPostAdresseFraDato(String value) {
        this.setPostAdresseFraDato(value);
        return this;
    }

    public PostType withPostAdresseTilDato(String value) {
        this.setPostAdresseTilDato(value);
        return this;
    }

    public PostType withPostKilde(String value) {
        this.setPostKilde(value);
        return this;
    }

    public PostType withPostRegistrertAv(String value) {
        this.setPostRegistrertAv(value);
        return this;
    }
}
