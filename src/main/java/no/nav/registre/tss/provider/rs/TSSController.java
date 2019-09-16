package no.nav.registre.tss.provider.rs;

import static no.nav.registre.tss.utils.Rutine110Util.fiksPosisjoner;
import static no.nav.registre.tss.utils.Rutine110Util.leggTilHeader;
import static no.nav.registre.tss.utils.Rutine110Util.setOppdater;
import static no.nav.registre.tss.utils.RutineUtil.TOTAL_LENGTH;
import static no.nav.registre.tss.utils.RutineUtil.padTilLengde;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.tss.domain.Person;
import no.nav.registre.tss.provider.rs.requests.SyntetiserTssRequest;
import no.nav.registre.tss.service.TSService;

@Slf4j
@RestController
@RequestMapping("api/v1/tss")
public class TSSController {

    @Autowired
    private TSService tssService;

    @PostMapping(value = "/opprettLeger")
    public ResponseEntity createDoctorsInTSS(@RequestBody SyntetiserTssRequest syntetiserTssRequest) {
        List<Person> ids = tssService.getIds(syntetiserTssRequest);
        List<String> tssQueueMessages = tssService.getMessagesFromSynt(ids);

        for (int i = 0; i < tssQueueMessages.size(); i++) {
            List<String> rutiner = new ArrayList<>(Arrays.asList(tssQueueMessages.get(i).split("\n")));
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < rutiner.size(); j++) {
                rutiner.set(j, padTilLengde(rutiner.get(j)));
                if (rutiner.get(j).length() != TOTAL_LENGTH) {
                    throw new RuntimeException("Feil lengde på rutine");
                }
                if (rutiner.get(j).startsWith("110")) {
                    rutiner.set(j, fiksPosisjoner(rutiner.get(j)));
                    rutiner.set(j, setOppdater(rutiner.get(j)));
                    rutiner.set(j, leggTilHeader(rutiner.get(j)));
                }
                s.append(rutiner.get(j));
            }
            tssQueueMessages.set(i, s.toString());
        }

        try {
            tssService.sendToMQQueue(tssQueueMessages);
        } catch (Exception e) {
            log.error("Kunne ikke sende til kø", e);
        }
        return ResponseEntity.status(HttpStatus.OK).body(tssQueueMessages);
    }

    @GetMapping("/hentLeger")
    public void getDoctorsFromTss(@RequestParam Long avspillergruppeId, @RequestParam(required = false) Integer antallLeger) {
        tssService.sendAndReceiveFromTss(avspillergruppeId, antallLeger);
    }
}
