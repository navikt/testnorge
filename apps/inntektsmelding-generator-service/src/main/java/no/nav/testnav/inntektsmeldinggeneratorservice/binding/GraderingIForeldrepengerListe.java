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
    name = "GraderingIForeldrepengerListe",
    propOrder = {"graderingIForeldrepenger"}
)
public class GraderingIForeldrepengerListe {
    @XmlElement(
        nillable = true
    )
    protected List<GraderingIForeldrepenger> graderingIForeldrepenger;

    public GraderingIForeldrepengerListe() {
    }

    public GraderingIForeldrepengerListe(List<GraderingIForeldrepenger> graderingIForeldrepenger) {
        this.graderingIForeldrepenger = graderingIForeldrepenger;
    }

    public List<GraderingIForeldrepenger> getGraderingIForeldrepenger() {
        if (this.graderingIForeldrepenger == null) {
            this.graderingIForeldrepenger = new ArrayList();
        }

        return this.graderingIForeldrepenger;
    }

    public GraderingIForeldrepengerListe withGraderingIForeldrepenger(GraderingIForeldrepenger... values) {
        if (values != null) {
            GraderingIForeldrepenger[] var2 = values;
            int var3 = values.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                GraderingIForeldrepenger value = var2[var4];
                this.getGraderingIForeldrepenger().add(value);
            }
        }

        return this;
    }

    public GraderingIForeldrepengerListe withGraderingIForeldrepenger(Collection<GraderingIForeldrepenger> values) {
        if (values != null) {
            this.getGraderingIForeldrepenger().addAll(values);
        }

        return this;
    }
}
