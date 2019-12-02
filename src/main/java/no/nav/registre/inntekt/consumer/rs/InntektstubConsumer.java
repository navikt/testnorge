package no.nav.registre.inntekt.consumer.rs;

import static no.nav.registre.inntekt.utils.DatoParser.hentMaanedsnummerFraMaanedsnavn;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;
import java.time.Month;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import no.nav.registre.inntekt.domain.RsInntekt;
import no.nav.registre.inntekt.domain.RsInntektsinformasjonsType;
import no.nav.registre.inntekt.domain.RsPerson;
import no.nav.registre.inntekt.domain.RsUser;
import no.nav.tjenester.stub.aordningen.inntektsinformasjon.v2.inntekter.Inntekt;
import no.nav.tjenester.stub.aordningen.inntektsinformasjon.v2.inntekter.Inntektsinformasjon;
import no.nav.tjenester.stub.aordningen.inntektsinformasjon.v2.inntekter.Inntektstype;

@Slf4j
@Component
public class InntektstubConsumer {

    private static final ParameterizedTypeReference<Map<String, List<RsInntekt>>> RESPONSE_TYPE_OPPRETT_INNTEKT = new ParameterizedTypeReference<>() {
    };

    private static final ParameterizedTypeReference<List<RsInntekt>> RESPONSE_TYPE_HENT_INNTEKT = new ParameterizedTypeReference<>() {
    };

    private static final ParameterizedTypeReference<List<RsPerson>> RESPONSE_TYPE_PERSON = new ParameterizedTypeReference<>() {
    };

    private String token;

    private RestTemplate restTemplate;

    private UriTemplate leggTilInntektUrl;
    private UriTemplate hentEksisterendeIdenterUrl;
    private UriTemplate hentEksisterendeInntekterUrl;
    private UriTemplate hentTokenUrl;

    public InntektstubConsumer(@Value("${inntektstub.rest.api.url}") String inntektstubUrl,
            @Value("${testnorges.ida.credential.inntektstub.username}") String username,
            @Value("${testnorges.ida.credential.inntektstub.password}") String password) {

        final SSLConnectionSocketFactory sslsf;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(), NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        final Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslsf)
                .build();

        final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(100);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setConnectionManager(cm)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        this.restTemplate = new RestTemplate(factory);
        this.restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));

        this.leggTilInntektUrl = new UriTemplate(inntektstubUrl + "/v1/personer/inntekt");
        this.hentEksisterendeIdenterUrl = new UriTemplate(inntektstubUrl + "/v1/personer");
        this.hentEksisterendeInntekterUrl = new UriTemplate(inntektstubUrl + "/v1/person/{ident}/inntekter");
        this.hentTokenUrl = new UriTemplate(inntektstubUrl + "/v1/user");
    }

    public Map<String, List<RsInntekt>> leggInntekterIInntektstub(Map<String, List<RsInntekt>> inntekter) {
        try {
            HttpEntity<Map<String, List<RsInntekt>>> entity = new HttpEntity<>(inntekter, getHeaders());
            return restTemplate.exchange(leggTilInntektUrl.expand(), HttpMethod.POST, entity, RESPONSE_TYPE_OPPRETT_INNTEKT).getBody();
        } catch (HttpClientErrorException e) {
            log.warn(e.getMessage(), e);
            throw e;
        }
    }

    public void leggInntekterIInntektstubV2(String ident, RsInntekt inntekt) {
        String inntektstype = inntekt.getInntektstype();
        Double beloep = inntekt.getBeloep();
        Boolean inngaarIGrunnlagForTrekk = inntekt.getInngaarIGrunnlagForTrekk();
        Boolean utloeserArbeidsgiveravgift = inntekt.getUtloeserArbeidsgiveravgift();
        RsInntektsinformasjonsType inntektsinformasjonsType = inntekt.getInntektsinformasjonsType();

        Inntektsinformasjon inntektsinformasjon =
                Inntektsinformasjon.builder()
                        .norskIdent(ident)
                        .inntektsliste(Collections.singletonList(
                                Inntekt.builder()
                                        .inntektstype(Inntektstype.valueOf(inntektstype))
                                        .utloeserArbeidsgiveravgift(utloeserArbeidsgiveravgift)
                                        .inngaarIGrunnlagForTrekk(inngaarIGrunnlagForTrekk)
                                        .beloep(beloep)
                                        .build()
                                )
                        )
                        .aarMaaned(YearMonth.of(Integer.parseInt(inntekt.getAar()), Month.of(hentMaanedsnummerFraMaanedsnavn(inntekt.getMaaned()))))
                        .virksomhet(inntekt.getVirksomhet())
                        .build();
    }

    public List<RsPerson> hentEksisterendeIdenter() {
        try {
            HttpEntity entity = new HttpEntity<>(getHeaders());
            return restTemplate.exchange(hentEksisterendeIdenterUrl.expand(), HttpMethod.GET, entity, RESPONSE_TYPE_PERSON).getBody();
        } catch (HttpClientErrorException e) {
            log.warn(e.getMessage(), e);
            throw e;
        }
    }

    public List<RsInntekt> hentEksisterendeInntekterPaaIdent(String ident) {
        try {
            HttpEntity entity = new HttpEntity<>(getHeaders());
            return restTemplate.exchange(hentEksisterendeInntekterUrl.expand(ident), HttpMethod.GET, entity, RESPONSE_TYPE_HENT_INNTEKT).getBody();
        } catch (HttpClientErrorException e) {
            log.warn(e.getMessage(), e);
            throw e;
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders session = getSession();
        if (session.containsKey("X-XSRF-TOKEN")) {
            List<String> tokenList = session.get("X-XSRF-TOKEN");
            if (tokenList != null) {
                this.token = tokenList.get(0);
            }
        } else {
            session.add("X-XSRF-TOKEN", token);
        }
        session.add("Content-Type", "application/json");
        return session;
    }

    private HttpHeaders getSession() {
        HttpEntity request = new HttpEntity("", new HttpHeaders());
        //Have to call this twice to get a valid token
        HttpEntity<RsUser> response1 = restTemplate.exchange(hentTokenUrl.expand(), HttpMethod.GET, request, RsUser.class);
        request = new HttpEntity("", getHeaders(response1));
        HttpEntity<RsUser> response = restTemplate.exchange(hentTokenUrl.expand(), HttpMethod.GET, request, RsUser.class);
        return getHeaders(response);
    }

    private HttpHeaders getHeaders(HttpEntity response) {
        HttpHeaders headers = response.getHeaders();
        HttpHeaders headersRet = new HttpHeaders();
        if (!headers.containsKey("Set-Cookie")) {
            return headersRet;
        }
        for (String entry : headers.get("Set-Cookie")) {
            String token = entry.substring(entry.indexOf('=') + 1, entry.indexOf(';'));
            String name = entry.substring(0, entry.indexOf('='));
            if (name.contains("XSRF")) {
                StringBuilder buf = new StringBuilder(name);
                buf.insert(0, "X-");
                headersRet.add(buf.toString(), token);
            }
        }
        return headersRet;
    }
}
