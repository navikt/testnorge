package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLArbeidsgiverPrivat;

@Data
@NoArgsConstructor(force = true)
public class ArbeidsgiverPrivatDTO implements ToXmlElement<XMLArbeidsgiverPrivat> {

    @JsonProperty(required = true)
    @Size(min = 11, max = 11)
    private String arbeidsgiverFnr;
    @JsonProperty(required = true)
    private KontaktinformasjonDTO kontaktinformasjon;

    @Override
    public XMLArbeidsgiverPrivat toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        XMLArbeidsgiverPrivat xmlArbeidsgiverPrivat = factory.createXMLArbeidsgiverPrivat();
        xmlArbeidsgiverPrivat.setArbeidsgiverFnr(arbeidsgiverFnr);
        if (kontaktinformasjon != null) {
            xmlArbeidsgiverPrivat.setKontaktinformasjon(kontaktinformasjon.toXmlElement());
        }
        return xmlArbeidsgiverPrivat;
    }
}
