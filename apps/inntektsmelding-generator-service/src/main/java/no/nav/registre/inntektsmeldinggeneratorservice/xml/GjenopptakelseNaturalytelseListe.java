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
    name = "GjenopptakelseNaturalytelseListe",
    propOrder = {"naturalytelseDetaljer"}
)
public class GjenopptakelseNaturalytelseListe {
    @XmlElement(
        nillable = true
    )
    protected List<NaturalytelseDetaljer> naturalytelseDetaljer;

    public GjenopptakelseNaturalytelseListe() {
    }

    public GjenopptakelseNaturalytelseListe(List<NaturalytelseDetaljer> naturalytelseDetaljer) {
        this.naturalytelseDetaljer = naturalytelseDetaljer;
    }

    public List<NaturalytelseDetaljer> getNaturalytelseDetaljer() {
        if (this.naturalytelseDetaljer == null) {
            this.naturalytelseDetaljer = new ArrayList();
        }

        return this.naturalytelseDetaljer;
    }

    public GjenopptakelseNaturalytelseListe withNaturalytelseDetaljer(NaturalytelseDetaljer... values) {
        if (values != null) {
            NaturalytelseDetaljer[] var2 = values;
            int var3 = values.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                NaturalytelseDetaljer value = var2[var4];
                this.getNaturalytelseDetaljer().add(value);
            }
        }

        return this;
    }

    public GjenopptakelseNaturalytelseListe withNaturalytelseDetaljer(Collection<NaturalytelseDetaljer> values) {
        if (values != null) {
            this.getNaturalytelseDetaljer().addAll(values);
        }

        return this;
    }
}
