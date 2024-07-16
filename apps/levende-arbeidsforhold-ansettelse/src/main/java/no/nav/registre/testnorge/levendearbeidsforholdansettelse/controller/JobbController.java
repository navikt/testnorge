package no.nav.registre.testnorge.levendearbeidsforholdansettelse.controller;


import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameter;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.JobbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
