package no.nav.registre.testnorge.organisasjonmottak.consumer.command;

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
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import no.nav.registre.testnorge.libs.dto.jenkins.v1.JenkinsCrumb;
import no.nav.registre.testnorge.organisasjonmottak.domain.Flatfil;

@Slf4j
@RequiredArgsConstructor
public class StartBEREG007Command implements Callable<Long> {
    private final WebClient webClient;
    private final String token;
    private final String server;
    private final String miljo;
    private final JenkinsCrumb crumb;
    private final Flatfil flatfil;

    private static Resource getFileResource(String content) throws IOException {
        Path tempFile = Files.createTempFile("ereg", ".txt");
        Files.write(tempFile, content.getBytes(StandardCharsets.UTF_8));
        File file = tempFile.toFile();
        return new FileSystemResource(file);
    }

    @SneakyThrows
    @Override
    public Long call() {
        log.info("Sender flatfil til server {} ({})", server, miljo);

        String content = flatfil.build();
        log.info("Flatfil inneholder: \n{}", content);

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
                .with("FileName", "dolly-" + System.currentTimeMillis() + ".txt")
                .with("overrideSequenceControl", "true")
                .with("stepSelection", "'2;3;4;5;6'")
                .with("input_file", fileEntity);

        String crumb = this.crumb.getCrumb();
        log.info("Jenkins-Crumb: {}", crumb);

        try {
            return webClient
                    .post()
                    .uri("/view/Registre/job/Start_BEREG007/buildWithParameters")
                    .header("Jenkins-Crumb", crumb)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(body)
                    .exchange()
                    .flatMap(response -> {
                        try {
                            var location = response.headers().asHttpHeaders().getLocation().toString();
                            var pattern = Pattern.compile("\\d+");
                            var matcher = pattern.matcher(location);
                            if (matcher.find()) {
                                return Mono.just(Long.valueOf(matcher.group()));
                            } else {
                                return Mono.error(new RuntimeException("Klarer ikke å finne item id fra location: " + location));
                            }
                        } catch (Exception e) {
                            log.error(
                                    "Klarer ikke å finne location. \nResponse body: {}\nLocation: {}\nRequest body: {}\nFile: {}",
                                    response.bodyToMono(String.class),
                                    response.headers().asHttpHeaders().getLocation(),
                                    body,
                                    fileEntity,
                                    e
                            );
                            return Mono.error(e);
                        }
                    }).block();
        } catch (WebClientResponseException e) {
            log.error(
                    "Feil ved innsending til jenkens batch BEREG007. Response: {}", e.getResponseBodyAsString(), e
            );
            throw e;
        } catch (Exception e) {
            log.error("Feil ved innsending til jenkens batch BEREG007.", e);
            throw e;
        }

    }
}
