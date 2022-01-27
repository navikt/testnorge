package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.util.DatoFraIdentUtil;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DoedsfallDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.NavnDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.StatsborgerskapDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static wiremock.org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public abstract class PdlPersonExcelService {

    protected static String getStatsborgerskap(StatsborgerskapDTO statsborgerskap) {

        return nonNull(statsborgerskap) && isNotBlank(statsborgerskap.getLandkode()) ?
                statsborgerskap.getLandkode() : "";
    }

    protected static LocalDate toLocalDate(LocalDateTime timestamp) {

        return nonNull(timestamp) ? timestamp.toLocalDate() : null;
    }

    protected static Integer getAlder(String ident, LocalDate doedsdato) {

        return (int) ChronoUnit.YEARS.between(
                DatoFraIdentUtil.getDato(ident),
                isNull(doedsdato) ? LocalDate.now() : doedsdato.atStartOfDay());
    }

    protected static String getDoedsdato(DoedsfallDTO doedsfall) {

        return nonNull(doedsfall) && nonNull(doedsfall.getDoedsdato()) ?
                doedsfall.getDoedsdato().toLocalDate().toString() : "";
    }

    protected static String getKjoenn(KjoennDTO kjoenn) {

        return nonNull(kjoenn) && nonNull(kjoenn.getKjoenn()) ? kjoenn.getKjoenn().name() : "";
    }

    protected static String getPersonstatus(FolkeregisterPersonstatusDTO personstatus) {

        return nonNull(personstatus) ? personstatus.getStatus().name() : "";
    }

    protected static String getAdressebeskyttelse(AdressebeskyttelseDTO adressebeskyttelse) {

        return nonNull(adressebeskyttelse) ? adressebeskyttelse.getGradering().name() : "";
    }

    protected static String getSivilstand(SivilstandDTO sivilstand) {

        return nonNull(sivilstand) ? sivilstand.getType().name() : "";
    }

    protected static String getBoadresse(BostedadresseDTO bostedadresse) {

        if (nonNull(bostedadresse.getVegadresse())) {
            return String.format("%s %s, %s", bostedadresse.getVegadresse().getAdressenavn(), bostedadresse.getVegadresse().getHusnummer() +
                            (isNotBlank(bostedadresse.getVegadresse().getHusbokstav()) ? bostedadresse.getVegadresse().getHusbokstav() : ""),
                    bostedadresse.getVegadresse().getPostnummer());

        } else if (nonNull(bostedadresse.getMatrikkeladresse())) {
            return String.format("Gaardsnummer: %d, Bruksnummer: %d, Kommunenr: %s", bostedadresse.getMatrikkeladresse().getGaardsnummer(),
                    bostedadresse.getMatrikkeladresse().getBruksenhetsnummer(), bostedadresse.getMatrikkeladresse().getKommunenummer());

        } else if (nonNull(bostedadresse.getUkjentBosted())) {
            return String.format("Ukjent bosted i kommunenr %s", bostedadresse.getUkjentBosted().getBostedskommune());

        } else if (nonNull(bostedadresse.getUtenlandskAdresse())) {
            return Arrays.stream(new String[]{bostedadresse.getUtenlandskAdresse().getAdressenavnNummer(),
                            bostedadresse.getUtenlandskAdresse().getPostboksNummerNavn(),
                            bostedadresse.getUtenlandskAdresse().getRegionDistriktOmraade(),
                            bostedadresse.getUtenlandskAdresse().getBySted(),
                            bostedadresse.getUtenlandskAdresse().getPostkode(),
                            bostedadresse.getUtenlandskAdresse().getLandkode()})
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(" "));
        } else {
            return "";
        }
    }

    protected static String getKontaktadresse(KontaktadresseDTO kontaktadresse) {

        if (nonNull(kontaktadresse.getVegadresse())) {
            return String.format("%s %s, %s", kontaktadresse.getVegadresse().getAdressenavn(), kontaktadresse.getVegadresse().getHusnummer() +
                    kontaktadresse.getVegadresse().getHusbokstav(), kontaktadresse.getVegadresse().getPostnummer());

        } else if (nonNull(kontaktadresse.getPostboksadresse())) {
            return String.format("%s, %s, %s", kontaktadresse.getPostboksadresse().getPostbokseier(),
                    kontaktadresse.getPostboksadresse().getPostboks(),
                    kontaktadresse.getPostboksadresse().getPostnummer());

        } else if (nonNull(kontaktadresse.getUtenlandskAdresse())) {

            return Arrays.stream(new String[]{kontaktadresse.getUtenlandskAdresse().getAdressenavnNummer(),
                            kontaktadresse.getUtenlandskAdresse().getPostboksNummerNavn(),
                            kontaktadresse.getUtenlandskAdresse().getRegionDistriktOmraade(),
                            kontaktadresse.getUtenlandskAdresse().getBySted(),
                            kontaktadresse.getUtenlandskAdresse().getPostkode(),
                            kontaktadresse.getUtenlandskAdresse().getLandkode()})
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(" "));

        } else if (nonNull(kontaktadresse.getPostadresseIFrittFormat())) {
            return kontaktadresse.getPostadresseIFrittFormat().getAdresselinjer().stream()
                    .collect(Collectors.joining(" ,"));

        } else if (nonNull(kontaktadresse.getUtenlandskAdresseIFrittFormat())) {
            return String.format("%s, %s", kontaktadresse.getUtenlandskAdresseIFrittFormat().getAdresselinjer().stream()
                    .collect(Collectors.joining(" ,")), kontaktadresse.getUtenlandskAdresseIFrittFormat().getLandkode());

        } else {
            return "";
        }
    }

    protected static String getOppholdsadresse(OppholdsadresseDTO oppholdsadresse) {

        if (nonNull(oppholdsadresse.getVegadresse())) {
            return String.format("%s %s, %s", oppholdsadresse.getVegadresse().getAdressenavn(), oppholdsadresse.getVegadresse().getHusnummer() +
                    oppholdsadresse.getVegadresse().getHusbokstav(), oppholdsadresse.getVegadresse().getPostnummer());

        } else if (nonNull(oppholdsadresse.getMatrikkeladresse())) {
            return String.format("Gaardsnummer: %d, Bruksnummer: %d, Kommunenr: %s", oppholdsadresse.getMatrikkeladresse().getGaardsnummer(),
                    oppholdsadresse.getMatrikkeladresse().getBruksenhetsnummer(), oppholdsadresse.getMatrikkeladresse().getKommunenummer());

        } else if (nonNull(oppholdsadresse.getUtenlandskAdresse())) {
            return Arrays.stream(new String[]{oppholdsadresse.getUtenlandskAdresse().getAdressenavnNummer(),
                            oppholdsadresse.getUtenlandskAdresse().getPostboksNummerNavn(),
                            oppholdsadresse.getUtenlandskAdresse().getRegionDistriktOmraade(),
                            oppholdsadresse.getUtenlandskAdresse().getBySted(),
                            oppholdsadresse.getUtenlandskAdresse().getPostkode(),
                            oppholdsadresse.getUtenlandskAdresse().getLandkode()})
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(" "));
        } else {
            return "";
        }
    }

    protected static String getFornavn(NavnDTO navn) {

        var mellomnavn = nonNull(navn) && isNotBlank(navn.getMellomnavn()) ? navn.getMellomnavn() : "";
        return nonNull(navn) && isNotBlank(navn.getFornavn()) ? String.format("%s %s", navn.getFornavn(), mellomnavn) : "";
    }

    protected static String getEtternavn(NavnDTO navn) {

        return nonNull(navn) && isNotBlank(navn.getEtternavn()) ? navn.getEtternavn() : "";
    }
}
