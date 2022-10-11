package no.nav.dolly.service.excel;

import lombok.experimental.UtilityClass;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class ExcelUtil {

    public static final String PERSON_FANE = "Personer";
    public static final String BANKKONTO_FANE = "Bankkontoer";

    public static final Object[] BANKKONTO_HEADER = {"Ident", "Kontonummer (Norge)", "Kontonummer (Utland)", "Banknavn", "Bankkode",
            "Banklandkode", "Valutakode", "Swift/Bickode", "Bankadresse1", "Bankadresse2", "Bankadresse3"};
    public static final Integer[] BANKKONTO_COL_WIDTHS = {14, 20, 20, 20, 20, 20, 20, 20, 30, 30, 30};

    public static void appendHyperlinkRelasjon(XSSFWorkbook workbook,
                                        String fane,
                                        List<Object[]> verdier,
                                        int indekskolonne,
                                        int kolonne) {

        var hyperLinks = createLinkReferanser(verdier, indekskolonne);
        IntStream.range(0, verdier.size()).boxed()
                .filter(row -> isNotBlank((String) verdier.get(row)[kolonne]))
                .forEach(row -> appendHyperLink(workbook, fane,
                        row + 1,
                        kolonne,
                        verdier.get(row)[kolonne],
                        hyperLinks));
    }

    private static Map<String, Integer> createLinkReferanser(List<Object[]> opplysninger, int indekskolonne) {

        return IntStream.range(0, opplysninger.size()).boxed()
                .collect(Collectors.toMap(row -> (String) opplysninger.get(row)[indekskolonne], row -> row,
                        (row1, row2) -> row1));
    }

    @SuppressWarnings("all")
    private static void appendHyperLink(XSSFWorkbook workbook, String sheet, int row, int column,
                                        Object verdier,
                                        Map<String, Integer> linkRefs) {

        var enheter = getEnheter(verdier);
        if (enheter.stream().anyMatch(linkRefs::containsKey)) {
            var cell = workbook.getSheet(sheet).getRow(row).getCell(column);
            cell.setHyperlink(createHyperLink(workbook.getCreationHelper(), linkRefs.get(
                    enheter.stream()
                            .filter(linkRefs::containsKey)
                            .findFirst()
                            .get()), sheet));
            cell.setCellStyle(createHyperlinkCellStyle(workbook));
        }
    }

    private static XSSFCellStyle createHyperlinkCellStyle(XSSFWorkbook workbook) {

        var hyperlinkStyle = workbook.createCellStyle();
        var hLinkFont = workbook.createFont();
        hLinkFont.setFontName("Ariel");
        hLinkFont.setUnderline(org.apache.poi.ss.usermodel.Font.U_SINGLE);
        hLinkFont.setColor(IndexedColors.BLUE.getIndex());
        hyperlinkStyle.setFont(hLinkFont);
        hyperlinkStyle.setWrapText(true);
        return hyperlinkStyle;
    }

    private static List<String> getEnheter(Object object) {

        return Stream.of(object)
                .map(Object::toString)
                .map(enheter -> enheter.split(","))
                .map(Arrays::asList)
                .flatMap(Collection::stream)
                .map(String::trim)
                .toList();
    }

    private static Hyperlink createHyperLink(CreationHelper helper, Integer row, String fane) {

        var hyperLink = helper.createHyperlink(HyperlinkType.DOCUMENT);
        hyperLink.setAddress(String.format("'%s'!A%d", fane, row + 2));
        return hyperLink;
    }
}
