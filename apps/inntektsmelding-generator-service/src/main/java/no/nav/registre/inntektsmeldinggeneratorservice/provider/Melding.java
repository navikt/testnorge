package no.nav.registre.inntektsmeldinggeneratorservice.provider;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import no.seres.xsd.nav.inntektsmelding_m._20181211.InntektsmeldingM;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Skjemainnhold;

import javax.xml.namespace.QName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;


@XmlRootElement(name = "melding")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ InntektsmeldingM.class, LocalDate.class, LocalDateTime.class })
public class Melding extends InntektsmeldingM {

    public Melding() {
    }

    public Melding(Skjemainnhold skjemainnhold, Map<QName, String> otherAttributes) {
        super(skjemainnhold, otherAttributes);
    }
}
