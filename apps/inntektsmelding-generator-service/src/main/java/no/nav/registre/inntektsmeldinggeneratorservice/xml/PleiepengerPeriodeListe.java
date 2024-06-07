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
    name = "PleiepengerPeriodeListe",
    propOrder = {"periode"}
)
public class PleiepengerPeriodeListe {
    @XmlElement(
        nillable = true
    )
    protected List<Periode> periode;

    public PleiepengerPeriodeListe() {
    }

    public PleiepengerPeriodeListe(List<Periode> periode) {
        this.periode = periode;
    }

    public List<Periode> getPeriode() {
        if (this.periode == null) {
            this.periode = new ArrayList();
        }

        return this.periode;
    }

    public PleiepengerPeriodeListe withPeriode(Periode... values) {
        if (values != null) {
            Periode[] var2 = values;
            int var3 = values.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Periode value = var2[var4];
                this.getPeriode().add(value);
            }
        }

        return this;
    }

    public PleiepengerPeriodeListe withPeriode(Collection<Periode> values) {
        if (values != null) {
            this.getPeriode().addAll(values);
        }

        return this;
    }
}
