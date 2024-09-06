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
        name = "UtsettelseAvForeldrepengerListe",
        propOrder = { "utsettelseAvForeldrepenger" }
)
public class UtsettelseAvForeldrepengerListe {
    @XmlElement(
            nillable = true
    )
    protected List<UtsettelseAvForeldrepenger> utsettelseAvForeldrepenger;

    public UtsettelseAvForeldrepengerListe() {
    }

    public UtsettelseAvForeldrepengerListe(List<UtsettelseAvForeldrepenger> utsettelseAvForeldrepenger) {
        this.utsettelseAvForeldrepenger = utsettelseAvForeldrepenger;
    }

    public List<UtsettelseAvForeldrepenger> getUtsettelseAvForeldrepenger() {
        if (this.utsettelseAvForeldrepenger == null) {
            this.utsettelseAvForeldrepenger = new ArrayList();
        }

        return this.utsettelseAvForeldrepenger;
    }

    public UtsettelseAvForeldrepengerListe withUtsettelseAvForeldrepenger(UtsettelseAvForeldrepenger... values) {
        if (values != null) {
            UtsettelseAvForeldrepenger[] var2 = values;
            int var3 = values.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                UtsettelseAvForeldrepenger value = var2[var4];
                this.getUtsettelseAvForeldrepenger().add(value);
            }
        }

        return this;
    }

    public UtsettelseAvForeldrepengerListe withUtsettelseAvForeldrepenger(Collection<UtsettelseAvForeldrepenger> values) {
        if (values != null) {
            this.getUtsettelseAvForeldrepenger().addAll(values);
        }

        return this;
    }
}
