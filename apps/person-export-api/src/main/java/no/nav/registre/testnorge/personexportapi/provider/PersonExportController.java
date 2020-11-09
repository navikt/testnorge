package no.nav.registre.testnorge.personexportapi.provider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.personexportapi.adapter.SlackAdapter;
import no.nav.registre.testnorge.personexportapi.consumer.TpsfConsumer;
import no.nav.registre.testnorge.personexportapi.converter.csv.HelsenettCsvConverter;
import no.nav.registre.testnorge.personexportapi.domain.Person;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/personer")
public class PersonExportController {

    private final TpsfConsumer consumer;
    private final SlackAdapter slackAdapter;
    private final HttpServletResponse response;
    private final HelsenettCsvConverter helsenettCsvConverter;

    @GetMapping(value = "/nhn/{avspillingsgruppe}")
    public void exportHelsenettFormat(@PathVariable("avspillingsgruppe") String avspillingsgruppe) throws IOException {

        long startTime = System.nanoTime();

        List<Person> personer = consumer.getPersoner(avspillingsgruppe);
        log.info("Eksporterer {} personer til csv til Slack", personer.size());

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(byteStream);

        helsenettCsvConverter.write(pw, personer);
        slackAdapter.uploadFile(
                byteStream.toByteArray(),
                "testnorge-personer-nhn-" + LocalDateTime.now().toString() + ".csv"
        );

//        log.info("Eksporterer {} personer til http response", personer.size());
//        response.setContentType("text/csv");
//        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
//        response.setHeader(
//                "Content-Disposition",
//                "attachment; filename=testnorge-personer-nhn-" + LocalDateTime.now().toString() + ".csv"
//        );
//        helsenettCsvConverter.write(response.getWriter(), personer);

        log.info("Medgaatt tid til uttrekk {} ", getReadableTime(System.nanoTime() - startTime));
    }

    private static String getReadableTime(Long nanos) {

        long tempSec = nanos / (1000 * 1000 * 1000);
        long sec = tempSec % 60;
        long min = (tempSec / 60) % 60;
        long hour = (tempSec / (60 * 60)) % 24;
        long day = (tempSec / (24 * 60 * 60)) % 24;

        return String.format("%dd %dh %dm %ds", day, hour, min, sec);
    }
}
