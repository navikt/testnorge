package no.nav.registre.testnorge.levendearbeidsforholdansettelse.controller;


import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobber")
@RequiredArgsConstructor
public class JobbController {
/*
    @Autowired
    private final JobbService jobbService;

    @GetMapping("/{id}")
    public List<JobbParameter> hentAlleJobber(@PathVariable String id) {
        return jobbService.hentParametere();
    }

    @GetMapping("/")
    public String helloworld(){
        return "Hello world";
    }

 */
}
