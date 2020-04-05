package no.nav.registre.inntektsmeldingstub.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLArbeidsgiverperiodeListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLSykepengerIArbeidsgiverperioden;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Value
@NoArgsConstructor(force = true)
public class SykepengerIArbeidsgiverperiodenDTO implements ToXmlElement<XMLSykepengerIArbeidsgiverperioden> {
    @JsonProperty
    private List<PeriodeDTO> arbeidsgiverperiodeListe = new ArrayList<>();
    @JsonProperty
    private Double bruttoUtbetalt;
    @JsonProperty
    private String begrunnelseForReduksjonEllerIkkeUtbetalt;

    @Override
    public XMLSykepengerIArbeidsgiverperioden toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        XMLArbeidsgiverperiodeListe xmlArbeidsgiverperiodeListe = factory.createXMLArbeidsgiverperiodeListe();
        xmlArbeidsgiverperiodeListe.withArbeidsgiverperiode(PeriodeDTO.convert(arbeidsgiverperiodeListe));

        XMLSykepengerIArbeidsgiverperioden xmlSykepengerIArbeidsgiverperioden = factory.createXMLSykepengerIArbeidsgiverperioden();
        xmlSykepengerIArbeidsgiverperioden.setArbeidsgiverperiodeListe(
                factory.createXMLSykepengerIArbeidsgiverperiodenArbeidsgiverperiodeListe(xmlArbeidsgiverperiodeListe)
        );
        xmlSykepengerIArbeidsgiverperioden.setBruttoUtbetalt(factory.createXMLSykepengerIArbeidsgiverperiodenBruttoUtbetalt(
                bruttoUtbetalt != null ? BigDecimal.valueOf(bruttoUtbetalt) : null
        ));
        xmlSykepengerIArbeidsgiverperioden.setBegrunnelseForReduksjonEllerIkkeUtbetalt(
                factory.createXMLSykepengerIArbeidsgiverperiodenBegrunnelseForReduksjonEllerIkkeUtbetalt(
                        begrunnelseForReduksjonEllerIkkeUtbetalt
                )
        );

        return xmlSykepengerIArbeidsgiverperioden;
    }
}
