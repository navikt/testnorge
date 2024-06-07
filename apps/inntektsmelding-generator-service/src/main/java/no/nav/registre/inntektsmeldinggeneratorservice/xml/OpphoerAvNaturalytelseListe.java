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
    name = "OpphoerAvNaturalytelseListe",
    propOrder = {"opphoerAvNaturalytelse"}
)
public class OpphoerAvNaturalytelseListe {
    @XmlElement(
        nillable = true
    )
    protected List<NaturalytelseDetaljer> opphoerAvNaturalytelse;

    public OpphoerAvNaturalytelseListe() {
    }

    public OpphoerAvNaturalytelseListe(List<NaturalytelseDetaljer> opphoerAvNaturalytelse) {
        this.opphoerAvNaturalytelse = opphoerAvNaturalytelse;
    }

    public List<NaturalytelseDetaljer> getOpphoerAvNaturalytelse() {
        if (this.opphoerAvNaturalytelse == null) {
            this.opphoerAvNaturalytelse = new ArrayList();
        }

        return this.opphoerAvNaturalytelse;
    }

    public OpphoerAvNaturalytelseListe withOpphoerAvNaturalytelse(NaturalytelseDetaljer... values) {
        if (values != null) {
            NaturalytelseDetaljer[] var2 = values;
            int var3 = values.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                NaturalytelseDetaljer value = var2[var4];
                this.getOpphoerAvNaturalytelse().add(value);
            }
        }

        return this;
    }

    public OpphoerAvNaturalytelseListe withOpphoerAvNaturalytelse(Collection<NaturalytelseDetaljer> values) {
        if (values != null) {
            this.getOpphoerAvNaturalytelse().addAll(values);
        }

        return this;
    }
}
