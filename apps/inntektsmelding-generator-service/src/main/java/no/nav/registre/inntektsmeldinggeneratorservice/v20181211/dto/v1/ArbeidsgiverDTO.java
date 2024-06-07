package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Arbeidsgiver;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;


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
