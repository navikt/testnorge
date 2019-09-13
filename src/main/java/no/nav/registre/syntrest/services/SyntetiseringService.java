package no.nav.registre.syntrest.services;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import no.nav.registre.syntrest.consumer.SyntConsumer;
import no.nav.registre.syntrest.controllers.request.InntektsmeldingInntekt;
import no.nav.registre.syntrest.response.Arbeidsforholdsmelding;
import no.nav.registre.syntrest.response.Barnebidragsmelding;
import no.nav.registre.syntrest.response.InntektsmeldingPopp;
import no.nav.registre.syntrest.response.Medlemskapsmelding;
import no.nav.registre.syntrest.response.AAP115Melding;
import no.nav.registre.syntrest.response.AAPMelding;
import no.nav.registre.syntrest.response.Institusjonsmelding;
import no.nav.registre.syntrest.response.SamMelding;
import no.nav.registre.syntrest.response.SkdMelding;
import no.nav.registre.syntrest.response.TPmelding;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SyntetiseringService {

    private final BeanFactory beanFactory;

    ///////////// NAMING AND URLs //////////////
    @Value("${synth-aareg-app}")
    private String aaregName;
    @Value("${synth-aareg-url}")
    private String aaregUrl;

    @Value("synth-arena-aap-app")
    private String arenaAAPName;
    @Value("synth-arena-aap-115-url")
    private String aap115Url;
    @Value("synth-arena-aap-nyRettighet-url")
    private String aapUrl;

    @Value("${synth-arena-bisys-app}")
    private String bisysName;
    @Value("${synth-arena-bisys-url}")
    private String bisysUrl;

    @Value("${synth-inst-app}")
    private String instName;
    @Value("${synth-inst-url}")
    private String instUrl;

    @Value("${synth-medl-app}")
    private String medlName;
    @Value("${synth-medl-url}")
    private String medlUrl;

    @Value("${synth-arena-meldekort-app}")
    private String meldekortName;
    @Value("${synth-arena-meldekort-url}")
    private String arenaMeldekortUrl;

    @Value("${synth-nav-app}")
    private String navEndringsmeldingName;
    @Value("${synth-nav-url}")
    private String navEndringsmeldingUrl;

    @Value("${synthdata-popp}")
    private String poppName;
    @Value("${synth-popp-url}")
    private String poppUrl;

    @Value("${synth-sam-app}")
    private String samName;
    @Value("${synth-sam-url}")
    private String samUrl;

    @Value("${synth-inntekt-app}")
    private String inntektName;
    @Value("${synth-inntekt-url}")
    private String inntektUrl;

    @Value("${synth-tp-app}")
    private String tpName;
    @Value("${synth-tp-url}")
    private String tpUrl;

    @Value("${synth-tps-app}")
    private String tpsName;
    @Value("${synth-tps-url}")
    private String tpsUrl;

    ///////////// CONSUMER FACTORY //////////////
    private final SyntConsumer aaregConsumer = beanFactory.getBean(SyntConsumer.class, aaregName);
    private final SyntConsumer arenaAAPConsumer = beanFactory.getBean(SyntConsumer.class, arenaAAPName);
    private final SyntConsumer bisysConsumer = beanFactory.getBean(SyntConsumer.class, bisysName);
    private final SyntConsumer instConsumer = beanFactory.getBean(SyntConsumer.class, instName);
    private final SyntConsumer medlConsumer = beanFactory.getBean(SyntConsumer.class, medlName);
    private final SyntConsumer meldekortConsumer = beanFactory.getBean(SyntConsumer.class, meldekortName);
    private final SyntConsumer navConsumer = beanFactory.getBean(SyntConsumer.class, navEndringsmeldingName);
    private final SyntConsumer poppConsumer = beanFactory.getBean(SyntConsumer.class, poppName);
    private final SyntConsumer samConsumer = beanFactory.getBean(SyntConsumer.class, samName);
    private final SyntConsumer inntektConsumer = beanFactory.getBean(SyntConsumer.class, inntektName);
    private final SyntConsumer tpConsumer = beanFactory.getBean(SyntConsumer.class, tpName);
    private final SyntConsumer tpsConsumer = beanFactory.getBean(SyntConsumer.class, tpsName);


    ///////////// GENERATE FUNCTIONS //////////////
    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-aareg" })
    public List<Arbeidsforholdsmelding> generateAaregData(List<String> fnrs) {
        return (List<Arbeidsforholdsmelding>) generateForFnrs(fnrs, aaregUrl, aaregConsumer);
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-arena-aap" })
    public List<AAP115Melding> generateAAP115Data(int numToGenerate) {
        return (List<AAP115Melding>) generateForNumbers(numToGenerate, aap115Url, arenaAAPConsumer);
    }
    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-arena-aap" })
    public List<AAPMelding> generateAAPData(int numToGenerate) {
        return (List<AAPMelding>) generateForNumbers(numToGenerate, aapUrl, arenaAAPConsumer);
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-arena-bisys" })
    public List<Barnebidragsmelding> generateBisysData(int numToGenerate) {
        return (List<Barnebidragsmelding>) generateForNumbers(numToGenerate, bisysUrl, bisysConsumer);
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-inst" })
    public List<Institusjonsmelding> generateInstData(int numToGenerate) {
        return (List<Institusjonsmelding>) generateForNumbers(numToGenerate, instUrl, instConsumer);
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-medl" })
    public List<Medlemskapsmelding> generateMedlData(int numToGenerate) {
        return (List<Medlemskapsmelding>) generateForNumbers(numToGenerate, medlUrl, medlConsumer);
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-meldekort" })
    public List<String> generateMeldekortData(int numToGenerate, String meldegruppe) {
        return (List<String>) generateForCodeAndNumber(meldegruppe, numToGenerate, arenaMeldekortUrl, meldekortConsumer);
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-nav" })
    public List<String> generateEndringsmeldingData(int numToGenerate, String endringskode) {
        return (List<String>) generateForCodeAndNumber(endringskode, numToGenerate, navEndringsmeldingUrl, navConsumer);
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-popp" })
    public List<InntektsmeldingPopp> generatePoppData(List<String> fnrs) {
        return (List<InntektsmeldingPopp>) generateForFnrs(fnrs, poppUrl, poppConsumer);
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-sam" })
    public List<SamMelding> generateSamMeldingData(int numToGenerate) {
        return (List<SamMelding>) generateForNumbers(numToGenerate, samUrl, samConsumer);
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-inntekt" })
    public Map<String, List<InntektsmeldingInntekt>> generateInntektData(Map<String, List<InntektsmeldingInntekt>> map) {
        UriTemplate uri = new UriTemplate(inntektUrl);
        RequestEntity request = RequestEntity.post(uri.expand()).body(map);
        return (Map<String, List<InntektsmeldingInntekt>>) inntektConsumer.synthesizeData(request);
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-tp" })
    public List<TPmelding> generateTPData(int numToGenerate) {
        return (List<TPmelding>) generateForNumbers(numToGenerate, tpUrl, tpConsumer);
    }

    @Timed(value = "syntrest.resource.latency", extraTags = { "operation", "synthdata-tps" })
    public List<SkdMelding> generateTPSData(int numToGenerate, String endringskode) {
        return (List<SkdMelding>) generateForCodeAndNumber(endringskode, numToGenerate, tpsUrl, tpsConsumer);
    }

    ///////////// COMMON FUNCTIONS /////////////
    private Object generateForCodeAndNumber(String code, int numToGenerate, String synthUrl, SyntConsumer syntConsumer) {
        UriTemplate uri = new UriTemplate(synthUrl);
        RequestEntity request = RequestEntity.get(uri.expand(numToGenerate, code)).build();
        return syntConsumer.synthesizeData(request);
    }

    private Object generateForFnrs(List<String> fnrs, String synthUrl, SyntConsumer syntConsumer) {
        UriTemplate uri = new UriTemplate(synthUrl);
        RequestEntity request = RequestEntity.post(uri.expand()).body(fnrs);
        return syntConsumer.synthesizeData(request);
    }

    private Object generateForNumbers(int numToGenerate, String synthUrl, SyntConsumer syntConsumer) {
        UriTemplate uri = new UriTemplate(synthUrl);
        RequestEntity request = RequestEntity.get(uri.expand(numToGenerate)).build();
        return syntConsumer.synthesizeData(request);
    }
}
