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
    public List<String> opprettLeger(@RequestParam String miljoe, @RequestBody List<String> identer) {
        if (!STOETTEDE_MILJOER.contains(miljoe.toLowerCase())) {
            throw new IllegalArgumentException("Miljø " + miljoe + " er ikke støttet. Støttede miljøer: " + STOETTEDE_MILJOER);
        }
        return identService.opprettLegerITss(miljoe.toLowerCase(), identer);
    }
}
