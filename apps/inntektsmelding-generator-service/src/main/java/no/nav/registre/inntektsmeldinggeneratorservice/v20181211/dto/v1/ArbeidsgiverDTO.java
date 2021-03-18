package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLArbeidsgiver;

import javax.validation.constraints.Size;

@Value
@NoArgsConstructor(force = true)
public class ArbeidsgiverDTO implements ToXmlElement<XMLArbeidsgiver> {
    @JsonProperty(required = true)
    @Size(min = 9, max = 9)
    private String virksomhetsnummer;
    @JsonProperty(required = true)
    private KontaktinformasjonDTO kontaktinformasjon;

    @Override
    public XMLArbeidsgiver toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        XMLArbeidsgiver xmlArbeidsgiver = factory.createXMLArbeidsgiver();
        xmlArbeidsgiver.setVirksomhetsnummer(virksomhetsnummer);
        if (kontaktinformasjon != null) {
            xmlArbeidsgiver.setKontaktinformasjon(kontaktinformasjon.toXmlElement());
        }
        return xmlArbeidsgiver;
    }
}
