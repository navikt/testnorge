package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.util.DatoFraIdentUtil;
import no.nav.dolly.util.IdentTypeUtil;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FolkeregisterPersonstatusDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import wiremock.com.google.common.collect.Lists;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static wiremock.org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class PersonExcelService {

    private final PdlPersonConsumer pdlPersonConsumer;

    protected static String getFornavn(PdlPerson.Navn navn) {

        var mellomnavn = nonNull(navn) && isNotBlank(navn.getMellomnavn()) ? navn.getMellomnavn() : "";
        return nonNull(navn) && isNotBlank(navn.getFornavn()) ? String.format("%s %s", navn.getFornavn(), mellomnavn) : "";
    }

    protected static String getEtternavn(PdlPerson.Navn navn) {

        return nonNull(navn) && isNotBlank(navn.getEtternavn()) ? navn.getEtternavn() : "";
    }

    private static String getFullmektig(List<FullmaktDTO> fullmakt) {

        return fullmakt.stream()
                .map(FullmaktDTO::getMotpartsPersonident)
                .collect(Collectors.joining(",\n"));
    }

    private static String getVerge(List<VergemaalDTO> vergemaal) {

        return vergemaal.stream()
                .map(VergemaalDTO::getVergeIdent)
                .collect(Collectors.joining(",\n"));
    }

    private static String getForeldre(List<PdlPerson.ForelderBarnRelasjon> foreldre) {

        return foreldre.stream()
                .filter(forelder -> PdlPerson.Rolle.BARN == forelder.getMinRolleForPerson())
                .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                .collect(Collectors.joining(",\n"));
    }

    private static String getBarn(List<PdlPerson.ForelderBarnRelasjon> barn) {

        return barn.stream()
                .filter(barnet -> PdlPerson.Rolle.BARN == barnet.getRelatertPersonsRolle())
                .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                .collect(Collectors.joining(",\n"));
    }

    private static String getPartnere(List<PdlPerson.Sivilstand> partnere) {

        return partnere.stream()
                .filter(partner -> nonNull(partner.getRelatertVedSivilstand()))
                .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                .collect(Collectors.joining(",\n"));
    }

    public List<Object[]> getFormattedCellsFromPdl(List<String> identer) {

        return Lists.partition(identer, 10).stream()
                .map(pdlPersonConsumer::getPdlPersoner)
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Collection::stream)
                .filter(bolkPerson -> nonNull(bolkPerson.getPerson()))
                .map(person -> new Object[]{
                        person.getIdent(),
                        IdentTypeUtil.getIdentType(person.getIdent()).name(),
                        getFornavn(person.getPerson().getNavn().stream().findFirst().orElse(null)),
                        getEtternavn(person.getPerson().getNavn().stream().findFirst().orElse(null)),
                        getAlder(person.getIdent(), person.getPerson().getDoedsfall().isEmpty() ? null :
                                person.getPerson().getDoedsfall().stream().findFirst().get().getDoedsdato()),
                        getKjoenn(person.getPerson().getKjoenn().stream().findFirst().orElse(null)),
                        getDoedsdato(person.getPerson().getDoedsfall().stream().findFirst().orElse(null)),
                        getPersonstatus(person.getPerson().getFolkeregisterpersonstatus().stream().findFirst().orElse(null)),
                        getStatsborgerskap(person.getPerson().getStatsborgerskap().stream().findFirst().orElse(null)),
                        getAdressebeskyttelse(person.getPerson().getAdressebeskyttelse().stream().findFirst().orElse(null)),
                        getBoadresse(person.getPerson().getBostedsadresse().stream().findFirst().orElse(new BostedadresseDTO())),
                        getKontaktadresse(person.getPerson().getKontaktadresse().stream().findFirst().orElse(new KontaktadresseDTO())),
                        getOppholdsadresse(person.getPerson().getOppholdsadresse().stream().findFirst().orElse(new OppholdsadresseDTO())),
                        getSivilstand(person.getPerson().getSivilstand().stream().findFirst().orElse(null)),
                        getPartnere(person.getPerson().getSivilstand()),
                        getBarn(person.getPerson().getForelderBarnRelasjon()),
                        getForeldre(person.getPerson().getForelderBarnRelasjon()),
                        getVerge(person.getPerson().getVergemaal()),
                        getFullmektig(person.getPerson().getFullmakt())
                })
                .toList();
    }

    protected static String getStatsborgerskap(PdlPerson.Statsborgerskap statsborgerskap) {

        return nonNull(statsborgerskap) && isNotBlank(statsborgerskap.getLand()) ?
                statsborgerskap.getLand() : "";
    }

    protected static Integer getAlder(String ident, LocalDate doedsdato) {

        return (int) ChronoUnit.YEARS.between(
                DatoFraIdentUtil.getDato(ident),
                isNull(doedsdato) ? LocalDate.now() : doedsdato.atStartOfDay());
    }

    protected static String getDoedsdato(PdlPerson.Doedsfall doedsfall) {

        return nonNull(doedsfall) && nonNull(doedsfall.getDoedsdato()) ?
                doedsfall.getDoedsdato().toString() : "";
    }

    protected static String getKjoenn(PdlPerson.PdlKjoenn kjoenn) {

        return nonNull(kjoenn) && nonNull(kjoenn.getKjoenn()) ? kjoenn.getKjoenn() : "";
    }

    protected static String getPersonstatus(FolkeregisterPersonstatusDTO personstatus) {

        return nonNull(personstatus) ? personstatus.getStatus().name() : "";
    }

    protected static String getAdressebeskyttelse(AdressebeskyttelseDTO adressebeskyttelse) {

        return nonNull(adressebeskyttelse) ? adressebeskyttelse.getGradering().name() : "";
    }

    protected static String getSivilstand(PdlPerson.Sivilstand sivilstand) {

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
                    .collect(Collectors.joining(", "));

        } else if (nonNull(kontaktadresse.getUtenlandskAdresseIFrittFormat())) {
            return String.format("%s, %s", kontaktadresse.getUtenlandskAdresseIFrittFormat().getAdresselinjer().stream()
                    .collect(Collectors.joining(", ")), kontaktadresse.getUtenlandskAdresseIFrittFormat().getLandkode());

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
}
