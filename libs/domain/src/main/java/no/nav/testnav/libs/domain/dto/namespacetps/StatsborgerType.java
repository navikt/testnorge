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
        name = "statsborgerType",
        propOrder = { "statsborger", "statsborgerFraDato", "statsborgerTilDato", "statsborgerKilde", "statsborgerRegistrertAv" }
)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class StatsborgerType {

    @XmlElement(
            required = true
    )
    protected String statsborger;
    @XmlElement(
            required = true
    )
    protected String statsborgerFraDato;
    @XmlElement(
            required = true
    )
    protected String statsborgerTilDato;
    @XmlElement(
            required = true
    )
    protected String statsborgerKilde;
    @XmlElement(
            required = true
    )
    protected String statsborgerRegistrertAv;

    public StatsborgerType withStatsborger(String value) {
        this.setStatsborger(value);
        return this;
    }

    public StatsborgerType withStatsborgerFraDato(String value) {
        this.setStatsborgerFraDato(value);
        return this;
    }

    public StatsborgerType withStatsborgerTilDato(String value) {
        this.setStatsborgerTilDato(value);
        return this;
    }

    public StatsborgerType withStatsborgerKilde(String value) {
        this.setStatsborgerKilde(value);
        return this;
    }

    public StatsborgerType withStatsborgerRegistrertAv(String value) {
        this.setStatsborgerRegistrertAv(value);
        return this;
    }
}
