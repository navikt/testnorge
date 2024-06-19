package no.nav.testnav.inntektsmeldinggeneratorservice.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Arbeidsgiver;
import no.nav.testnav.inntektsmeldinggeneratorservice.provider.adapter.ObjectFactory;


@Data
@NoArgsConstructor(force = true)
public class ArbeidsgiverDTO implements ToXmlElement<Arbeidsgiver> {
    @JsonProperty(required = true)
    @Size(min = 9, max = 9)
    private String virksomhetsnummer;
    @JsonProperty(required = true)
    private KontaktinformasjonDTO kontaktinformasjon;

    @Override
    public Arbeidsgiver toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        Arbeidsgiver xmlArbeidsgiver = factory.createArbeidsgiver();
        xmlArbeidsgiver.setVirksomhetsnummer(virksomhetsnummer);
        if (kontaktinformasjon != null) {
            xmlArbeidsgiver.setKontaktinformasjon(kontaktinformasjon.toXmlElement());
        }
        return xmlArbeidsgiver;
    }
}
