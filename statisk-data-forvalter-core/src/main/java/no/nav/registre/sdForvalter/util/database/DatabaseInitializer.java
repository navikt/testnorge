package no.nav.registre.sdForvalter.util.database;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.Entity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.database.ModelEnum;

public class DatabaseInitializer {

    public static List<Entity> initializeFromCsv(String staticFilePath, ModelEnum dataType, String splitToken) throws IOException {
        File inputF = new ClassPathResource(staticFilePath).getFile();
        InputStream inputFS = new FileInputStream(inputF);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputFS));

        List<String> headers = Arrays.asList(br.readLine().split(splitToken));
        return br.lines().map(line -> DataEntityFactory.create(dataType, Arrays.asList(line.split(splitToken)), headers)).collect(Collectors.toList());
    }

}
