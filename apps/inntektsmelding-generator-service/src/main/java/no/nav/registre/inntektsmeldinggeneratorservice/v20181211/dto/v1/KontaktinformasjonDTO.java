package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLKontaktinformasjon;

@Data
@NoArgsConstructor(force = true)
public class KontaktinformasjonDTO implements ToXmlElement<XMLKontaktinformasjon> {
    @JsonProperty(required = true)
    private String kontaktinformasjonNavn;
    @JsonProperty(required = true)
    @Size(min = 8, max = 8)
    private String telefonnummer;


    @Override
    public XMLKontaktinformasjon toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        XMLKontaktinformasjon xmlKontaktinformasjon = factory.createXMLKontaktinformasjon();
        xmlKontaktinformasjon.setKontaktinformasjonNavn(kontaktinformasjonNavn);
        xmlKontaktinformasjon.setTelefonnummer(telefonnummer);

        return xmlKontaktinformasjon;
    }
}
