package no.nav.registre.ereg.csv;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Getter
@RequiredArgsConstructor
public class CsvReader {

    public static List<NaeringskodeRecord> read(String staticFilePath) throws IOException {
        InputStream inputFS = new ClassPathResource(staticFilePath).getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputFS, StandardCharsets.ISO_8859_1));

        ArrayList<NaeringskodeRecord> naeringskodeRecords = new ArrayList<>();

        try {
//            CSVReader reader = new CSVReader(br, ';');
            String line;
//            reader.readNext();
            br.readLine();
            while ((line = br.readLine()) != null) {
//                line[0] = removeUTF8BOM(line[0]);

                String[] splitArray = line.split(";");

                String[] split = Arrays.stream(splitArray)
                        .map(s -> s.substring(s.indexOf("\"") + 1, s.lastIndexOf("\""))).toArray(String[]::new);

                NaeringskodeRecord.NaeringskodeRecordBuilder builder = NaeringskodeRecord.builder()
                        .code(split[0])
                        .parentCode(split[1])
                        .level(split[2])
                        .name(split[3])
                        .shortName(split[4])
                        .notes(split[5]);

                if (split.length >= 7) {
                    builder.validFrom(split[6]);
                }
                if (split.length == 8) {
                    builder.validTo(split[7]);
                }

                naeringskodeRecords.add(builder.build());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return naeringskodeRecords;
    }

    private static String removeUTF8BOM(String s) {
        if (s.startsWith("\uFEFF")) {
            s = s.substring(1);
        }
        return s;
    }
}
