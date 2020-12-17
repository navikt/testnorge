package no.nav.registre.orgnrservice.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import no.nav.registre.orgnrservice.adapter.OrganisasjonAdapter;
import no.nav.registre.orgnrservice.domain.Organisasjon;
import no.nav.registre.orgnrservice.service.GenerateOrgnummerService;
import no.nav.registre.orgnrservice.service.OrgnummerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orgnummer")
public class OrgnummerController {

    private final OrgnummerService orgnummerService;
    private final GenerateOrgnummerService generateOrgnummerService;
    private final OrganisasjonAdapter adapter;

    @GetMapping
    public List<String> getOrgnummer (@RequestHeader Integer antall) {
        return orgnummerService.generateOrgnrs(antall);
    }

    @GetMapping("/v2")
    public List<String> getOrgnummer2 (@RequestHeader Integer antall) {
        return orgnummerService.hentOrgnr(antall);
    }

    @PutMapping
    public Organisasjon setLedig (@RequestHeader String orgnummer, @RequestParam( defaultValue = "true") boolean ledig) {
        return  adapter.setIsLedigByOrgnummer(orgnummer, ledig);
    }

    //Endepunkt for å teste under utvikling
    @PostMapping
    public List<Organisasjon> genererOrgnummerTilDb(@RequestHeader Integer antall) {
        return orgnummerService.genererOrgnrsTilDb(antall, true);
    }

//    //Endepunkt for å teste under utvikling
//    @PostMapping("/ett")
//    public Organisasjon lagreOrgnr (@RequestHeader String orgnr, @RequestParam( defaultValue = "true") boolean ledig) {
//        var a  = new Organisasjon(orgnr, ledig);
//        return adapter.save(a);
//    }


//    //Endepunkt for å teste under utvikling
//    @GetMapping("/ledige")
//    public List<String> hentAlleLedige() {
//        return adapter.hentAlleLedige().stream().map(Organisasjon::getOrgnummer).collect(Collectors.toList());
//    }
//
//    //Endepunkt for å teste under utvikling
//    @DeleteMapping
//    public ResponseEntity<HttpStatus> deleteByOrgnummer (@RequestParam String orgnummer) {
//        Organisasjon organisasjon = adapter.hentByOrgnummer(orgnummer);
//        if (organisasjon == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        adapter.deleteByOrgnummer(orgnummer);
//        return ResponseEntity.ok().build();
//    }
}