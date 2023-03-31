package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.seres.xsd.nav.inntektsmelding_m._20181211.ObjectFactory;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLArbeidsgiverperiodeListe;
import no.seres.xsd.nav.inntektsmelding_m._20181211.XMLSykepengerIArbeidsgiverperioden;
import org.apache.commons.text.CaseUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Value
@NoArgsConstructor(force = true)
public class SykepengerIArbeidsgiverperiodenDTO implements ToXmlElement<XMLSykepengerIArbeidsgiverperioden> {
    @JsonProperty
    private List<PeriodeDTO> arbeidsgiverperiodeListe;
    @JsonProperty
    private Double bruttoUtbetalt;
    @JsonProperty
    private String begrunnelseForReduksjonEllerIkkeUtbetalt;

    @Override
    public XMLSykepengerIArbeidsgiverperioden toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        XMLSykepengerIArbeidsgiverperioden xmlSykepengerIArbeidsgiverperioden = factory.createXMLSykepengerIArbeidsgiverperioden();

        if (arbeidsgiverperiodeListe != null) {
            XMLArbeidsgiverperiodeListe xmlArbeidsgiverperiodeListe = factory.createXMLArbeidsgiverperiodeListe();
            xmlArbeidsgiverperiodeListe.withArbeidsgiverperiode(PeriodeDTO.convert(arbeidsgiverperiodeListe));
            xmlSykepengerIArbeidsgiverperioden.setArbeidsgiverperiodeListe(
                    factory.createXMLSykepengerIArbeidsgiverperiodenArbeidsgiverperiodeListe(xmlArbeidsgiverperiodeListe)
            );
        }


        xmlSykepengerIArbeidsgiverperioden.setBruttoUtbetalt(factory.createXMLSykepengerIArbeidsgiverperiodenBruttoUtbetalt(
                bruttoUtbetalt != null ? BigDecimal.valueOf(bruttoUtbetalt) : null
        ));
        if (isBlank(begrunnelseForReduksjonEllerIkkeUtbetalt))
            xmlSykepengerIArbeidsgiverperioden.setBegrunnelseForReduksjonEllerIkkeUtbetalt(
                    factory.createXMLSykepengerIArbeidsgiverperiodenBegrunnelseForReduksjonEllerIkkeUtbetalt(
                            null
                    )
            );
        else xmlSykepengerIArbeidsgiverperioden.setBegrunnelseForReduksjonEllerIkkeUtbetalt(
                factory.createXMLSykepengerIArbeidsgiverperiodenBegrunnelseForReduksjonEllerIkkeUtbetalt(
                        begrunnelseForReduksjonEllerIkkeUtbetalt.contains("_") ? CaseUtils.toCamelCase(begrunnelseForReduksjonEllerIkkeUtbetalt, true, '_') : begrunnelseForReduksjonEllerIkkeUtbetalt
                )
        );

        return xmlSykepengerIArbeidsgiverperioden;
    }
}
