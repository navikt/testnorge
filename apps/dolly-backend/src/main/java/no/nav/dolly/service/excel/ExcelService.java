package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.TestgruppeRepository;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelService {

    private final TestgruppeRepository testgruppeRepository;
    private final PersonExcelService personExcelService;

    public Resource getExcelWorkbook(Long gruppeId) {

        var testidenter = testgruppeRepository.findById(gruppeId)
                .orElseThrow(() -> new NotFoundException("Testgruppe ikke funnet for id " + gruppeId))
                .getTestidenter().stream()
                .map(Testident::getIdent)
                .toList();

        var workbook = new XSSFWorkbook();

        var wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);

        personExcelService.preparePersonSheet(workbook, wrapStyle, testidenter);

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
