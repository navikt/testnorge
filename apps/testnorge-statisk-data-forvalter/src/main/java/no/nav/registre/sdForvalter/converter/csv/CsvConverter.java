package no.nav.registre.sdForvalter.converter.csv;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
abstract class CsvConverter<T> {
    private static final char DELIMITER = ';';

    @JsonIgnore
    abstract RowConverter<T> getRowConverter();

    @JsonIgnore
    abstract ObjectConverter<T> getObjectConverter();

    @JsonIgnore
    abstract CsvHeader[] getHeaders();

    public final void write(PrintWriter writer, List<T> rows) throws IOException {
        String[] headers = getHeadersAsString();
        CSVPrinter printer = CSVFormat.DEFAULT
                .withDelimiter(DELIMITER)
                .withHeader(headers)
                .print(writer);

        List<Map<String, Object>> collectionOfMap = rows
                .stream()
                .map(getObjectConverter()::convert)
                .collect(Collectors.toList());

        for (Map<String, Object> rowMap : collectionOfMap) {
            printRow(printer, rowMap, headers);
        }
        printer.close();
    }

    public final List<T> read(Reader reader) throws IOException {
        CSVParser parse = CSVFormat.DEFAULT
                .withDelimiter(DELIMITER)
                .withHeader(getHeadersAsString())
                .withFirstRecordAsHeader()
                .parse(reader);

        List<T> list = new ArrayList<>();
        for (CSVRecord record : parse.getRecords()) {
            list.add(getRowConverter().convert(recordToMap(record)));
        }
        reader.close();
        return list;
    }

    private Map<String, Object> recordToMap(CSVRecord record) {
        Map<String, Object> map = new HashMap<>();
        for (String header : getHeadersAsString()) {
            map.put(header, record.get(header));
        }
        return map;
    }

    private void printRow(CSVPrinter printer, Map<String, Object> row, String[] headers) throws IOException {
        for (String header : headers) {
            printer.print(row.get(header));
        }
        printer.println();
    }

    final String getString(Map<String, Object> row, CsvHeader header) {
        return getString(row.get(header.getValue()));
    }

    private String getString(Object original) {
        String value = (String) original;
        return value != null && value.isBlank() ? null : value;
    }

    private String[] getHeadersAsString() {
        return Arrays.stream(getHeaders()).map(CsvHeader::getValue).toArray(String[]::new);
    }
}