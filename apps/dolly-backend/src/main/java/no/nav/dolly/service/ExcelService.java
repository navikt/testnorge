package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.TestgruppeRepository;
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
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static wiremock.org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelService {

    private final TestgruppeRepository testgruppeRepository;
    private final PdlDataConsumer pdlDataConsumer;

    private static Integer getAlder(String ident, LocalDate doedsdato) {

        return (int) ChronoUnit.YEARS.between(
                DatoFraIdentUtil.getDato(ident),
                isNull(doedsdato) ? LocalDate.now() : doedsdato.atStartOfDay());
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

    private static void appendRows(HSSFSheet sheet, List<Object[]> rows) {

        var rowCount = new AtomicInteger(0);
        rows.stream()
                .forEach(rowValue -> {
                    var row = sheet.createRow(rowCount.getAndIncrement());
                    var cellCount = new AtomicInteger(0);
                    Arrays.stream(rowValue)
                            .forEach(cellValue -> {
                                var cell = row.createCell(cellCount.getAndIncrement());
                                if (cellValue instanceof String) {
                                    cell.setCellValue((String) cellValue);
                                } else {
                                    cell.setCellValue((Integer) cellValue);
                                }
                            });
                });
    }

    public Resource getExcelWorkbook(Long gruppeId) {

        var gruppe = testgruppeRepository.findById(gruppeId)
                .orElseThrow(() -> new NotFoundException("Testgruppe ikke funnet for id " + gruppeId));

        var workbook = new HSSFWorkbook();
        var sheet = workbook.createSheet("Personer");

        var pdlfPersoner = getPdlf(gruppe.getTestidenter().stream()
                .filter(Testident::isPdlf)
                .map(Testident::getIdent)
                .toList());

//        appendRows(HSSFSheet sheet, Stream.generate()etHeader().)

        try {
            var excelFile = File.createTempFile("Excel-", ".xls");
            try (var outputStream = new FileOutputStream(excelFile)) {
                workbook.write(outputStream);
            }
            return new FileSystemResource(excelFile);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DollyFunctionalException("Generering av Excel-fil feilet", e);
        }
    }

    private String[] getHeader() {

        return new String[]{"Ident", "Identtype", "Fornavn", "Etternavn", "Alder", "Kjønn", "Dødsdato", "Personstatus",
                "Statsborgerskap", "Adressebeskyttelse", "Bostedsadresse", "Kontaktadresse", "Oppholdsadresse",
                "Sivilstand", "Barn1", "Barn2", "Barn3"};

//                ,"Gateadresse","Husnummer","Gatekode","Postnr","Kommunenr","Flyttedato",""
//                "Postlinje1","Postlinje2","Postlinje3","Postland","InnvandretFraLand","GtVerdi","GtType","GtRegel",""
//                "Språkkode","Statsborgerskap","TypeSikkerhetTiltak","BeskrivelseSikkerhetTiltak",""
//                "Relasjon1-Type","Relasjon1-Ident","Relasjon2-Type","Relasjon2-Ident","Relasjon3-Type","Relasjon3-Ident%n"};

    }

    private List<Object[]> getPdlf(List<String> identer) {

        var personer = pdlDataConsumer.getPersoner(identer, 0, identer.size());

        return personer.stream()
                .map(person -> new Object[]{
                        person.getPerson().getIdent(),
                        IdentTypeUtil.getIdentType(person.getPerson().getIdent()),
                        getFornavn(person.getPerson().getNavn().stream().findFirst().orElse(new NavnDTO())),
                        person.getPerson().getNavn().stream().findFirst().orElse(new NavnDTO()).getEtternavn(),
                        getAlder(person.getPerson().getIdent(), person.getPerson().getDoedsfall().isEmpty() ? null :
                                person.getPerson().getDoedsfall().stream().findFirst().get().getDoedsdato().toLocalDate()),
                        person.getPerson().getKjoenn().stream().findFirst().orElse(new KjoennDTO()).getKjoenn().name(),
                        person.getPerson().getDoedsfall().stream().findFirst().orElse(new DoedsfallDTO()).getDoedsdato(),
                        person.getPerson().getFolkeregisterPersonstatus().stream().findFirst()
                                .orElse(new FolkeregisterPersonstatusDTO()).getStatus(),
                        person.getPerson().getStatsborgerskap().stream().findFirst().orElse(new StatsborgerskapDTO()).getLandkode(),
                        person.getPerson().getAdressebeskyttelse().stream().findFirst().orElse(new AdressebeskyttelseDTO()).getGradering(),
                        getBoadresse(person.getPerson().getBostedsadresse().stream().findFirst().orElse(new BostedadresseDTO())),
                        getKontaktadresse(person.getPerson().getKontaktadresse().stream().findFirst().orElse(new KontaktadresseDTO())),
                        getOppholdsadresse(person.getPerson().getOppholdsadresse().stream().findFirst().orElse(new OppholdsadresseDTO())),
                        person.getPerson().getSivilstand().stream().findFirst().orElse(new SivilstandDTO()).getType()
                })
                .toList();
    }
}
