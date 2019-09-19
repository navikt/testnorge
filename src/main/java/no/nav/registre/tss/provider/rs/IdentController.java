package no.nav.registre.tss.provider.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.nav.registre.tss.service.IdentService;

@RestController
@RequestMapping("api/v1/ident")
public class IdentController {

    private static final Set<String> STOETTEDE_MILJOER = new HashSet<>(Arrays.asList("q1", "q2"));

    @Autowired
    private IdentService identService;

    @PostMapping
    public void opprettLeger(@RequestParam String miljoe, @RequestBody List<String> identer) {
        if (!STOETTEDE_MILJOER.contains(miljoe)) {
            throw new IllegalArgumentException("Miljø " + miljoe + " er ikke støttet. Støttede miljøer: " + STOETTEDE_MILJOER);
        }
        identService.opprettLegerITss(miljoe, identer);
        // bruk fnrs i listen med identer og finn tilhørende navn i tpsf (husk på miljø her) / ELLER: nytt endepunkt i hodejegeren som returnerer navn og litt diverse fra status-quo?
        // bruk syntpakken til å opprette syntetiske meldinger på identene
        // legg identene inn i tss (husk på miljø her)
    }
}
