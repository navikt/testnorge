package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.TestgruppeRepository;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelService {

    private final TestgruppeRepository testgruppeRepository;
    private final PdlfExcelService pdlfExcelService;

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

        var pdlfPersoner = pdlfExcelService.getPdlfCells(gruppe.getTestidenter().stream()
                .filter(Testident::isPdlf)
                .map(Testident::getIdent)
                .toList());

        appendRows(sheet, Stream.of(Collections.singletonList(getHeader()), pdlfPersoner)
                .flatMap(Collection::stream)
                .toList());

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

    private Object[] getHeader() {

        return new String[]{"Ident", "Identtype", "Fornavn", "Etternavn", "Alder", "Kjønn", "Dødsdato", "Personstatus",
                "Statsborgerskap", "Adressebeskyttelse", "Bostedsadresse", "Kontaktadresse", "Oppholdsadresse",
                "Sivilstand", "Barn1", "Barn2", "Barn3"};

//                ,"Gateadresse","Husnummer","Gatekode","Postnr","Kommunenr","Flyttedato",""
//                "Postlinje1","Postlinje2","Postlinje3","Postland","InnvandretFraLand","GtVerdi","GtType","GtRegel",""
//                "Språkkode","Statsborgerskap","TypeSikkerhetTiltak","BeskrivelseSikkerhetTiltak",""
//                "Relasjon1-Type","Relasjon1-Ident","Relasjon2-Type","Relasjon2-Ident","Relasjon3-Type","Relasjon3-Ident%n"};

    }
}
