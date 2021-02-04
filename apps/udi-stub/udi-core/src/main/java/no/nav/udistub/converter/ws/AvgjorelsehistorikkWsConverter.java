package no.nav.udistub.converter.ws;

import no.nav.udistub.service.dto.UdiAvgjorelse;
import no.nav.udistub.service.dto.UdiPerson;
import no.udi.mt_1067_nav_data.v1.AvgjorelseListe;
import no.udi.mt_1067_nav_data.v1.Avgjorelser;
import no.udi.mt_1067_nav_data.v1.Avgjorelsestype;
import no.udi.mt_1067_nav_data.v1.Tillatelse;
import no.udi.mt_1067_nav_data.v1.Utfall;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AvgjorelsehistorikkWsConverter implements Converter<UdiPerson, Avgjorelser> {

    @Override
    public Avgjorelser convert(UdiPerson person) {
        if (person != null) {
            Avgjorelser avgjorelser = new Avgjorelser();
            if (person.getAvgjoerelseUavklart() != null) {
                avgjorelser.setUavklart(person.getAvgjoerelseUavklart());
            }
            avgjorelser.setAvgjorelseListe(new AvgjorelseListe());
            person.getAvgjoerelser().stream()
                    .map(this::converterPersonAvgjorelse)
                    .forEach(avgjorelser.getAvgjorelseListe().getAvgjorelse()::add);
            return avgjorelser;
        }
        return null;
    }

    private no.udi.mt_1067_nav_data.v1.Avgjorelse converterPersonAvgjorelse(UdiAvgjorelse avgjorelse) {
        XmlDateWsConverter xmlDateWsConverter = new XmlDateWsConverter();
        PeriodeWsConverter periodeWsConverter = new PeriodeWsConverter();
        no.udi.mt_1067_nav_data.v1.Avgjorelse udiAvgjorelse = new no.udi.mt_1067_nav_data.v1.Avgjorelse();
        if (avgjorelse.getIverksettelseDato() != null) {
            udiAvgjorelse.setAvgjorelseDato(xmlDateWsConverter.convert(avgjorelse.getAvgjoerelsesDato()));
        }
        Avgjorelsestype avgjorelsestype = new Avgjorelsestype();
        if (avgjorelse.getGrunntypeKode() != null) {
            avgjorelsestype.setGrunntypeKode(avgjorelse.getGrunntypeKode().udiKodeverk());
        }
        if (avgjorelse.getTillatelseKode() != null) {
            avgjorelsestype.setTillatelseKode(avgjorelse.getTillatelseKode().udiKodeverk());
        }
        if (avgjorelse.getUtfallstypeKode() != null) {
            avgjorelsestype.setUtfallstypeKode(avgjorelse.getUtfallstypeKode().udiKodeverk());
        }
        udiAvgjorelse.setAvgjorelsestype(avgjorelsestype);

        udiAvgjorelse.setEffektueringsDato(xmlDateWsConverter.convert(avgjorelse.getEffektueringsDato()));
        if (avgjorelse.getErPositiv() != null) {
            udiAvgjorelse.setErPositiv(avgjorelse.getErPositiv());
        }
        if (avgjorelse.getEtat() != null && !avgjorelse.getEtat().isEmpty()) {
            udiAvgjorelse.setEtat(avgjorelse.getEtat());
        }
        if (avgjorelse.getHarFlyktningstatus() != null) {
            udiAvgjorelse.setFlyktingstatus(avgjorelse.getHarFlyktningstatus());
        }
        udiAvgjorelse.setIverksettelseDato(xmlDateWsConverter.convert(avgjorelse.getIverksettelseDato()));

        if (avgjorelse.getSaksnummer() != null && !avgjorelse.getSaksnummer().isEmpty()) {
            udiAvgjorelse.setSaksnummer(avgjorelse.getSaksnummer());
        }

        Tillatelse tillatelse = new Tillatelse();
        tillatelse.setGyldighetsperiode(periodeWsConverter.convert(avgjorelse.getTillatelsePeriode()));
        if (avgjorelse.getTillatelseVarighet() != null) {
            tillatelse.setVarighet(avgjorelse.getTillatelseVarighet());
        }
        if (avgjorelse.getTillatelseVarighetKode() != null) {
            tillatelse.setVarighetKode(avgjorelse.getTillatelseVarighetKode().udiKodeverk());
        }
        udiAvgjorelse.setTillatelse(tillatelse);

        udiAvgjorelse.setUavklartFlyktningstatus(avgjorelse.getUavklartFlyktningstatus());

        Utfall utfall = new Utfall();
        utfall.setGjeldendePeriode(periodeWsConverter.convert(avgjorelse.getTillatelsePeriode()));
        if (avgjorelse.getUtfallVarighet() != null) {
            utfall.setVarighet(avgjorelse.getUtfallVarighet());
        }
        if (avgjorelse.getUtfallVarighetKode() != null) {
            utfall.setVarighetKode(avgjorelse.getUtfallVarighetKode().udiKodeverk());
        }
        udiAvgjorelse.setUtfall(utfall);
        udiAvgjorelse.setUtreisefristDato(xmlDateWsConverter.convert(avgjorelse.getIverksettelseDato()));
        return udiAvgjorelse;
    }
}
