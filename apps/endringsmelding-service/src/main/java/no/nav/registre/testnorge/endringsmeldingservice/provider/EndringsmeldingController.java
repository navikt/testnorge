package no.nav.registre.testnorge.endringsmeldingservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import no.nav.registre.testnorge.endringsmeldingservice.consumer.TpsForvalterConsumer;
import no.nav.registre.testnorge.libs.dto.endringsmelding.v1.DoedsmeldingDTO;
import no.nav.registre.testnorge.libs.dto.endringsmelding.v1.FoedselsmeldingDTO;

@RestController
@RequestMapping("/api/v1/endringsmelding")
@RequiredArgsConstructor
public class EndringsmeldingController {

    private final TpsForvalterConsumer consumer;

    @PostMapping("/foedeselsmelding")
    public ResponseEntity<String> sendFoedselsmelding(
            @RequestHeader Set<String> miljoer,
            @RequestBody FoedselsmeldingDTO dto
    ) {
        var ident = consumer.sendFoedselsmelding(dto, miljoer);
        return ResponseEntity.ok(ident);
    }

    @PostMapping("/doedsmelding")
    public ResponseEntity<HttpStatus> sendFoedselsmelding(
            @RequestHeader Set<String> miljoer,
            @RequestBody DoedsmeldingDTO dto
    ) {
        consumer.sendDoedsmelding(dto, miljoer);
        return ResponseEntity.ok().build();
    }

}
