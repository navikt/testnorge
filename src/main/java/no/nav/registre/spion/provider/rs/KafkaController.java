package no.nav.registre.spion.provider.rs;

import no.nav.registre.spion.provider.rs.request.SyntetiserSpionRequest;
import no.nav.registre.spion.provider.rs.respone.SyntetiserSpionResponse;
import no.nav.registre.spion.service.VedtakPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/kafka")
public class KafkaController {

    private final VedtakPublisher vedtakPublisher;

    @Autowired
    private SyntetiseringController syntetiseringController;

    @Autowired
    public KafkaController(VedtakPublisher vedtakPublisher) {
        this.vedtakPublisher = vedtakPublisher;
    }

    @PostMapping(value = "/publish")
    public String sendSynVedakToKafkaTopic(@RequestParam("message") SyntetiserSpionRequest request){

        SyntetiserSpionResponse response = syntetiseringController.genererVedtak(request);

        return this.vedtakPublisher.publish(response);
    }
}
