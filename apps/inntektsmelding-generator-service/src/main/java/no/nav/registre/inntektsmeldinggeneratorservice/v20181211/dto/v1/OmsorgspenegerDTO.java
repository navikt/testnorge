package no.nav.registre.inntektsmeldinggeneratorservice.v20181211.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.registre.inntektsmeldinggeneratorservice.v20181211.adapter.ObjectFactory;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.DelvisFravaersListe;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.FravaersPeriodeListe;
import no.nav.registre.inntektsmeldinggeneratorservice.xml.Omsorgspenger;

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
