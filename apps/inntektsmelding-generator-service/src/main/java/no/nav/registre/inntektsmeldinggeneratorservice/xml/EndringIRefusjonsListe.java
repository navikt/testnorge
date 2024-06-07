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
    name = "EndringIRefusjonsListe",
    propOrder = {"endringIRefusjon"}
)
public class EndringIRefusjonsListe {
    @XmlElement(
        nillable = true
    )
    protected List<EndringIRefusjon> endringIRefusjon;

    public EndringIRefusjonsListe() {
    }

    public EndringIRefusjonsListe(List<EndringIRefusjon> endringIRefusjon) {
        this.endringIRefusjon = endringIRefusjon;
    }

    public List<EndringIRefusjon> getEndringIRefusjon() {
        if (this.endringIRefusjon == null) {
            this.endringIRefusjon = new ArrayList();
        }

        return this.endringIRefusjon;
    }

    public EndringIRefusjonsListe withEndringIRefusjon(EndringIRefusjon... values) {
        if (values != null) {
            EndringIRefusjon[] var2 = values;
            int var3 = values.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                EndringIRefusjon value = var2[var4];
                this.getEndringIRefusjon().add(value);
            }
        }

        return this;
    }

    public EndringIRefusjonsListe withEndringIRefusjon(Collection<EndringIRefusjon> values) {
        if (values != null) {
            this.getEndringIRefusjon().addAll(values);
        }

        return this;
    }
}
