package no.nav.testnav.apps.tpsmessagingservice.provider.v1;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.service.XmlService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/xml")
@RequiredArgsConstructor
public class XmlMessagingController {

    private final XmlService xmlService;

    @PostMapping
    @Operation(description = "Send xml-melding til en kø")
    public String sendXml(@RequestParam String queue,
                          @RequestBody String xml) {

        return xmlService.sendXml(xml, queue);
    }

    @GetMapping
    @Operation(description = "Henter relevante kø-navn")
    public List<String> getQueues() {

        return xmlService.getQueues();
    }
}
