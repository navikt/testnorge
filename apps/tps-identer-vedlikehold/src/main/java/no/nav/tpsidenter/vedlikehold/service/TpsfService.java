package no.nav.tpsidenter.vedlikehold.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.tpsidenter.vedlikehold.consumers.TpsfConsumer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TpsfService {

    private final TpsfConsumer tpsfConsumer;

    @SneakyThrows
    public List<String> deleteIdents() {

        InputStream in = getClass().getResourceAsStream("/identer/identer-for-sletting.csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        List<String> status = new ArrayList<>();
        status.add("Starter sletting av identer fra TPSF ...");
        log.info("Starter sletting av identer fra TPSF ...");

        String ident = "";
        while (nonNull(ident = br.readLine())) {

            try {
                tpsfConsumer.deleteIdentInTpsf(ident);
                status.add(format("Ident %s slettet", ident));
                log.info("Ident {} slettet", ident);

            } catch (WebClientResponseException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    status.add(format("Ident %s ikke funnet",ident));
                    log.info("Ident {} ikke funnet", ident);
                } else {
                    status.add("Feil oppsto ved sletting av ident " + ident);
                    log.error("Feil oppsto ved sletting av ident {} ", ident, e);
                }
            } catch (RuntimeException e) {
                status.add("Feil oppsto ved sletting av ident " + ident);
                log.error("Feil oppsto ved sletting av ident {} ", ident, e);
            }
        }
        in.close();

        return status;
    }
}
