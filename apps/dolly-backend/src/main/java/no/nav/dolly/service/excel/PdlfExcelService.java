package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.util.DatoFraIdentUtil;
import no.nav.dolly.util.IdentTypeUtil;
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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static wiremock.org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class PdlfExcelService {

    private final PdlDataConsumer pdlDataConsumer;

    private static Integer getAlder(String ident, LocalDate doedsdato) {

        return (int) ChronoUnit.YEARS.between(
                DatoFraIdentUtil.getDato(ident),
                isNull(doedsdato) ? LocalDate.now() : doedsdato.atStartOfDay());
    }

    private static String getDoedsdato(DoedsfallDTO doedsfall) {

        return nonNull(doedsfall) ? doedsfall.getDoedsdato().toLocalDate().toString() : "";
    }

    private static String getPersonstatus(FolkeregisterPersonstatusDTO personstatus) {

        return nonNull(personstatus) ? personstatus.getStatus().name() : "";
    }

    private static String getAdressebeskyttelse(AdressebeskyttelseDTO adressebeskyttelse) {

        return nonNull(adressebeskyttelse) ? adressebeskyttelse.getGradering().name() : "";
    }

    private static String getSivilstand(SivilstandDTO sivilstand) {

        return nonNull(sivilstand) ? sivilstand.getType().name() : "";
    }

    private static String getBoadresse(BostedadresseDTO bostedadresse) {

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
            return String.format("%s, %s, %s %s, %s", bostedadresse.getUtenlandskAdresse().getAdressenavnNummer(),
                    bostedadresse.getUtenlandskAdresse().getRegionDistriktOmraade(),
                    bostedadresse.getUtenlandskAdresse().getBySted(), bostedadresse.getUtenlandskAdresse().getPostkode(),
                    bostedadresse.getUtenlandskAdresse().getLandkode());
        } else {
            return "";
        }
    }

    private static String getKontaktadresse(KontaktadresseDTO kontaktadresse) {

        if (nonNull(kontaktadresse.getVegadresse())) {
            return String.format("%s %s, %s", kontaktadresse.getVegadresse().getAdressenavn(), kontaktadresse.getVegadresse().getHusnummer() +
                    kontaktadresse.getVegadresse().getHusbokstav(), kontaktadresse.getVegadresse().getPostnummer());

        } else if (nonNull(kontaktadresse.getPostboksadresse())) {
            return String.format("%s, %s, %s", kontaktadresse.getPostboksadresse().getPostbokseier(),
                    kontaktadresse.getPostboksadresse().getPostboks(),
                    kontaktadresse.getPostboksadresse().getPostnummer());

        } else if (nonNull(kontaktadresse.getUtenlandskAdresse())) {
            return String.format("%s, %s, %s %s, %s", kontaktadresse.getUtenlandskAdresse().getAdressenavnNummer(),
                    kontaktadresse.getUtenlandskAdresse().getRegionDistriktOmraade(),
                    kontaktadresse.getUtenlandskAdresse().getBySted(), kontaktadresse.getUtenlandskAdresse().getPostkode(),
                    kontaktadresse.getUtenlandskAdresse().getLandkode());

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

    private static String getOppholdsadresse(OppholdsadresseDTO oppholdsadresse) {

        if (nonNull(oppholdsadresse.getVegadresse())) {
            return String.format("%s %s, %s", oppholdsadresse.getVegadresse().getAdressenavn(), oppholdsadresse.getVegadresse().getHusnummer() +
                    oppholdsadresse.getVegadresse().getHusbokstav(), oppholdsadresse.getVegadresse().getPostnummer());

        } else if (nonNull(oppholdsadresse.getMatrikkeladresse())) {
            return String.format("Gaardsnummer: %d, Bruksnummer: %d, Kommunenr: %s", oppholdsadresse.getMatrikkeladresse().getGaardsnummer(),
                    oppholdsadresse.getMatrikkeladresse().getBruksenhetsnummer(), oppholdsadresse.getMatrikkeladresse().getKommunenummer());

        } else if (nonNull(oppholdsadresse.getUtenlandskAdresse())) {
            return String.format("%s, %s, %s %s, %s", oppholdsadresse.getUtenlandskAdresse().getAdressenavnNummer(),
                    oppholdsadresse.getUtenlandskAdresse().getRegionDistriktOmraade(),
                    oppholdsadresse.getUtenlandskAdresse().getBySted(), oppholdsadresse.getUtenlandskAdresse().getPostkode(),
                    oppholdsadresse.getUtenlandskAdresse().getLandkode());
        } else {
            return "";
        }
    }

    private static String getFornavn(NavnDTO navn) {

        return isBlank(navn.getMellomnavn()) ? navn.getFornavn() : String.format("%s %s", navn.getFornavn(), navn.getMellomnavn());
    }


    public List<Object[]> getPdlfCells(List<String> identer) {

        var personer = pdlDataConsumer.getPersoner(identer, 0, identer.size());

        return personer.stream()
                .map(person -> new Object[]{
                        person.getPerson().getIdent(),
                        IdentTypeUtil.getIdentType(person.getPerson().getIdent()).name(),
                        getFornavn(person.getPerson().getNavn().stream().findFirst().orElse(new NavnDTO())),
                        person.getPerson().getNavn().stream().findFirst().orElse(new NavnDTO()).getEtternavn(),
                        getAlder(person.getPerson().getIdent(), person.getPerson().getDoedsfall().isEmpty() ? null :
                                person.getPerson().getDoedsfall().stream().findFirst().get().getDoedsdato().toLocalDate()),
                        person.getPerson().getKjoenn().stream().findFirst().orElse(new KjoennDTO()).getKjoenn().name(),
                        getDoedsdato(person.getPerson().getDoedsfall().stream().findFirst().orElse(null)),
                        getPersonstatus(person.getPerson().getFolkeregisterPersonstatus().stream().findFirst().orElse(null)),
                        person.getPerson().getStatsborgerskap().stream().findFirst().orElse(new StatsborgerskapDTO()).getLandkode(),
                        getAdressebeskyttelse(person.getPerson().getAdressebeskyttelse().stream().findFirst().orElse(null)),
                        getBoadresse(person.getPerson().getBostedsadresse().stream().findFirst().orElse(new BostedadresseDTO())),
                        getKontaktadresse(person.getPerson().getKontaktadresse().stream().findFirst().orElse(new KontaktadresseDTO())),
                        getOppholdsadresse(person.getPerson().getOppholdsadresse().stream().findFirst().orElse(new OppholdsadresseDTO())),
                        getSivilstand(person.getPerson().getSivilstand().stream().findFirst().orElse(null))
                })
                .toList();
    }

}
