package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLAvsendersystem;

import java.time.LocalDateTime;

@Value
@NoArgsConstructor(force = true)
public class AvsendersystemDTO implements ToXmlElement<XMLAvsendersystem> {
    @JsonProperty(required = true)
    private String systemnavn;
    @JsonProperty(required = true)
    private String systemversjon;
    @JsonProperty
    private LocalDateTime innsendingstidspunkt;

    @Override
    public XMLAvsendersystem toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        XMLAvsendersystem xmlAvsendersystem = factory.createXMLAvsendersystem();
        xmlAvsendersystem.setInnsendingstidspunkt(factory.createXMLAvsendersystemInnsendingstidspunkt(
                innsendingstidspunkt
        ));
        xmlAvsendersystem.setSystemnavn(systemnavn);
        xmlAvsendersystem.setSystemversjon(systemversjon);

        return xmlAvsendersystem;
    }
}
