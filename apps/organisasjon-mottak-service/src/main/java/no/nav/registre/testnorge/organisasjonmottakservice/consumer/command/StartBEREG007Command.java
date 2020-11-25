package no.nav.registre.testnorge.organisasjonmottakservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import no.nav.registre.testnorge.organisasjonmottakservice.consumer.request.JenkinsCrumb;
import no.nav.registre.testnorge.organisasjonmottakservice.domain.Flatfil;

@Slf4j
@RequiredArgsConstructor
public class StartBEREG007Command implements Runnable {
    private final WebClient webClient;
    private final String server;
    private final String miljo;
    private final JenkinsCrumb crumb;
    private final Flatfil flatfil;
    private final String username;
    private final String password;

    private static Resource getFileResource(String content) throws IOException {
        Path tempFile = Files.createTempFile("ereg_" + System.currentTimeMillis(), ".txt");
        Files.write(tempFile, content.getBytes(StandardCharsets.UTF_8));
        File file = tempFile.toFile();
        return new FileSystemResource(file);
    }

    @SneakyThrows
    @Override
    public void run() {
        log.info("Sender faltfil til server {} ({})", server, miljo);

        String content = flatfil.build();

        Resource resource = getFileResource(content);

        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        ContentDisposition contentDisposition = ContentDisposition
                .builder("form-data")
                .name("input_file")
                .filename(resource.getFilename())
                .build();
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
        fileMap.set("Content-Type", ContentType.TEXT_PLAIN.withCharset(StandardCharsets.ISO_8859_1).toString());
        var fileEntity = new HttpEntity<>(content.toUpperCase().getBytes(StandardCharsets.ISO_8859_1), fileMap);

        var body = BodyInserters
                .fromMultipartData("server", server)
                .with("batchName", "BEREG007")
                .with("workUnit", "100")
                .with("FileName", "dolly-" + UUID.randomUUID() + ".txt")
                .with("overrideSequenceControl", "true")
                .with("input_file", fileEntity);

        webClient
                .post()
                .uri("/view/Registre/job/Start_BEREG007/buildWithParameters")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .header("Jenkins-Crumb", crumb.getCrumb())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .headers(headers -> headers.setBasicAuth(username, password))
                .body(body)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
