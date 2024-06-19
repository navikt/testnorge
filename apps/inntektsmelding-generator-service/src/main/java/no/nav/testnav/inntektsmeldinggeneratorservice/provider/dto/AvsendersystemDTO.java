package no.nav.testnav.inntektsmeldinggeneratorservice.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.inntektsmeldinggeneratorservice.provider.adapter.ObjectFactory;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Avsendersystem;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor(force = true)
public class AvsendersystemDTO implements ToXmlElement<Avsendersystem> {
    @JsonProperty(required = true)
    private String systemnavn;
    @JsonProperty(required = true)
    private String systemversjon;
    @JsonProperty
    private LocalDateTime innsendingstidspunkt;

    @Override
    public Avsendersystem toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        Avsendersystem xmlAvsendersystem = factory.createAvsendersystem();
        xmlAvsendersystem.setInnsendingstidspunkt(factory.createAvsendersystemInnsendingstidspunkt(
                innsendingstidspunkt
        ));
        xmlAvsendersystem.setSystemnavn(systemnavn);
        xmlAvsendersystem.setSystemversjon(systemversjon);

        return xmlAvsendersystem;
    }
}
