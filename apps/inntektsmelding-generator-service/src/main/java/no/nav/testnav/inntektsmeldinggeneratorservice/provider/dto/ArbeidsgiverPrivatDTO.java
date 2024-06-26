package no.nav.testnav.inntektsmeldinggeneratorservice.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.ArbeidsgiverPrivat;
import no.nav.testnav.inntektsmeldinggeneratorservice.provider.adapter.ObjectFactory;

@Data
@NoArgsConstructor(force = true)
public class ArbeidsgiverPrivatDTO implements ToXmlElement<ArbeidsgiverPrivat> {

    @JsonProperty(required = true)
    @Size(min = 11, max = 11)
    private String arbeidsgiverFnr;
    @JsonProperty(required = true)
    private KontaktinformasjonDTO kontaktinformasjon;

    @Override
    public ArbeidsgiverPrivat toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        ArbeidsgiverPrivat xmlArbeidsgiverPrivat = factory.createArbeidsgiverPrivat();
        xmlArbeidsgiverPrivat.setArbeidsgiverFnr(arbeidsgiverFnr);
        if (kontaktinformasjon != null) {
            xmlArbeidsgiverPrivat.setKontaktinformasjon(kontaktinformasjon.toXmlElement());
        }
        return xmlArbeidsgiverPrivat;
    }
}
