package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.util.DatoFraIdentUtil;
import no.nav.dolly.util.IdentTypeUtil;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SikkerhetstiltakDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.service.excel.ExcelUtil.PERSON_FANE;
import static no.nav.dolly.service.excel.ExcelUtil.appendHyperlinkRelasjon;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonExcelService {

    private static final Object[] HEADER = {"Ident", "Identtype", "Fornavn", "Etternavn", "Alder", "Kjønn", "Foedselsdato",
            "Dødsdato", "Personstatus", "Statsborgerskap", "Adressebeskyttelse", "Bostedsadresse", "Kontaktadresse",
            "Oppholdsadresse", "Sivilstand", "Partner", "Barn", "Foreldre", "Verge", "Fullmektig", "Sikkerhetstiltak", "Brukt", "Beskrivelse"};
    private static final Integer[] COL_WIDTHS = {14, 10, 20, 20, 6, 8, 12, 12, 18, 20, 20, 25, 25, 25, 25, 14, 14, 14, 14, 14, 14, 14, 20};
    private static final DateTimeFormatter NORSK_DATO = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String DUAL_FMT = "%s %s";
    private static final String ADR_UTLAND_FMT = "%s (%s)";
    private static final String POSTNUMMER = "Postnummer";
    private static final String KOMMUNENR = "Kommuner";
    private static final String LANDKODER = "Landkoder";
    private static final String UKJENT = "UKJENT";
    private static final String CO_ADRESSE = "CoAdressenavn: %s";
    private static final String COMMA_DELIM = ", ";
    private static final int PARTNER = 15;
    private static final int BARN = 16;
    private static final int FORELDRE = 17;
    private static final int VERGE = 18;
    private static final int FULLMEKTIG = 19;
    private static final int BLOCK_SIZE = 50;

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
                .filter(Objects::nonNull)
                .collect(Collectors.joining(",\n"));
    }

    private static String getBarn(List<PdlPerson.ForelderBarnRelasjon> barn) {

        return barn.stream()
                .filter(barnet -> PdlPerson.Rolle.BARN == barnet.getRelatertPersonsRolle())
                .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                .filter(Objects::nonNull)
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

    private static List<String> getIdenterForRelasjon(List<Object[]> hovedpersoner, int relasjon) {

        return hovedpersoner.stream()
                .map(row -> row[relasjon])
                .map(Object::toString)
                .filter(StringUtils::isNotBlank)
                .map(partnere -> partnere.split(","))
                .map(Arrays::asList)
                .flatMap(Collection::stream)
                .map(String::trim)
                .toList();
    }

    private static String formatUtenlandskAdresse(UtenlandskAdresseDTO utenlandskAdresse,
                                                  String coAdresseNavn, Map<String, String> landkoder) {

        return Arrays.stream(new String[]{utenlandskAdresse.getAdressenavnNummer(),
                        utenlandskAdresse.getPostboksNummerNavn(),
                        utenlandskAdresse.getRegionDistriktOmraade(),
                        Stream.of(utenlandskAdresse.getBySted(), utenlandskAdresse.getPostkode())
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.joining(" ")),
                        String.format(ADR_UTLAND_FMT, utenlandskAdresse.getLandkode(),
                                landkoder.getOrDefault(utenlandskAdresse.getLandkode(), UKJENT)),
                        isNotBlank(coAdresseNavn) ? String.format(CO_ADRESSE, coAdresseNavn) : null})
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(COMMA_DELIM));
    }

    private static String formatMatrikkeladresse(MatrikkeladresseDTO matrikkeladresse, String matrikkelId,
                                                 String coAdresseNavn, Map<String, String> kommunenummer) {

        return Stream.of("Matrikkeladresse",
                        isNotBlank(matrikkelId) ? String.format("MatrikkelId: %s", matrikkelId) : null,
                        isNotBlank(matrikkeladresse.getTilleggsnavn()) ?
                                String.format("Tilleggsadresse: %s", matrikkeladresse.getTilleggsnavn()) : null,
                        isNotBlank(matrikkeladresse.getBruksenhetsnummer()) ?
                                String.format("Bruksenhet: %s",
                                        matrikkeladresse.getBruksenhetsnummer()) : null,
                        String.format("Kommune: %s %s", matrikkeladresse.getKommunenummer(),
                                kommunenummer.get(matrikkeladresse.getKommunenummer())),
                        isNotBlank(coAdresseNavn) ? String.format(CO_ADRESSE, coAdresseNavn) : null)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(COMMA_DELIM));
    }

    private static String formatVegadresse(VegadresseDTO vegadresse, String coAdresseNavn, Map<String, String> postnummer) {
        return Stream.of(String.format(DUAL_FMT, vegadresse.getAdressenavn(), vegadresse.getHusnummer() +
                                (isNotBlank(vegadresse.getHusbokstav()) ? vegadresse.getHusbokstav() : "")),
                        isNotBlank(vegadresse.getBruksenhetsnummer()) ?
                                String.format("Bruksenhet: %s",
                                        vegadresse.getBruksenhetsnummer()) : null,
                        String.format(DUAL_FMT, vegadresse.getPostnummer(),
                                postnummer.get(vegadresse.getPostnummer()),
                                isNotBlank(coAdresseNavn) ? String.format(CO_ADRESSE, coAdresseNavn) : null))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(COMMA_DELIM));
    }

    private static String getKontaktadresse(PdlPerson.Kontaktadresse kontaktadresse,
                                            Map<String, String> postnummer,
                                            Map<String, String> landkoder) {

        if (nonNull(kontaktadresse.getVegadresse())) {
            return formatVegadresse(kontaktadresse.getVegadresse(), kontaktadresse.getCoAdressenavn(), postnummer);

        } else if (nonNull(kontaktadresse.getPostboksadresse())) {
            return Stream.of(kontaktadresse.getPostboksadresse().getPostbokseier(),
                            kontaktadresse.getPostboksadresse().getPostboks(),
                            kontaktadresse.getPostboksadresse().getPostnummer())
                    .collect(Collectors.joining(COMMA_DELIM));

        } else if (nonNull(kontaktadresse.getUtenlandskAdresse())) {

            return formatUtenlandskAdresse(kontaktadresse.getUtenlandskAdresse(), kontaktadresse.getCoAdressenavn(), landkoder);

        } else if (nonNull(kontaktadresse.getPostadresseIFrittFormat())) {
            return Stream.of(kontaktadresse.getPostadresseIFrittFormat().getAdresselinje1(),
                            kontaktadresse.getPostadresseIFrittFormat().getAdresselinje2(),
                            kontaktadresse.getPostadresseIFrittFormat().getAdresselinje3(),
                            isNotBlank(kontaktadresse.getPostadresseIFrittFormat().getPostnummer()) ?
                                    String.format(DUAL_FMT, kontaktadresse.getPostadresseIFrittFormat().getPostnummer(),
                                            postnummer.get(kontaktadresse.getPostadresseIFrittFormat().getPostnummer())) : null)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(COMMA_DELIM));

        } else if (nonNull(kontaktadresse.getUtenlandskAdresseIFrittFormat())) {
            return Stream.of(kontaktadresse.getUtenlandskAdresseIFrittFormat().getAdresselinje1(),
                            kontaktadresse.getUtenlandskAdresseIFrittFormat().getAdresselinje2(),
                            kontaktadresse.getUtenlandskAdresseIFrittFormat().getAdresselinje3(),
                            String.format(ADR_UTLAND_FMT, kontaktadresse.getUtenlandskAdresseIFrittFormat().getLandkode(),
                                    landkoder.getOrDefault(kontaktadresse.getUtenlandskAdresseIFrittFormat().getLandkode(), UKJENT)))
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(COMMA_DELIM));

        } else {
            return "";
        }
    }

    private static String getStatsborgerskap(PdlPerson.Statsborgerskap statsborgerskap, Map<String, String> landkoder) {

        return nonNull(statsborgerskap) && isNotBlank(statsborgerskap.getLand()) ?
                String.format(ADR_UTLAND_FMT, statsborgerskap.getLand(),
                        landkoder.getOrDefault(statsborgerskap.getLand(), UKJENT)) : "";
    }

    private static String getBoadresse(BostedadresseDTO bostedadresse, Map<String, String> postnumre,
                                       Map<String, String> kommunenumre, Map<String, String> landkoder) {

        if (nonNull(bostedadresse.getVegadresse())) {
            return formatVegadresse(bostedadresse.getVegadresse(), bostedadresse.getCoAdressenavn(), postnumre);

        } else if (nonNull(bostedadresse.getMatrikkeladresse())) {
            return formatMatrikkeladresse(bostedadresse.getMatrikkeladresse(),
                    bostedadresse.getAdresseIdentifikatorFraMatrikkelen(), bostedadresse.getCoAdressenavn(), kommunenumre);

        } else if (nonNull(bostedadresse.getUkjentBosted())) {
            return Stream.of("Ukjent bosted",
                            isNotBlank(bostedadresse.getUkjentBosted().getBostedskommune()) ?
                                    String.format("i kommune %s %s", bostedadresse.getUkjentBosted().getBostedskommune(),
                                            kommunenumre.get(bostedadresse.getUkjentBosted().getBostedskommune())) : null)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining(" "));

        } else if (nonNull(bostedadresse.getUtenlandskAdresse())) {
            return formatUtenlandskAdresse(bostedadresse.getUtenlandskAdresse(), bostedadresse.getCoAdressenavn(), landkoder);
        } else {
            return "";
        }
    }

    private static String getOppholdsadresse(OppholdsadresseDTO oppholdsadresse, Map<String, String> postnumre,
                                             Map<String, String> kommunenumre, Map<String, String> landkoder) {

        if (nonNull(oppholdsadresse.getVegadresse())) {
            return formatVegadresse(oppholdsadresse.getVegadresse(), oppholdsadresse.getCoAdressenavn(), postnumre);

        } else if (nonNull(oppholdsadresse.getMatrikkeladresse())) {
            return formatMatrikkeladresse(oppholdsadresse.getMatrikkeladresse(),
                    oppholdsadresse.getAdresseIdentifikatorFraMatrikkelen(), oppholdsadresse.getCoAdressenavn(), kommunenumre);

        } else if (nonNull(oppholdsadresse.getUtenlandskAdresse())) {
            return formatUtenlandskAdresse(oppholdsadresse.getUtenlandskAdresse(), oppholdsadresse.getCoAdressenavn(), landkoder);

        } else {
            return "";
        }
    }

    public Mono<Void> preparePersonSheet(XSSFWorkbook workbook,
                                         List<Testident> identer) {

        var sheet = workbook.createSheet(PERSON_FANE);
        var rows = getPersondataRowContents(identer);
        sheet.addIgnoredErrors(new CellRangeAddress(0, rows.size(), 0, HEADER.length),
                IgnoredErrorType.NUMBER_STORED_AS_TEXT);

        var columnNo = new AtomicInteger(0);
        Arrays.stream(COL_WIDTHS)
                .forEach(colWidth -> sheet.setColumnWidth(columnNo.getAndIncrement(), colWidth * 256));

        ExcelService.appendRows(workbook, PERSON_FANE,
                Stream.of(Collections.singletonList(HEADER), rows)
                        .flatMap(Collection::stream)
                        .toList());

        appendHyperlinks(workbook, rows);

        return Mono.empty();
    }

    private List<Object[]> getPersondataRowContents(List<Testident> hovedpersoner) {

        var start = System.currentTimeMillis();
        var personer = new ArrayList<>(getPersoner(hovedpersoner.stream().map(Testident::getIdent).toList(), hovedpersoner));

        log.info("Excel: hentet alle hovedpersoner, medgått tid er {} sekunder", (System.currentTimeMillis() - start) / 1000);
        start = System.currentTimeMillis();
        personer.addAll(getPersoner(Stream.of(
                                        getIdenterForRelasjon(personer, PARTNER),
                                        getIdenterForRelasjon(personer, BARN),
                                        getIdenterForRelasjon(personer, FORELDRE),
                                        getIdenterForRelasjon(personer, VERGE),
                                        getIdenterForRelasjon(personer, FULLMEKTIG)
                        )
                        .flatMap(Collection::stream)
                        .filter(ident -> hovedpersoner.stream().noneMatch(person -> person.getIdent().equals(ident)))
                        .distinct()
                        .toList(), hovedpersoner)
        );
        log.info("Excel: hentet alle relasjoner, medgått tid er {} sekunder", (System.currentTimeMillis() - start) / 1000);
        return personer;
    }

    private void appendHyperlinks(XSSFWorkbook workbook, List<Object[]> persondata) {

        appendHyperlinkRelasjon(workbook, PERSON_FANE, persondata, 0, PARTNER);
        appendHyperlinkRelasjon(workbook, PERSON_FANE, persondata, 0, BARN);
        appendHyperlinkRelasjon(workbook, PERSON_FANE, persondata, 0, FORELDRE);
        appendHyperlinkRelasjon(workbook, PERSON_FANE, persondata, 0, VERGE);
        appendHyperlinkRelasjon(workbook, PERSON_FANE, persondata, 0, FULLMEKTIG);
    }

    @SneakyThrows
    private List<Object[]> getPersoner(List<String> identer, List<Testident> testidenter) {

        return identer.isEmpty() ?
                Collections.emptyList() :

                Mono.zip(kodeverkConsumer.getKodeverkByName(LANDKODER),
                                kodeverkConsumer.getKodeverkByName(KOMMUNENR),
                                kodeverkConsumer.getKodeverkByName(POSTNUMMER))
                        .flatMapMany(kodeverk -> Flux.range(0, identer.size() / BLOCK_SIZE + 1)
                                .flatMap(index -> pdlPersonConsumer.getPdlPersoner(identer.subList(index * BLOCK_SIZE, Math.min((index + 1) * BLOCK_SIZE, identer.size())))
                                )
                                .filter(personbolk -> nonNull(personbolk.getData()))
                                .map(PdlPersonBolk::getData)
                                .map(PdlPersonBolk.Data::getHentPersonBolk)
                                .flatMap(Flux::fromIterable)
                                .filter(personBolk -> nonNull(personBolk.getPerson()))
                                .map(person -> prepDataRow(person, kodeverk, testidenter)))
                        .collectList()
                        .block();
    }

    private Object[] prepDataRow(PdlPersonBolk.PersonBolk person, Tuple3 kodeverk, List<Testident> identer) {
        return new Object[]{
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
                getStatsborgerskap(person.getPerson().getStatsborgerskap().stream().findFirst().orElse(null),
                        (Map<String, String>) kodeverk.getT1()),
                getAdressebeskyttelse(person.getPerson().getAdressebeskyttelse().stream().findFirst().orElse(null)),
                getBoadresse(person.getPerson().getBostedsadresse().stream().findFirst().orElse(new BostedadresseDTO()),
                        (Map<String, String>) kodeverk.getT3(),
                        (Map<String, String>) kodeverk.getT2(),
                        (Map<String, String>) kodeverk.getT1()),
                getKontaktadresse(person.getPerson().getKontaktadresse().stream().findFirst().orElse(new PdlPerson.Kontaktadresse()),
                        (Map<String, String>) kodeverk.getT3(),
                        (Map<String, String>) kodeverk.getT1()),
                getOppholdsadresse(person.getPerson().getOppholdsadresse().stream().findFirst().orElse(new OppholdsadresseDTO()),
                        (Map<String, String>) kodeverk.getT3(),
                        (Map<String, String>) kodeverk.getT2(),
                        (Map<String, String>) kodeverk.getT1()),
                getSivilstand(person.getPerson().getSivilstand().stream().findFirst().orElse(null)),
                getPartnere(person.getPerson().getSivilstand()),
                getBarn(person.getPerson().getForelderBarnRelasjon()),
                getForeldre(person.getPerson().getForelderBarnRelasjon()),
                getVerge(person.getPerson().getVergemaalEllerFremtidsfullmakt()),
                getFullmektig(person.getPerson().getFullmakt()),
                getSikkerhetstiltak(person.getPerson().getSikkerhetstiltak()),
                getIBruk(person, identer),
                getBeskrivelse(person, identer)
        };
    }

    private static String getIBruk(PdlPersonBolk.PersonBolk person, List<Testident> identer) {
        var testident = identer.stream()
                .filter(ident -> ident.getIdent().equals(person.getIdent()))
                .findAny();

        if (testident.isEmpty() || (testident.get().getIBruk() == null)) {
            return "";
        }

        return Boolean.TRUE.equals(testident.get().getIBruk()) ? "Ja" : "Nei";
    }

    private static String getBeskrivelse(PdlPersonBolk.PersonBolk person, List<Testident> identer) {
        var testident = identer.stream()
                .filter(ident -> ident.getIdent().equals(person.getIdent()))
                .findAny();

        if (testident.isPresent()) {
            return StringUtils.trimToEmpty(testident.get().getBeskrivelse());
        } else {
            return "";
        }
    }
}
