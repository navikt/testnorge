package no.nav.testnav.libs.domain.dto.namespacetps;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "telefonType",
        propOrder = { "tlfPrivat", "tlfJobb", "tlfMobil" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class TelefonType {

    protected TelefonPrivatType tlfPrivat;
    protected TelefonJobbType tlfJobb;
    protected TelefonMobilType tlfMobil;

    public TelefonType withTlfPrivat(TelefonPrivatType value) {
        this.setTlfPrivat(value);
        return this;
    }

    public TelefonType withTlfJobb(TelefonJobbType value) {
        this.setTlfJobb(value);
        return this;
    }

    public TelefonType withTlfMobil(TelefonMobilType value) {
        this.setTlfMobil(value);
        return this;
    }
}
