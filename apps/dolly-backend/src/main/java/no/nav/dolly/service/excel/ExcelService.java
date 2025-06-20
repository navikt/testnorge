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
import reactor.core.publisher.Flux;
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
        rows.forEach(rowValue -> {
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

    public Mono<Resource> getExcelWorkbook(Long gruppeId) {

        long timestamp = System.currentTimeMillis();
        return testgruppeRepository.findById(gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException("Testgruppe ikke funnet for id " + gruppeId)))
                .flatMap(testgruppe -> Mono.just(new XSSFWorkbook())
                .flatMap(workbook -> Flux.merge(
                                personExcelService.preparePersonSheet(workbook, testgruppe),
                                bankkontoExcelService.prepareBankkontoSheet(workbook, testgruppe))
                        .collectList()
                        .doOnNext(resultat -> BankkontoToPersonHelper.appendData(workbook))
                        .then(Mono.fromCallable(() -> convertToResource(timestamp, workbook)))));
    }

    public Mono<Resource> getExcelOrganisasjonerWorkbook(String brukerId) {

        long timestamp = System.currentTimeMillis();

        var workbook = new XSSFWorkbook();

        return organisasjonExcelService.prepareOrganisasjonSheet(workbook, brukerId)
                .then(Mono.just(convertToResource(timestamp, workbook)));
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
