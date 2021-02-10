package no.nav.registre.testnorge.libs.csvconverter.v1;

import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public abstract class CsvPrinterConverter<T> {
    private static final char DELIMITER = ';';
    private final CSVPrinter printer;

    @SneakyThrows
    public CsvPrinterConverter(PrintWriter writer) {
        this.printer = CSVFormat.DEFAULT
                .withDelimiter(DELIMITER)
                .withHeader(getHeadersAsString())
                .print(writer);
    }

    protected abstract ObjectConverter<T> getObjectConverter();

    protected abstract CsvHeader[] getHeaders();

    public final void write(List<T> rows) throws IOException {
        String[] headers = getHeadersAsString();
        List<Map<String, Object>> collectionOfMap = rows
                .parallelStream()
                .map(getObjectConverter()::convert)
                .collect(Collectors.toList());

        for (Map<String, Object> rowMap : collectionOfMap) {
            printRow(printer, rowMap, headers);
        }
    }

    public void close() throws IOException {
        printer.close();
    }

    private void printRow(CSVPrinter printer, Map<String, Object> row, String[] headers) throws IOException {
        for (String header : headers) {
            printer.print(row.get(header));
        }
        printer.println();
    }

    private String[] getHeadersAsString() {
        return Arrays.stream(getHeaders()).map(CsvHeader::getValue).toArray(String[]::new);
    }
}