package no.nav.registre.tss.provider.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping(value = "/opprettLeger")
    public ResponseEntity createDoctorsInTSS(@RequestBody SyntetiserTssRequest syntetiserTssRequest) {
        List<Person> ids = tssService.getIds(syntetiserTssRequest);
        for (Person p : ids) {
            log.info(p.getNavn());
        }
        List<String> tssQueueMessages = tssService.getMessagesFromSynt(ids);

        try {
            tssService.sendToMQQueue(tssQueueMessages);
        } catch (Exception e) {
            log.error("Kunne ikke sende til kø", e);
        }
        log.info(tssQueueMessages.toString());
        return ResponseEntity.status(HttpStatus.OK).body(tssQueueMessages);
    }
}
