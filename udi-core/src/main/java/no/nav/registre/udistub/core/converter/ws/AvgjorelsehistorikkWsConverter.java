package no.nav.registre.udistub.core.converter.ws;

import no.udi.mt_1067_nav_data.v1.AvgjorelseListe;
import no.udi.mt_1067_nav_data.v1.Avgjorelser;
import no.udi.mt_1067_nav_data.v1.Avgjorelsestype;
import no.udi.mt_1067_nav_data.v1.Periode;
import no.udi.mt_1067_nav_data.v1.Tillatelse;
import no.udi.mt_1067_nav_data.v1.Utfall;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.registre.udistub.core.service.to.UdiAvgjorelse;
import no.nav.registre.udistub.core.service.to.UdiPerson;

@Component
public class AvgjorelsehistorikkWsConverter implements Converter<UdiPerson, Avgjorelser> {

    private final ConversionService conversionService;

    public AvgjorelsehistorikkWsConverter(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public Avgjorelser convert(UdiPerson person) {
        if (person != null) {
            Avgjorelser avgjorelser = new Avgjorelser();
            avgjorelser.setUavklart(person.getAvgjoerelseUavklart());
            avgjorelser.setAvgjorelseListe(new AvgjorelseListe());
            person.getAvgjoerelser().stream()
                    .map(this::converterPersonAvgjorelse)
                    .forEach(avgjorelser.getAvgjorelseListe().getAvgjorelse()::add);
            return avgjorelser;
        }
        return null;
    }

    private no.udi.mt_1067_nav_data.v1.Avgjorelse converterPersonAvgjorelse(UdiAvgjorelse avgjorelse) {

        no.udi.mt_1067_nav_data.v1.Avgjorelse udiAvgjorelse = new no.udi.mt_1067_nav_data.v1.Avgjorelse();
        udiAvgjorelse.setAvgjorelseDato(conversionService.convert(avgjorelse.getAvgjoerelsesDato(), XMLGregorianCalendar.class));
        Avgjorelsestype avgjorelsestype = new Avgjorelsestype();
        avgjorelsestype.setGrunntypeKode(avgjorelse.getGrunntypeKode().udiKodeverk());
        avgjorelsestype.setTillatelseKode(avgjorelse.getTillatelseKode().udiKodeverk());
        avgjorelsestype.setUtfallstypeKode(avgjorelse.getUtfallstypeKode().udiKodeverk());
        udiAvgjorelse.setAvgjorelsestype(avgjorelsestype);

        udiAvgjorelse.setEffektueringsDato(conversionService.convert(avgjorelse.getEffektueringsDato(), XMLGregorianCalendar.class));
        udiAvgjorelse.setErPositiv(avgjorelse.getErPositiv());
        udiAvgjorelse.setEtat(avgjorelse.getEtat());
        udiAvgjorelse.setFlyktingstatus(avgjorelse.getHarFlyktningstatus());
        udiAvgjorelse.setIverksettelseDato(conversionService.convert(avgjorelse.getIverksettelseDato(), XMLGregorianCalendar.class));
        udiAvgjorelse.setSaksnummer(avgjorelse.getSaksnummer());

        Tillatelse tillatelse = new Tillatelse();
        tillatelse.setGyldighetsperiode(conversionService.convert(avgjorelse.getTillatelsePeriode(), Periode.class));
        tillatelse.setVarighet(avgjorelse.getTillatelseVarighet());
        tillatelse.setVarighetKode(avgjorelse.getTillatelseVarighetKode().udiKodeverk());
        udiAvgjorelse.setTillatelse(tillatelse);

        udiAvgjorelse.setUavklartFlyktningstatus(avgjorelse.getUavklartFlyktningstatus());

        Utfall utfall = new Utfall();
        utfall.setGjeldendePeriode(conversionService.convert(avgjorelse.getTillatelsePeriode(), Periode.class));
        utfall.setVarighet(avgjorelse.getTillatelseVarighet());
        utfall.setVarighetKode(avgjorelse.getTillatelseVarighetKode().udiKodeverk());
        udiAvgjorelse.setUtfall(utfall);

        if (avgjorelse.getUtreisefristDato() != null) {
            udiAvgjorelse.setUtreisefristDato(conversionService.convert(avgjorelse.getIverksettelseDato(), XMLGregorianCalendar.class));
        } else {
            udiAvgjorelse.setUtreisefristDato(null);
        }
        return udiAvgjorelse;

    }
}
