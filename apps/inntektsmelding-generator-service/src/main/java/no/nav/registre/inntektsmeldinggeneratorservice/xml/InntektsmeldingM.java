//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package no.nav.registre.inntektsmeldinggeneratorservice.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "Inntektsmelding_M",
    propOrder = {"skjemainnhold"}
)
public class InntektsmeldingM {
    @Setter
    @XmlElement(
        name = "Skjemainnhold",
        required = true
    )
    protected Skjemainnhold skjemainnhold;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<>();

    public InntektsmeldingM() {
    }

    public InntektsmeldingM(Skjemainnhold skjemainnhold, Map<QName, String> otherAttributes) {
        this.skjemainnhold = skjemainnhold;
        this.otherAttributes = otherAttributes;
    }

    public InntektsmeldingM withSkjemainnhold(Skjemainnhold value) {
        this.setSkjemainnhold(value);
        return this;
    }
}
