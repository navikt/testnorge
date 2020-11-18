package no.nav.registre.orgnrservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

import no.nav.registre.orgnrservice.service.OrgnummerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orgnummer")
public class OrgnummerController {

    OrgnummerService orgnummerService;

    @GetMapping
    public List<String> getOrgnummer (@RequestHeader Integer antall) {

        var a = orgnummerService.generate();

        //sjekk om orgnr finnes i EREG

        return Collections.singletonList(a);
    }
}
