package no.nav.registre.testnorge.personexportapi.domain;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import no.nav.registre.testnorge.personexportapi.consumer.dto.EndringsmeldingDTO;
import no.nav.registre.testnorge.personexportapi.consumer.dto.FoedselsdatoFraIdent;
import no.nav.registre.testnorge.personexportapi.consumer.dto.HusbokstavEncoder;
import no.nav.registre.testnorge.personexportapi.consumer.dto.KjoennFraIdent;
import no.nav.registre.testnorge.personexportapi.consumer.dto.LandkodeEncoder;
import no.nav.registre.testnorge.personexportapi.consumer.dto.Sivilstatus;

public class Person {

    private final EndringsmeldingDTO endringsmeldingDTO;
    private final String page;

    public Person(EndringsmeldingDTO endringsmeldingDTO, String page) {
        this.endringsmeldingDTO = endringsmeldingDTO;
        this.page = page;
    }

    public String getPage() {
        return page;
    }

    public String getIdent() {
        return endringsmeldingDTO.getFodselsdato() + endringsmeldingDTO.getPersonnummer();
    }

    public String getFornavn() {
        return endringsmeldingDTO.getFornavn();
    }

    public String getMellomnavn() {
        return isNotBlank(endringsmeldingDTO.getMellomnavn()) ? endringsmeldingDTO.getMellomnavn() : null;
    }

    public String getEtternavn() {
        return endringsmeldingDTO.getSlektsnavn();
    }

    public String getAdressetype() {
        return endringsmeldingDTO.getAdressetype();
    }

    public String getGatenavn() {
        return endringsmeldingDTO.isGateadresse() ? endringsmeldingDTO.getAdressenavn() : null;
    }

    public String getGaardsnavn() {
        return endringsmeldingDTO.isMatrikkeladresse() ? endringsmeldingDTO.getAdressenavn() : null;
    }

    public String getHusnr() {
        return endringsmeldingDTO.isGateadresse() ? endringsmeldingDTO.getHusBruk() : null;
    }

    public String getHusbokstav() {
        return endringsmeldingDTO.isGateadresse() ? HusbokstavEncoder.decode(endringsmeldingDTO.getBokstavFestenr()) : null;
    }

    public String getGatenr() {
        return endringsmeldingDTO.isGateadresse() ? endringsmeldingDTO.getGateGaard() : null;
    }

    public String getGaardsnr() {
        return endringsmeldingDTO.isMatrikkeladresse() ? endringsmeldingDTO.getGateGaard() : null;
    }

    public String getBruksnr() {
        return endringsmeldingDTO.isMatrikkeladresse() ? endringsmeldingDTO.getHusBruk() : null;
    }

    public String getBolignr() {
        return isNotBlank(endringsmeldingDTO.getBolignr()) ? endringsmeldingDTO.getBolignr() : null;
    }

    public Object getTilleggsadresse() {
        return isNotBlank(endringsmeldingDTO.getTilleggsadresse()) ? endringsmeldingDTO.getTilleggsadresse() : null;
    }

    public String getPostnummer() {
        return isNotBlank(endringsmeldingDTO.getPostnummer()) ? endringsmeldingDTO.getPostnummer() : null;
    }

    public String getKommunenr() {
        return isNotBlank(endringsmeldingDTO.getKommunenummer()) ? endringsmeldingDTO.getKommunenummer() : null;
    }

    public String getFlyttedato() {
        return isNotBlank(endringsmeldingDTO.getFlyttedatoAdr()) ?
                LocalDate.parse(endringsmeldingDTO.getFlyttedatoAdr(), DateTimeFormatter.ofPattern("yyyyMMdd"))
                        .format(DateTimeFormatter.ISO_DATE) : null;
    }

    public String getAdresse1() {
        return isNotBlank(endringsmeldingDTO.getAdresse1()) ? endringsmeldingDTO.getAdresse1() : null;
    }

    public String getAdresse2() {
        return isNotBlank(endringsmeldingDTO.getAdresse2()) ? endringsmeldingDTO.getAdresse2() : null;
    }

    public String getAdresse3() {
        return isNotBlank(endringsmeldingDTO.getAdresse3()) ? endringsmeldingDTO.getAdresse3() : null;
    }

    public String getPostadrLand() {
        return isNotBlank(endringsmeldingDTO.getPostadrLand()) ? LandkodeEncoder.decode(endringsmeldingDTO.getPostadrLand()) : null;
    }

    public String getPersonstatus() {
        return isNotBlank(endringsmeldingDTO.getStatuskode()) ? endringsmeldingDTO.getStatuskode() : null;
    }

    public String getFoedselsdato() {
        return FoedselsdatoFraIdent.getFoedselsdato(getIdent()).format(DateTimeFormatter.ISO_DATE);
    }

    public String getKjoenn() {
        return KjoennFraIdent.getKjoenn(getIdent()).name();
    }

    public String getKjoennBeskrivelse() {
        return KjoennFraIdent.getKjoenn(getIdent()).getBeskrivelse();
    }

    public String getSivilstand() {
        return endringsmeldingDTO.getSivilstand();
    }

    public String getSivilstandBeskrivelse() {
        return Sivilstatus.lookup(getSivilstand()).getKodeverkskode();
    }

    public String getSivilstandRegdato() {
        return endringsmeldingDTO.getRegdatoSivilstand();
    }

    @JsonIgnore
    public boolean isFnr() {
        return parseInt(Character.toString(endringsmeldingDTO.getFodselsdato().charAt(0))) < 4;
    }

    @Override
    public String toString() {
        return format("Page: %s fnr: %s", getPage(), getIdent());
    }
}