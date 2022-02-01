package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.util.DatoFraIdentUtil;
import no.nav.dolly.util.IdentTypeUtil;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KontaktadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import wiremock.com.google.common.collect.Lists;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static wiremock.org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@RequiredArgsConstructor
public class PersonExcelService {

    private static final Object[] header = {"Ident", "Identtype", "Fornavn", "Etternavn", "Alder", "Kjønn", "Foedselsdato",
            "Dødsdato", "Personstatus", "Statsborgerskap", "Adressebeskyttelse", "Bostedsadresse", "Kontaktadresse",
            "Oppholdsadresse", "Sivilstand", "Partner", "Barn", "Foreldre", "Verge", "Fullmektig", "Sikkerhetstiltak"};
    private static final Integer[] COL_WIDTHS = {14, 10, 20, 20, 6, 8, 12, 12, 18, 20, 20, 25, 25, 25, 25, 14, 14, 14, 14, 14, 14};
    private static final DateTimeFormatter NORSK_DATO = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String DUAL_FMT = "%s %s";
    private static final String ADR_UTLAND_FMT = "%s (%s)";
    private static final String POSTNUMMER = "Postnummer";
    private static final String KOMMUNENR = "Kommuner";
    private static final String LAMDKODER = "Landkoder";
    private static final String CO_ADRESSE = "CoAdressenavn: %s";
    private static final String COMMA_DELIM = ", ";

    private final PdlPersonConsumer pdlPersonConsumer;
    private final KodeverkConsumer kodeverkConsumer;

    private static String getFornavn(PdlPerson.Navn navn) {

        var mellomnavn = nonNull(navn) && isNotBlank(navn.getMellomnavn()) ? navn.getMellomnavn() : "";
        return nonNull(navn) && isNotBlank(navn.getFornavn()) ? String.format(DUAL_FMT, navn.getFornavn(), mellomnavn) : "";
    }

    private static String getEtternavn(PdlPerson.Navn navn) {

        return nonNull(navn) && isNotBlank(navn.getEtternavn()) ? navn.getEtternavn() : "";
    }

    private static String getFullmektig(List<FullmaktDTO> fullmakt) {

        return fullmakt.stream()
                .map(FullmaktDTO::getMotpartsPersonident)
                .collect(Collectors.joining(",\n"));
    }

