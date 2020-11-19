package no.nav.registre.testnorge.organisasjonmottakservice.consumer;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import no.nav.registre.testnorge.organisasjonmottakservice.domain.Flatfil;

@Component
public class EregConsumer {

    private static Resource getFileResource(String content) throws IOException {
        Path tempFile = Files.createTempFile("ereg", ".txt");
        Files.write(tempFile, content.getBytes(StandardCharsets.UTF_8));
        File file = tempFile.toFile();
        return new FileSystemResource(file);
    }

    public void save(Flatfil flatfil, String miljo){

    }

}
