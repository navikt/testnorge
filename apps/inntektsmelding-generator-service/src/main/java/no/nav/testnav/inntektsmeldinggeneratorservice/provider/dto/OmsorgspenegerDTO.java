package no.nav.testnav.inntektsmeldinggeneratorservice.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.Omsorgspenger;
import no.nav.testnav.inntektsmeldinggeneratorservice.provider.adapter.ObjectFactory;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.DelvisFravaersListe;
import no.nav.testnav.inntektsmeldinggeneratorservice.binding.FravaersPeriodeListe;

import java.util.List;

@Data
@NoArgsConstructor(force = true)
public class OmsorgspenegerDTO implements ToXmlElement<Omsorgspenger> {

    @JsonProperty
    private Boolean harUtbetaltPliktigeDager;
    @JsonProperty
    private List<PeriodeDTO> fravaersPerioder;
    @JsonProperty
    private List<DelvisFravearDTO> delvisFravaersListe;


    @Override
    public Omsorgspenger toXmlElement() {
        ObjectFactory factory = new ObjectFactory();

        Omsorgspenger xmlOmsorgspenger = factory.createOmsorgspenger();
        if (delvisFravaersListe != null) {
            DelvisFravaersListe xmlDelvisFravaersListe = factory.createDelvisFravaersListe();
            xmlDelvisFravaersListe.withDelvisFravaer(DelvisFravearDTO.convert(delvisFravaersListe));
            xmlOmsorgspenger.setDelvisFravaersListe(factory.createOmsorgspengerDelvisFravaersListe(xmlDelvisFravaersListe));
        }

        if (fravaersPerioder != null) {
            FravaersPeriodeListe xmlFravaersPeriodeListe = factory.createFravaersPeriodeListe();
            xmlFravaersPeriodeListe.withFravaerPeriode(PeriodeDTO.convert(fravaersPerioder));
            xmlOmsorgspenger.setFravaersPerioder(factory.createOmsorgspengerFravaersPerioder(xmlFravaersPeriodeListe));
        }

        xmlOmsorgspenger.setHarUtbetaltPliktigeDager(factory.createOmsorgspengerHarUtbetaltPliktigeDager(
                harUtbetaltPliktigeDager
        ));
        return xmlOmsorgspenger;
    }
}
