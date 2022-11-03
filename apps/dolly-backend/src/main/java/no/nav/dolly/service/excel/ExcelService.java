package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.TestgruppeRepository;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelService {

    private final TestgruppeRepository testgruppeRepository;
    private final PersonExcelService personExcelService;
    private final BankkontoExcelService bankkontoExcelService;
    private final OrganisasjonExcelService organisasjonExcelService;

    protected static void appendRows(XSSFWorkbook workbook, String fane, List<Object[]> rows) {

        var wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);
        var sheet = workbook.getSheet(fane);

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
                                    if (text.contains(",") || text.length() > 25) {
                                        cell.setCellStyle(wrapStyle);
                                    }
                                } else {
                                    cell.setCellValue((Integer) cellValue);
                                }
                            });
                });
    }

    public Resource getExcelWorkbook(Long gruppeId) {

        long timestamp = System.currentTimeMillis();
        var testgruppe = testgruppeRepository.findById(gruppeId)
                .orElseThrow(() -> new NotFoundException("Testgruppe ikke funnet for id " + gruppeId));

        var testidenter = testgruppe
                .getTestidenter().stream()
                .toList();

        var workbook = new XSSFWorkbook();

        Mono.zip(
                        personExcelService.preparePersonSheet(workbook, testidenter),
                        bankkontoExcelService.prepareBankkontoSheet(workbook, testgruppe))
                .block();

        BankkontoToPersonHelper.appendData(workbook);

        return convertToResource(timestamp, workbook);
    }

    public Resource getExcelOrganisasjonerWorkbook(Bruker bruker) {

        long timestamp = System.currentTimeMillis();

        var workbook = new XSSFWorkbook();

        organisasjonExcelService.prepareOrganisasjonSheet(workbook, bruker);

        return convertToResource(timestamp, workbook);
    }

    private Resource convertToResource(long timestamp, XSSFWorkbook workbook) {

        log.info("Excel: totalt medgått tid {} sekunder", (System.currentTimeMillis() - timestamp) / 1000);
        try {
            var excelFile = File.createTempFile("Excel-", ".xlsx");
            try (var outputStream = new FileOutputStream(excelFile)) {
                workbook.write(outputStream);
                workbook.close();
                return new FileSystemResource(excelFile);
            }

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new DollyFunctionalException("Generering av Excel-fil feilet", e);
        }
    }
}
