package no.nav.registre.inntektsmeldinggeneratorservice.provider;

import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLInntektsmeldingM;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLSkjemainnhold;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.namespace.QName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;


@XmlRootElement(name = "melding")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({XMLInntektsmeldingM.class, LocalDate.class, LocalDateTime.class})
public class Melding extends XMLInntektsmeldingM {

    public Melding() {
    }

    public Melding(XMLSkjemainnhold skjemainnhold, Map<QName, String> otherAttributes) {
        super(skjemainnhold, otherAttributes);
    }
}
