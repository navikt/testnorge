package no.nav.testnav.endringsmeldingservice.controller;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.endringsmeldingservice.service.DoedsmeldingService;
import no.nav.testnav.endringsmeldingservice.service.FoedselsmeldingService;
import no.nav.testnav.libs.dto.endringsmelding.v2.FoedselsmeldingDTO;
import no.nav.testnav.libs.dto.endringsmelding.v2.FoedselsmeldingResponseDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Set;

@RestController
@RequestMapping("/api/v2/endringsmelding")
@RequiredArgsConstructor
public class SkdEndringsmeldingerController {

    private final FoedselsmeldingService foedselsmeldingService;
    private final DoedsmeldingService doedsmeldingService;

    @PostMapping("/foedeselsmelding")
    public Mono<FoedselsmeldingResponseDTO> sendFoedselsmelding(
            @RequestHeader Set<String> miljoer,
            @RequestBody FoedselsmeldingDTO dto) {

        return foedselsmeldingService.sendFoedselsmelding(dto, miljoer);
    }

//    @PostMapping("/doedsmelding")
//    public Mono<ResponseEntity<?>> sendFoedselsmelding(
//            @RequestHeader Set<String> miljoer,
//            @RequestBody DoedsmeldingDTO doedsmelding) {
//
//        return doedsmeldingService.sendDoedsmelding(doedsmelding, miljoer)
//                .map(status -> {
//                    if (!status.isOk()) {
//                        return ResponseEntity.badRequest().body(status.getErrors());
//                    }
//                    return ResponseEntity.ok(status.getPersonId());
//                });
//    }
//
//    @DeleteMapping("/doedsmelding")
//    public Mono<ResponseEntity<?>> kansellerFoedselsmelding(
//            @RequestHeader Set<String> miljoer,
//            @RequestBody KansellerDoedsmeldingDTO kansellerDoedsmelding) {
//
//        return doedsmeldingService.sendKansellerDoedsmelding(kansellerDoedsmelding.getIdent(), miljoer)
//                .map(status -> {
//                    if (!status.isOk()) {
//                        return ResponseEntity.badRequest().body(status.getErrors());
//                    }
//                    return ResponseEntity.ok(status.getPersonId());
//                });
//    }
}
