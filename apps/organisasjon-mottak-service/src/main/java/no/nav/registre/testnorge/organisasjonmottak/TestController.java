package no.nav.registre.testnorge.organisasjonmottak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Metadata;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class TestController {

    private final SendMetadata sendMetadata;


    @GetMapping
    public void test(){


        log.info("Sender");
        sendMetadata.send("dummy", Metadata.newBuilder().setMiljo("q2").build());
        log.info("sendt");

    }
}
