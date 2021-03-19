package no.nav.tpsidenter.vedlikehold.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.tpsidenter.vedlikehold.consumers.SlackAdapter;
import no.nav.tpsidenter.vedlikehold.consumers.TpsfConsumer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TpsfService {

    private final TpsfConsumer tpsfConsumer;
    private final SlackAdapter slackAdapter;

    @SneakyThrows
    public String deleteIdents(MultipartFile file) {

        InputStream in = file.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        StringBuilder status = new StringBuilder();
        status.append("Starter sletting av identer fra TPSF ...\n");
        log.info("Starter sletting av identer fra TPSF ...");

        String ident = "";
        while (nonNull(ident = br.readLine())) {

            try {
                tpsfConsumer.deleteIdentInTpsf(ident);
                status.append("Ident ")
                        .append(ident)
                        .append(" slettet\n");
                log.info("Ident {} slettet", ident);

            } catch (WebClientResponseException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    status.append("Ident ")
                            .append(ident)
                            .append(" ikke funnet\n");
                    log.info("Ident {} ikke funnet", ident);
                } else {
                    status.append("Feil oppsto ved sletting av ident ")
                            .append(ident)
                            .append('\n');
                    log.error("Feil oppsto ved sletting av ident {} ", ident, e);
                }
            } catch (RuntimeException e) {
                status.append("Feil oppsto ved sletting av ident ")
                        .append(ident)
                        .append('\n');
                log.error("Feil oppsto ved sletting av ident {} ", ident, e);
            }
        }
        in.close();

        slackAdapter.uploadFile(
                status.toString().getBytes(StandardCharsets.UTF_8),
                format("tps-identer-vedlikehold-%s.csv", LocalDateTime.now().toString())
        );

        return status.toString();
    }
}
