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
    name = "DelvisFravaersListe",
    propOrder = {"delvisFravaer"}
)
public class DelvisFravaersListe {
    @XmlElement(
        nillable = true
    )
    protected List<DelvisFravaer> delvisFravaer;

    public DelvisFravaersListe() {
    }

    public DelvisFravaersListe(List<DelvisFravaer> delvisFravaer) {
        this.delvisFravaer = delvisFravaer;
    }

    public List<DelvisFravaer> getDelvisFravaer() {
        if (this.delvisFravaer == null) {
            this.delvisFravaer = new ArrayList();
        }

        return this.delvisFravaer;
    }

    public DelvisFravaersListe withDelvisFravaer(DelvisFravaer... values) {
        if (values != null) {
            DelvisFravaer[] var2 = values;
            int var3 = values.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                DelvisFravaer value = var2[var4];
                this.getDelvisFravaer().add(value);
            }
        }

        return this;
    }

    public DelvisFravaersListe withDelvisFravaer(Collection<DelvisFravaer> values) {
        if (values != null) {
            this.getDelvisFravaer().addAll(values);
        }

        return this;
    }
}
