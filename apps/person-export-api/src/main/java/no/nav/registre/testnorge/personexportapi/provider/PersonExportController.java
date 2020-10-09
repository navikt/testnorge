package no.nav.registre.testnorge.personexportapi.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import no.nav.registre.testnorge.personexportapi.adapter.SlackAdapter;
import no.nav.registre.testnorge.personexportapi.consumer.TpsfConsumer;
import no.nav.registre.testnorge.personexportapi.converter.csv.NorskHelsenettPersonCsvConverter;
import no.nav.registre.testnorge.personexportapi.domain.Person;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/personer")
public class PersonExportController {
    private final TpsfConsumer consumer;
    private final SlackAdapter slackAdapter;

    @GetMapping(value = "/nhn/{avspillingsgruppe}")
    public void exportHelsenettFormat(@PathVariable("avspillingsgruppe") String avspillingsgruppe, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=testnorge-personer-nhn-" + LocalDateTime.now().toString() + ".csv"
        );

        List<Person> personer = consumer.getPersoner(avspillingsgruppe);
        log.info("Eksporterer {} personer til csv.", personer.size());


        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(byteStream);

        NorskHelsenettPersonCsvConverter.inst().write(pw, personer);
        slackAdapter.uploadFile(
                byteStream.toByteArray(),
                "testnorge-personer-nhn-" + LocalDateTime.now().toString() + ".csv"
        );

        NorskHelsenettPersonCsvConverter.inst().write(response.getWriter(), personer);
    }
}
