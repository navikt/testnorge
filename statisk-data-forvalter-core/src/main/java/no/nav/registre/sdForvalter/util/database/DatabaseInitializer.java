package no.nav.registre.sdForvalter.util.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.database.ModelEnum;
import no.nav.registre.sdForvalter.database.model.factory.DataEntityFactory;

@Slf4j
public class DatabaseInitializer {

    public static List<Object> initializeFromCsv(String staticFilePath, ModelEnum dataType, String splitToken) throws IOException {
        File inputF = new ClassPathResource(staticFilePath).getFile();
        InputStream inputFS = new FileInputStream(inputF);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputFS, StandardCharsets.ISO_8859_1));

        List<String> headers = Arrays.asList(br.readLine().split(splitToken));

        return br.lines().map(line -> {
            Object entity = null;
            try {
                if (line.isEmpty()) {
                    log.warn("Read empty line");
                    return null;
                }
                List<String> args = Arrays.asList(line.split(splitToken));
                if (args.size() == 0) {
                    log.warn("Split gave 0 inputs to creation of model");
                }
                entity = DataEntityFactory.create(dataType, args, headers);
            } catch (IllegalArgumentException e) {
                log.warn("Manglende informasjon i datasettet");
                log.warn(e.getMessage(), e);
            }
            return entity;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
