//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package no.nav.testnav.inntektsmeldinggeneratorservice.binding;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "FravaersPeriodeListe",
    propOrder = {"fravaerPeriode"}
)
public class FravaersPeriodeListe {
    @XmlElement(
        nillable = true
    )
    protected List<Periode> fravaerPeriode;

    public FravaersPeriodeListe() {
    }

    public FravaersPeriodeListe(List<Periode> fravaerPeriode) {
        this.fravaerPeriode = fravaerPeriode;
    }

    public List<Periode> getFravaerPeriode() {
        if (this.fravaerPeriode == null) {
            this.fravaerPeriode = new ArrayList();
        }

        return this.fravaerPeriode;
    }

    public FravaersPeriodeListe withFravaerPeriode(Periode... values) {
        if (values != null) {
            Periode[] var2 = values;
            int var3 = values.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Periode value = var2[var4];
                this.getFravaerPeriode().add(value);
            }
        }

        return this;
    }

    public FravaersPeriodeListe withFravaerPeriode(Collection<Periode> values) {
        if (values != null) {
            this.getFravaerPeriode().addAll(values);
        }

        return this;
    }
}
