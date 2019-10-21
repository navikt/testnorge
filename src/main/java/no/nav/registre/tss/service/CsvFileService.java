package no.nav.registre.tss.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.tss.domain.TssType;

@Slf4j
@Service
@NoArgsConstructor
public class CsvFileService {

    @Value("${ognrfil}")
    private String orgnrFilePath;

    public Map<TssType, List<String>> findExistingFromFile() {
        HashMap<TssType, List<String>> result = new HashMap<>();
        File file = new File(orgnrFilePath);
        if (!file.exists()) {
            return result;
        }
        try {
            Reader reader = Files.newBufferedReader(Paths.get(orgnrFilePath));
            CSVReader csvReader = new CSVReader(reader);
            List<String> typesAsString = Arrays.asList(csvReader.readNext());
            String[] nextRecord;

            for (String type : typesAsString) {
                result.put(TssType.valueOf(type), new ArrayList<>());
            }

            while ((nextRecord = csvReader.readNext()) != null) {
                for (int i = 0; i < typesAsString.size(); i++) {
                    var key = TssType.valueOf(typesAsString.get(i));
                    result.get(key).add(nextRecord[i]);
                }
            }

        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        }
        return result;
    }

    public void writeIfNotExist(Map<TssType, List<String>> orgnrByType) {

        File file = new File(orgnrFilePath);
        if (file.exists()) {
            return;
        }

        List<List<String>> rows = new ArrayList<>(orgnrByType.keySet().size());
        List<String> types = orgnrByType.keySet().stream().map(Enum::name).collect(Collectors.toList());

        for (String ignored : types) {
            rows.add(new ArrayList<>());
        }

        for (int i = 0; i < types.size(); i++) {
            List<String> orgnrs = orgnrByType.get(TssType.valueOf(types.get(i)));
            for (List<String> row : rows) {
                if (orgnrs.size() == i) {
                    break;
                }
                row.add(i, orgnrs.get(i));
            }
        }
        try {
            FileWriter outputfile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputfile);

            writer.writeNext((String[]) types.toArray());
            for (var row : rows) {
                writer.writeNext((String[]) row.toArray());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
