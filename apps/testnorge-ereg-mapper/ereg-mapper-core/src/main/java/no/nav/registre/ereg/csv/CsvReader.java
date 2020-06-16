package no.nav.registre.ereg.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public class CsvReader {

    public static List<NaeringskodeRecord> read(String staticFilePath) throws IOException {
        InputStream inputFS = new ClassPathResource(staticFilePath).getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputFS, StandardCharsets.UTF_8));
        ArrayList<NaeringskodeRecord> naeringskodeRecords = new ArrayList<>();
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(';')
                .withIgnoreQuotations(false)
                .build();

        CSVReader csvReader = new CSVReaderBuilder(br)
                .withSkipLines(0)
                .withCSVParser(parser)
                .build();
        String[] line;
        br.readLine();
        while ((line = csvReader.readNext()) != null) {

            for (int i = 0; i < line.length; i++)
                line[i] = line[i].trim();

            NaeringskodeRecord.NaeringskodeRecordBuilder builder = NaeringskodeRecord.builder()
                    .code(line[0])
                    .parentCode(line[1])
                    .level(line[2])
                    .name(line[3])
                    .shortName(line[4])
                    .notes(line[5]);

            if (line.length >= 7) {
                builder.validFrom(line[6]);
            }
            if (line.length == 8) {
                builder.validTo(line[7]);
            }

            naeringskodeRecords.add(builder.build());
        }
        return naeringskodeRecords;
    }

}
