package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.seres.xsd.nav.inntektsmelding_m._20181211.Avsendersystem;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;

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
