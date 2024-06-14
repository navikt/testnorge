package no.nav.testnav.inntektsmeldinggeneratorservice.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.inntektsmeldinggeneratorservice.provider.adapter.ObjectFactory;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Kontaktinformasjon;

@Data
@NoArgsConstructor(force = true)
public class KontaktinformasjonDTO implements ToXmlElement<Kontaktinformasjon> {
    @JsonProperty(required = true)
    private String kontaktinformasjonNavn;
    @JsonProperty(required = true)
    @Size(min = 8, max = 8)
    private String telefonnummer;


    @Override
    public Kontaktinformasjon toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        Kontaktinformasjon xmlKontaktinformasjon = factory.createKontaktinformasjon();
        xmlKontaktinformasjon.setKontaktinformasjonNavn(kontaktinformasjonNavn);
        xmlKontaktinformasjon.setTelefonnummer(telefonnummer);

        return xmlKontaktinformasjon;
    }
}