    private static String getVerge(List<PdlPerson.Vergemaal> vergemaal) {

        return vergemaal.stream()
                .map(PdlPerson.Vergemaal::getVergeEllerFullmektig)
                .map(PdlPerson.VergeEllerFullmektig::getMotpartsPersonident)
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


    private static String getFoedselsdato(PdlPerson.Foedsel foedsel) {

        return nonNull(foedsel) && nonNull(foedsel.getFoedselsdato()) ? foedsel.getFoedselsdato().format(NORSK_DATO) : "";
    }

    private static Integer getAlder(String ident, LocalDate doedsdato) {

        return (int) ChronoUnit.YEARS.between(
                DatoFraIdentUtil.getDato(ident),
                isNull(doedsdato) ? LocalDate.now() : doedsdato.atStartOfDay());
    }

    private static String getDoedsdato(PdlPerson.Doedsfall doedsfall) {

        return nonNull(doedsfall) && nonNull(doedsfall.getDoedsdato()) ?
                doedsfall.getDoedsdato().format(NORSK_DATO) : "";
    }

    private static String getKjoenn(PdlPerson.PdlKjoenn kjoenn) {

        return nonNull(kjoenn) && nonNull(kjoenn.getKjoenn()) ? kjoenn.getKjoenn() : "";
    }

    private static String getPersonstatus(PdlPerson.FolkeregisterPersonstatus personstatus) {

        return nonNull(personstatus) ? personstatus.getStatus().name() : "";
    }

    private static String getAdressebeskyttelse(AdressebeskyttelseDTO adressebeskyttelse) {

        return nonNull(adressebeskyttelse) ? adressebeskyttelse.getGradering().name() : "";
    }

    private static String getSivilstand(PdlPerson.Sivilstand sivilstand) {

        return nonNull(sivilstand) ? sivilstand.getType().name() : "";
    }

    private static String getSikkerhetstiltak(List<SikkerhetstiltakDTO> sikkerhetstiltak) {

        return sikkerhetstiltak.stream()
                .map(tiltak -> String.format("%s -- %s", tiltak.getTiltakstype(), tiltak.getBeskrivelse()) +
                        (nonNull(tiltak.getKontaktperson()) && isNotBlank(tiltak.getKontaktperson().getPersonident()) ?
                                String.format(", kontaktperson: %s", tiltak.getKontaktperson().getPersonident()) : ""))
                .collect(Collectors.joining(",\n"));
    }

    private String getStatsborgerskap(PdlPerson.Statsborgerskap statsborgerskap) {

        return nonNull(statsborgerskap) && isNotBlank(statsborgerskap.getLand()) ?
                String.format(ADR_UTLAND_FMT, statsborgerskap.getLand(),
                        kodeverkConsumer.getKodeverkByName(LAMDKODER)
                                .get(statsborgerskap.getLand())) : "";
    }

    private String formatUtenlandskAdresse(UtenlandskAdresseDTO utenlandskAdresse, String coAdresseNavn) {
        return Arrays.stream(new String[]{utenlandskAdresse.getAdressenavnNummer(),
                        utenlandskAdresse.getPostboksNummerNavn(),
                        utenlandskAdresse.getRegionDistriktOmraade(),
                        Stream.of(utenlandskAdresse.getBySted(), utenlandskAdresse.getPostkode())
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.joining(" ")),
                        String.format(ADR_UTLAND_FMT, utenlandskAdresse.getLandkode(),
                                kodeverkConsumer.getKodeverkByName(LAMDKODER).get(utenlandskAdresse.getLandkode())),
                        isNotBlank(coAdresseNavn) ? String.format(CO_ADRESSE, coAdresseNavn) : null})
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(COMMA_DELIM));
    }

    private String formatMatrikkeladresse(MatrikkeladresseDTO matrikkeladresse, String matrikkelId, String coAdresseNavn) {

        return Stream.of("Matrikkeladresse",
                        isNotBlank(matrikkelId) ? String.format("MatrikkelId: %s", matrikkelId) : null,
                        isNotBlank(matrikkeladresse.getTilleggsnavn()) ?
                                String.format("Tilleggsadresse: %s", matrikkeladresse.getTilleggsnavn()) : null,
                        isNotBlank(matrikkeladresse.getBruksenhetsnummer()) ?
                                String.format("Bruksenhet: %s",
                                        matrikkeladresse.getBruksenhetsnummer()) : null,
                        String.format("Kommune: %s %s", matrikkeladresse.getKommunenummer(),
                                kodeverkConsumer.getKodeverkByName(KOMMUNENR).get(matrikkeladresse.getKommunenummer())),
                        isNotBlank(coAdresseNavn) ? String.format(CO_ADRESSE, coAdresseNavn) : null)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(COMMA_DELIM));
    }

    private String formatVegadresse(VegadresseDTO vegadresse, String coAdresseNavn) {
        return Stream.of(String.format(DUAL_FMT, vegadresse.getAdressenavn(), vegadresse.getHusnummer() +
                                (isNotBlank(vegadresse.getHusbokstav()) ? vegadresse.getHusbokstav() : "")),
                        isNotBlank(vegadresse.getBruksenhetsnummer()) ?
                                String.format("Bruksenhet: %s",
                                        vegadresse.getBruksenhetsnummer()) : null,
                        String.format(DUAL_FMT, vegadresse.getPostnummer(),
                                kodeverkConsumer.getKodeverkByName(POSTNUMMER).get(vegadresse.getPostnummer()),
                                isNotBlank(coAdresseNavn) ? String.format(CO_ADRESSE, coAdresseNavn) : null))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(COMMA_DELIM));
    }

    private String getBoadresse(BostedadresseDTO bostedadresse) {

        if (nonNull(bostedadresse.getVegadresse())) {
            return formatVegadresse(bostedadresse.getVegadresse(), bostedadresse.getCoAdressenavn());

        } else if (nonNull(bostedadresse.getMatrikkeladresse())) {
            return formatMatrikkeladresse(bostedadresse.getMatrikkeladresse(),
                    bostedadresse.getAdresseIdentifikatorFraMatrikkelen(), bostedadresse.getCoAdressenavn());

        } else if (nonNull(bostedadresse.getUkjentBosted())) {
            return Stream.of("Ukjent bosted",
                            isNotBlank(bostedadresse.getUkjentBosted().getBostedskommune()) ?
                                    String.format("i kommune %s %s", bostedadresse.getUkjentBosted().getBostedskommune(),
                                            kodeverkConsumer.getKodeverkByName(KOMMUNENR)
                                                    .get(bostedadresse.getUkjentBosted().getBostedskommune())) : null)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(" "));

        } else if (nonNull(bostedadresse.getUtenlandskAdresse())) {
            return formatUtenlandskAdresse(bostedadresse.getUtenlandskAdresse(), bostedadresse.getCoAdressenavn());
        } else {
            return "";
        }
    }

    private String getKontaktadresse(KontaktadresseDTO kontaktadresse) {

        if (nonNull(kontaktadresse.getVegadresse())) {
            return formatVegadresse(kontaktadresse.getVegadresse(), kontaktadresse.getCoAdressenavn());

        } else if (nonNull(kontaktadresse.getPostboksadresse())) {
            return Stream.of(kontaktadresse.getPostboksadresse().getPostbokseier(),
                            kontaktadresse.getPostboksadresse().getPostboks(),
                            kontaktadresse.getPostboksadresse().getPostnummer())
                    .collect(Collectors.joining(COMMA_DELIM));

        } else if (nonNull(kontaktadresse.getUtenlandskAdresse())) {

            return formatUtenlandskAdresse(kontaktadresse.getUtenlandskAdresse(), kontaktadresse.getCoAdressenavn());

        } else if (nonNull(kontaktadresse.getPostadresseIFrittFormat())) {
            return Stream.of(kontaktadresse.getPostadresseIFrittFormat().getAdresselinjer().stream()
                                    .filter(StringUtils::isNotBlank)
                                    .collect(Collectors.joining(COMMA_DELIM)),
                            isNotBlank(kontaktadresse.getPostadresseIFrittFormat().getPostnummer()) ?
                                    String.format(DUAL_FMT, kontaktadresse.getPostadresseIFrittFormat().getPostnummer(),
                                            kodeverkConsumer.getKodeverkByName(POSTNUMMER)
                                                    .get(kontaktadresse.getPostadresseIFrittFormat().getPostnummer())) : null)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(COMMA_DELIM));

        } else if (nonNull(kontaktadresse.getUtenlandskAdresseIFrittFormat())) {
            return Stream.of(kontaktadresse.getUtenlandskAdresseIFrittFormat().getAdresselinjer().stream()
                                    .collect(Collectors.joining(COMMA_DELIM)),
                            String.format(ADR_UTLAND_FMT, kontaktadresse.getUtenlandskAdresseIFrittFormat().getLandkode(),
                                    kodeverkConsumer.getKodeverkByName(LAMDKODER)
                                            .get(kontaktadresse.getUtenlandskAdresseIFrittFormat().getLandkode())))
                    .collect(Collectors.joining(COMMA_DELIM));

        } else {
            return "";
        }
    }

    private String getOppholdsadresse(OppholdsadresseDTO oppholdsadresse) {

        if (nonNull(oppholdsadresse.getVegadresse())) {
            return formatVegadresse(oppholdsadresse.getVegadresse(), oppholdsadresse.getCoAdressenavn());

        } else if (nonNull(oppholdsadresse.getMatrikkeladresse())) {
            return formatMatrikkeladresse(oppholdsadresse.getMatrikkeladresse(),
                    oppholdsadresse.getAdresseIdentifikatorFraMatrikkelen(), oppholdsadresse.getCoAdressenavn());

        } else if (nonNull(oppholdsadresse.getUtenlandskAdresse())) {
            return formatUtenlandskAdresse(oppholdsadresse.getUtenlandskAdresse(), oppholdsadresse.getCoAdressenavn());

        } else {
            return "";
        }
    }

    public void preparePersonSheet(XSSFWorkbook workbook, XSSFCellStyle wrapStyle, List<String> identer) {

        var sheet = workbook.createSheet("Personer");
        var rows = getPersondataRowContents(identer);
        sheet.addIgnoredErrors(new CellRangeAddress(0, rows.size(), 0, header.length),
                IgnoredErrorType.NUMBER_STORED_AS_TEXT);

        var columnNo = new AtomicInteger(0);
        Arrays.stream(COL_WIDTHS)
                .forEach(colWidth -> sheet.setColumnWidth(columnNo.getAndIncrement(), colWidth * 256));

        ExcelService.appendRows(sheet, wrapStyle, Stream.of(Collections.singletonList(header),
                        getPersondataRowContents(identer))
                .flatMap(Collection::stream)
                .toList());
    }

    private List<Object[]> getPersondataRowContents(List<String> identer) {

        return Lists.partition(identer, 20).stream()
                .map(pdlPersonConsumer::getPdlPersoner)
                .map(PdlPersonBolk::getData)
                .filter(Objects::nonNull)
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
                        getFoedselsdato(person.getPerson().getFoedsel().stream().findFirst().orElse(null)),
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
                        getVerge(person.getPerson().getVergemaalEllerFremtidsfullmakt()),
                        getFullmektig(person.getPerson().getFullmakt()),
                        getSikkerhetstiltak(person.getPerson().getSikkerhetstiltak())
                })
                .toList();
    }
}
