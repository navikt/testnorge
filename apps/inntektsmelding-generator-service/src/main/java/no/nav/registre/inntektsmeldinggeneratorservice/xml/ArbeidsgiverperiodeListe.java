//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package no.nav.registre.inntektsmeldinggeneratorservice.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "ArbeidsgiverperiodeListe",
    propOrder = {"arbeidsgiverperiode"}
)
public class ArbeidsgiverperiodeListe {
    @XmlElement(
        required = true
    )
    protected List<Periode> arbeidsgiverperiode;

    public ArbeidsgiverperiodeListe() {
    }

    public ArbeidsgiverperiodeListe(List<Periode> arbeidsgiverperiode) {
        this.arbeidsgiverperiode = arbeidsgiverperiode;
    }

    public List<Periode> getArbeidsgiverperiode() {
        if (this.arbeidsgiverperiode == null) {
            this.arbeidsgiverperiode = new ArrayList();
        }

        return this.arbeidsgiverperiode;
    }

    public ArbeidsgiverperiodeListe withArbeidsgiverperiode(Periode... values) {
        if (values != null) {
            Periode[] var2 = values;
            int var3 = values.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Periode value = var2[var4];
                this.getArbeidsgiverperiode().add(value);
            }
        }

        return this;
    }

    public ArbeidsgiverperiodeListe withArbeidsgiverperiode(Collection<Periode> values) {
        if (values != null) {
            this.getArbeidsgiverperiode().addAll(values);
        }

        return this;
    }
}
