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
    name = "AvtaltFerieListe",
    propOrder = {"avtaltFerie"}
)
public class AvtaltFerieListe {
    @XmlElement(
        nillable = true
    )
    protected List<Periode> avtaltFerie;

    public AvtaltFerieListe() {
    }

    public AvtaltFerieListe(List<Periode> avtaltFerie) {
        this.avtaltFerie = avtaltFerie;
    }

    public List<Periode> getAvtaltFerie() {
        if (this.avtaltFerie == null) {
            this.avtaltFerie = new ArrayList();
        }

        return this.avtaltFerie;
    }

    public AvtaltFerieListe withAvtaltFerie(Periode... values) {
        if (values != null) {
            Periode[] var2 = values;
            int var3 = values.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Periode value = var2[var4];
                this.getAvtaltFerie().add(value);
            }
        }

        return this;
    }

    public AvtaltFerieListe withAvtaltFerie(Collection<Periode> values) {
        if (values != null) {
            this.getAvtaltFerie().addAll(values);
        }

        return this;
    }
}
