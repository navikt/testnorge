package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.TestgruppeRepository;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    private static final Object[] header = {"Ident", "Identtype", "Fornavn", "Etternavn", "Alder", "Kjønn", "Dødsdato",
            "Personstatus", "Statsborgerskap", "Adressebeskyttelse", "Bostedsadresse", "Kontaktadresse", "Oppholdsadresse",
            "Sivilstand", "Partner", "Barn", "Foreldre", "Verge", "Fullmektig"};
    private static final Integer[] COL_WIDTHS = {12, 10, 20, 20, 6, 8, 12, 18, 15, 15, 25, 25, 25, 12, 14, 14, 14, 14, 14};

    private final TestgruppeRepository testgruppeRepository;
    private final PersonExcelService personExcelService;

    private static void appendRows(XSSFSheet sheet, CellStyle wrapStyle, List<Object[]> rows) {

        sheet.addIgnoredErrors(new CellRangeAddress(0, rows.size(), 0, 0), IgnoredErrorType.NUMBER_STORED_AS_TEXT);

        var columnNo = new AtomicInteger(0);
        Arrays.stream(COL_WIDTHS)
                .forEach(colWidth -> sheet.setColumnWidth(columnNo.getAndIncrement(), colWidth * 256));

        sheet.setColumnWidth(0, 15 * 256);
        var rowCount = new AtomicInteger(0);
        rows.stream()
                .forEach(rowValue -> {
                    var row = sheet.createRow(rowCount.getAndIncrement());
                    var cellCount = new AtomicInteger(0);
                    Arrays.stream(rowValue)
                            .forEach(cellValue -> {
                                var cell = row.createCell(cellCount.getAndIncrement());
                                if (cellValue instanceof String text) {
                                    cell.setCellValue(text);
                                    if (text.contains(",")) {
                                        cell.setCellStyle(wrapStyle);
                                    }
                                } else {
                                    cell.setCellValue((Integer) cellValue);
                                }
                            });
                });
    }

    public Resource getExcelWorkbook(Long gruppeId) {

        var gruppe = testgruppeRepository.findById(gruppeId)
                .orElseThrow(() -> new NotFoundException("Testgruppe ikke funnet for id " + gruppeId));

        var workbook = new XSSFWorkbook();

        var sheet = workbook.createSheet("Personer");
        var wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);

        appendRows(sheet, wrapStyle, Stream.of(Collections.singletonList(header),
                        personExcelService.getFormattedCellsFromPdl(gruppe.getTestidenter().stream()
                                .map(Testident::getIdent)
                                .toList()))
                .flatMap(Collection::stream)
                .toList());

        try {
            var excelFile = File.createTempFile("Excel-", ".xlsx");
            try (var outputStream = new FileOutputStream(excelFile)) {
                workbook.write(outputStream);
                workbook.close();
                return new FileSystemResource(excelFile);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new DollyFunctionalException("Generering av Excel-fil feilet", e);
        }
    }
}
