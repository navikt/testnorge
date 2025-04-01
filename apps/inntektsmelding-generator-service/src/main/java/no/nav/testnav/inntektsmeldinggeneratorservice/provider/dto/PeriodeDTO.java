package no.nav.testnav.inntektsmeldinggeneratorservice.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Periode;
import no.nav.testnav.inntektsmeldinggeneratorservice.provider.adapter.ObjectFactory;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class PeriodeDTO implements ToXmlElement<Periode> {
    @JsonProperty
    private String fom;
    @JsonProperty
    private String tom;

    @Override
    public Periode toXmlElement() {
        ObjectFactory factory = new ObjectFactory();
        Periode xmlPeriode = factory.createPeriode();
        xmlPeriode.setFom(factory.createPeriodeFom(this.fom));
        xmlPeriode.setTom(factory.createPeriodeTom(this.tom));
        return xmlPeriode;
    }

    static List<Periode> convert(List<PeriodeDTO> list) {
        return list.stream().map(PeriodeDTO::toXmlElement)
                .toList();
    }
}