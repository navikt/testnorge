package no.nav.testnav.inntektsmeldinggeneratorservice.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.ArbeidsgiverperiodeListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.provider.adapter.ObjectFactory;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.SykepengerIArbeidsgiverperioden;
import org.apache.commons.text.CaseUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Data
@NoArgsConstructor(force = true)
public class SykepengerIArbeidsgiverperiodenDTO implements ToXmlElement<SykepengerIArbeidsgiverperioden> {
    @JsonProperty
    private List<PeriodeDTO> arbeidsgiverperiodeListe;
    @JsonProperty
    private Double bruttoUtbetalt;
    @JsonProperty
    private String begrunnelseForReduksjonEllerIkkeUtbetalt;

    @Override
    public SykepengerIArbeidsgiverperioden toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        SykepengerIArbeidsgiverperioden xmlSykepengerIArbeidsgiverperioden = factory.createSykepengerIArbeidsgiverperioden();

        if (arbeidsgiverperiodeListe != null) {
            ArbeidsgiverperiodeListe xmlArbeidsgiverperiodeListe = factory.createArbeidsgiverperiodeListe();
            xmlArbeidsgiverperiodeListe.withArbeidsgiverperiode(PeriodeDTO.convert(arbeidsgiverperiodeListe));
            xmlSykepengerIArbeidsgiverperioden.setArbeidsgiverperiodeListe(
                    factory.createSykepengerIArbeidsgiverperiodenArbeidsgiverperiodeListe(xmlArbeidsgiverperiodeListe)
            );
        }


        xmlSykepengerIArbeidsgiverperioden.setBruttoUtbetalt(factory.createSykepengerIArbeidsgiverperiodenBruttoUtbetalt(
                bruttoUtbetalt != null ? BigDecimal.valueOf(bruttoUtbetalt) : null
        ));
        if (isBlank(begrunnelseForReduksjonEllerIkkeUtbetalt))
            xmlSykepengerIArbeidsgiverperioden.setBegrunnelseForReduksjonEllerIkkeUtbetalt(
                    factory.createSykepengerIArbeidsgiverperiodenBegrunnelseForReduksjonEllerIkkeUtbetalt(
                            null
                    )
            );
        else xmlSykepengerIArbeidsgiverperioden.setBegrunnelseForReduksjonEllerIkkeUtbetalt(
                factory.createSykepengerIArbeidsgiverperiodenBegrunnelseForReduksjonEllerIkkeUtbetalt(
                        begrunnelseForReduksjonEllerIkkeUtbetalt.contains("_") ? CaseUtils.toCamelCase(begrunnelseForReduksjonEllerIkkeUtbetalt, true, '_') : begrunnelseForReduksjonEllerIkkeUtbetalt
                )
        );

        return xmlSykepengerIArbeidsgiverperioden;
    }
}
