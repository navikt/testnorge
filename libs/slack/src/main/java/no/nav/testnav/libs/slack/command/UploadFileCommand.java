package no.nav.testnav.libs.slack.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.testnav.libs.slack.dto.SlackResponse;


@RequiredArgsConstructor
public class UploadFileCommand implements Callable<SlackResponse> {
    private final WebClient webClient;
    private final String token;
    private final byte[] file;
    private final String fileName;
    private final String channels;
    private final String applicationName;

    @Override
    public SlackResponse call() {
        var bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", new FileNameByteArrayResource(fileName, file));
        bodyBuilder.part("channels", channels);
        bodyBuilder.part("title", "Fil sendt fra applikasjon: " + applicationName);

        return webClient.post()
                .uri("/api/files.upload")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .retrieve()
                .bodyToMono(SlackResponse.class)
                .block();
    }
}
