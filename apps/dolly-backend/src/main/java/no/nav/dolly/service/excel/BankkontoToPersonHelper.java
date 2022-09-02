package no.nav.dolly.service.excel;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.IgnoredErrorType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static no.nav.dolly.service.excel.ExcelUtil.BANKKONTO_COL_WIDTHS;
import static no.nav.dolly.service.excel.ExcelUtil.BANKKONTO_FANE;
import static no.nav.dolly.service.excel.ExcelUtil.BANKKONTO_HEADER;
import static no.nav.dolly.service.excel.ExcelUtil.PERSON_FANE;

@UtilityClass
public class BankkontoToPersonHelper {

    public static void appendData(XSSFWorkbook workbook) {

        var bankData = workbook.getSheet(BANKKONTO_FANE);
        if (bankData.getPhysicalNumberOfRows() == 0) {

            return;
        }

        var personData = workbook.getSheet(PERSON_FANE);
        var startColumn = personData.getRow(0).getPhysicalNumberOfCells() + 4;
        var relativeEndColumn = bankData.getRow(0).getPhysicalNumberOfCells();

        appendHeader(personData, startColumn, bankData, relativeEndColumn);

        var wrapStyle = workbook.createCellStyle();
        wrapStyle.setWrapText(true);
        appendRows(personData, startColumn, bankData, relativeEndColumn, wrapStyle);

        personData.addIgnoredErrors(new CellRangeAddress(0, personData.getPhysicalNumberOfRows(),
                        startColumn, startColumn + relativeEndColumn),
                IgnoredErrorType.NUMBER_STORED_AS_TEXT);
    }

    private static void appendHeader(XSSFSheet personData, int startColumn, XSSFSheet bankData, int relativeEndColumn) {

        var headerRow = personData.getRow(0);
        for (int i = 1; i < relativeEndColumn; i++) {

            headerRow.createCell(startColumn + i)
                    .setCellValue((String) BANKKONTO_HEADER[i]);
            personData.setColumnWidth(startColumn + i, BANKKONTO_COL_WIDTHS[i] * 256);
        }
    }

    private static void appendRows(XSSFSheet personData, int startColumn,
                                   XSSFSheet bankData, int relativeEndColumn,
                                   XSSFCellStyle wrapStyle) {

        var formatter = new DataFormatter();

        for (int i = 1; i < bankData.getPhysicalNumberOfRows(); i++) {

            var bankDataRow = bankData.getRow(i);
            var ident = formatter.formatCellValue(bankDataRow.getCell(0));
            var personDataMatchRow = getPersonDataRow(personData, ident);

            for (int j = 1; j < relativeEndColumn; j++) {

                var dataCell = personDataMatchRow.createCell(startColumn + j);
                var dataValue = formatter.formatCellValue(bankDataRow.getCell(j));

                dataCell.setCellValue(dataValue);
                if (dataValue.contains(",") || dataValue.length() > 25) {
                    dataCell.setCellStyle(wrapStyle);
                }
            }
        }
    }

    private static XSSFRow getPersonDataRow(XSSFSheet personData, String ident) {

        var rowMatch = personData.getPhysicalNumberOfRows();

        var formatter = new DataFormatter();
        for (int i = 1; i < personData.getPhysicalNumberOfRows(); i++) {
            if (formatter.formatCellValue(personData.getRow(i).getCell(0)).equals(ident)) {
                rowMatch = i;
                break;
            }
        }
        return personData.getRow(rowMatch);
    }
}
