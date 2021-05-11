package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/arbeidsforhold")
public class GenererArbeidsforholdController {


    @PostMapping("/populate")
    public ResponseEntity<HttpStatus> getOpplysningsplikitgOrgnummer(
            @RequestHeader String miljo,
            @RequestHeader Integer max
    ) {
        return ResponseEntity.noContent().build();
    }

}
