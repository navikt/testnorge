package no.nav.dolly.service.excel;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ExcelUtil {

    public static final String PERSON_FANE = "Personer";
    public static final String BANKKONTO_FANE = "Bankkontoer";

    public static final Object[] BANKKONTO_HEADER = {"Ident", "Kontonummer (Norge)", "Kontonummer (Utland)", "Banknavn", "Bankkode",
            "Banklandkode", "Valutakode", "Swift/Bickode", "Bankadresse1", "Bankadresse2", "Bankadresse3"};
    public static final Integer[] BANKKONTO_COL_WIDTHS = {14, 20, 20, 20, 20, 20, 20, 20, 30, 30, 30};
}
