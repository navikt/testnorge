package no.nav.registre.tss.service;

import com.opencsv.CSVReader;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.tss.domain.TssType;

@Slf4j
@Service
@NoArgsConstructor
public class CsvFileService {

    @Value("${ognrfil}")
    private String orgnrFilePath;

    public Map<TssType, List<String>> findExistingFromFile() {
        HashMap<TssType, List<String>> result = new HashMap<>();
        Resource file = new ClassPathResource(orgnrFilePath);
        if (!file.exists()) {
            return result;
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource(orgnrFilePath).getInputStream()));
            CSVReader csvReader = new CSVReader(reader);
            List<String> typesAsString = Arrays.asList(csvReader.readNext());
            String[] nextRecord;

            for (String type : typesAsString) {
                result.put(TssType.valueOf(type.trim()), new ArrayList<>());
            }

            while ((nextRecord = csvReader.readNext()) != null) {
                for (int i = 0; i < typesAsString.size(); i++) {
                    var key = TssType.valueOf(typesAsString.get(i));
                    if (nextRecord.length <= i) {
                        continue;
                    }
                    if (nextRecord[i] == null || nextRecord[i].isEmpty() || nextRecord[i].trim().isEmpty()) {
                        continue;
                    }
                    result.get(key).add(nextRecord[i]);
                }
            }

        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        }
        return result;
    }
}
