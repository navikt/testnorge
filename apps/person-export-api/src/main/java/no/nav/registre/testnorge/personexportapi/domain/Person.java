package no.nav.registre.testnorge.personexportapi.domain;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import no.nav.registre.testnorge.personexportapi.consumer.dto.EndringsmeldingDTO;
import no.nav.registre.testnorge.personexportapi.consumer.dto.HusbokstavEncoder;

public class Person {

    private final EndringsmeldingDTO endringsmeldingDTO;

    public Person(EndringsmeldingDTO endringsmeldingDTO) {
        this.endringsmeldingDTO = endringsmeldingDTO;
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
        return endringsmeldingDTO.isGateadresse() ? "GATE" :
                endringsmeldingDTO.isMatrikkeladresse() ? "MATR" : null;
    }

    public String getGatenavn() {
        return endringsmeldingDTO.isGateadresse() ? endringsmeldingDTO.getGateGaard() : null;
    }

    public String getGaardsnavn() {
        return endringsmeldingDTO.isMatrikkeladresse() ? endringsmeldingDTO.getGateGaard() : null;
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
                LocalDate.parse(endringsmeldingDTO.getFlyttedatoAdr()).format(DateTimeFormatter.ISO_DATE) : null;
    }
}