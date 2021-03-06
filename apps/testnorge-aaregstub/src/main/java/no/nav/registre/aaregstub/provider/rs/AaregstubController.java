package no.nav.registre.aaregstub.provider.rs;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.registre.aaregstub.arbeidsforhold.ArbeidsforholdsResponse;
import no.nav.registre.aaregstub.arbeidsforhold.Ident;
import no.nav.registre.aaregstub.arbeidsforhold.contents.Arbeidsforhold;
import no.nav.registre.aaregstub.service.ArbeidsforholdService;

//import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptions;

@RestController
@RequestMapping("api/v1")
public class AaregstubController {

    @Autowired
    private ArbeidsforholdService arbeidsforholdService;

    //    @LogExceptions
    @ApiOperation(value = "Her kan man lagre arbeidsforhold i stubben")
    @PostMapping(value = "/lagreArbeidsforhold")
    public List<String> lagreArbeidsforhold(@RequestBody List<ArbeidsforholdsResponse> arbeidsforholdsmeldinger) {
        return arbeidsforholdService.lagreArbeidsforhold(arbeidsforholdsmeldinger);
    }

    //    @LogExceptions
    @ApiOperation(value = "Her kan man hente ut alle arbeidsforhold-idene som er lagret i stubben.")
    @GetMapping(value = "/hentArbeidsforholdIder")
    public List<BigInteger> hentArbeidsforholdIder() {
        return arbeidsforholdService.hentAlleArbeidsforholdIder();
    }

    //    @LogExceptions
    @ApiOperation(value = "Her kan man hente ut arbeidsforholdet med den angitte id-en.")
    @GetMapping(value = "/hentArbeidsforhold/{id}")
    public Arbeidsforhold hentArbeidsforhold(@PathVariable Long id) {
        Optional<Arbeidsforhold> arbeidsforhold = arbeidsforholdService.hentArbeidsforhold(id);
        return arbeidsforhold.orElse(null);
    }

    //    @LogExceptions
    @ApiOperation(value = "Her kan man slette arbeidsforholdet med den angitte id-en fra stubben. Arbeidsforholdet blir ikke slettet fra aareg hvis det er lagret der.")
    @Transactional
    @DeleteMapping(value = "/slettArbeidsforhold/{id}")
    public void slettArbeidsforhold(@PathVariable Long id) {
        arbeidsforholdService.slettArbeidsforhold(id);
    }

    //    @LogExceptions
    @ApiOperation(value = "Her kan man hente ut alle identene som har fått opprettet arbeidsforhold i stubben.")
    @GetMapping(value = "/hentAlleArbeidstakere")
    public List<String> hentAlleArbeidstakere() {
        return arbeidsforholdService.hentAlleArbeidstakere();
    }

    //    @LogExceptions
    @ApiOperation(value = "Her kan man hente ut alle arbeidsforholdene som er opprettet i stubben på en gitt ident.")
    @GetMapping(value = "/hentIdentMedArbeidsforhold/{ident}")
    public Ident hentIdentMedArbeidsforhold(@PathVariable String ident) {
        return arbeidsforholdService.hentIdentMedArbeidsforhold(ident);
    }

    @ApiOperation(value = "Her kan man hente ut id-ene til alle arbeidsforholdene til en liste med identer.")
    @GetMapping(value = "/hentIdenterMedIder")
    public Map<String, List<Long>> hentIdenterMedIder(@RequestParam List<String> identer) {
        Map<String, List<Long>> identerMedArbeidsforholdIder = new HashMap<>();
        for (String fnr : identer) {
            Ident ident = arbeidsforholdService.hentIdentMedArbeidsforhold(fnr);
            if (ident != null) {
                identerMedArbeidsforholdIder.put(ident.getFnr(), ident.getArbeidsforhold().stream().map(Arbeidsforhold::getId).collect(Collectors.toList()));
            }
        }
        return identerMedArbeidsforholdIder;
    }

    @ApiOperation(value = "Her kan man slette en ident og alle tilhørende arbeidsforhold fra stubben.")
    @Transactional
    @DeleteMapping(value = "/slettIdent/{ident}")
    public List<Long> slettIdent(@PathVariable String ident) {
        List<Long> forholdSomSkalSlettes = new ArrayList<>();
        Ident identen = arbeidsforholdService.hentIdentMedArbeidsforhold(ident);
        if (identen != null) {
            forholdSomSkalSlettes.addAll(identen.getArbeidsforhold().stream().map(Arbeidsforhold::getId).collect(Collectors.toList()));
        }

        for (Long id : forholdSomSkalSlettes) {
            arbeidsforholdService.slettArbeidsforhold(id);
        }

        return forholdSomSkalSlettes;
    }
}
