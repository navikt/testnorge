package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.registre.inntektsmeldinggeneratorservice.v20181211.adapter.ObjectFactory;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.Periode;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class PeriodeDTO implements ToXmlElement<Periode> {
    @JsonProperty
    private LocalDate fom;
    @JsonProperty
    private LocalDate tom;

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