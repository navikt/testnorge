package no.nav.registre.inntekt.consumer.rs.v2;

import static no.nav.registre.inntekt.utils.DatoParser.hentMaanedsnummerFraMaanedsnavn;
import static no.nav.tjenester.stub.aordningen.inntektsinformasjon.v2.inntekter.Inntektstype.LOENNSINNTEKT;
import static no.nav.tjenester.stub.aordningen.inntektsinformasjon.v2.inntekter.Inntektstype.NAERINGSINNTEKT;
import static no.nav.tjenester.stub.aordningen.inntektsinformasjon.v2.inntekter.Inntektstype.PENSJON_ELLER_TRYGD;
import static no.nav.tjenester.stub.aordningen.inntektsinformasjon.v2.inntekter.Inntektstype.YTELSE_FRA_OFFENTLIGE;

import com.google.common.collect.Lists;
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
import org.springframework.http.RequestEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.inntekt.domain.RsInntekt;
import no.nav.tjenester.stub.aordningen.inntektsinformasjon.v2.inntekter.Inntekt;
import no.nav.tjenester.stub.aordningen.inntektsinformasjon.v2.inntekter.Inntektsinformasjon;
import no.nav.tjenester.stub.aordningen.inntektsinformasjon.v2.inntekter.Inntektstype;

@Slf4j
@Component
public class InntektstubV2Consumer {

    private static final int PAGE_SIZE = 100;

    private final RestTemplate restTemplate;

    private final UriTemplate leggTilInntektUrl;
    private final UriTemplate hentEksisterendeIdenterUrl;
    private final UriTemplate hentEksisterendeInntekterUrl;

    public InntektstubV2Consumer(
            @Value("${inntektstub-t4.rest.api.url}") String inntektstubUrl
    ) {
        this.restTemplate = new RestTemplate(getHttpRequestFactory());
        this.leggTilInntektUrl = new UriTemplate(inntektstubUrl + "/v2/inntektsinformasjon?valider-inntektskombinasjoner=true&valider-kodeverk=true");
        this.hentEksisterendeIdenterUrl = new UriTemplate(inntektstubUrl + "/v2/personer");
        this.hentEksisterendeInntekterUrl = new UriTemplate(inntektstubUrl + "/v2/inntektsinformasjon?historikk=false&norske-identer={identer}");
    }

    private HttpComponentsClientHttpRequestFactory getHttpRequestFactory() {
        SSLConnectionSocketFactory sslsf;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(), NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory())
                .register("https", sslsf)
                .build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        cm.setMaxTotal(100);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setConnectionManager(cm)
                .build();

        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    public List<String> hentEksisterendeIdenter() {
        var getRequest = RequestEntity.get(hentEksisterendeIdenterUrl.expand()).build();
        return restTemplate.exchange(getRequest, new ParameterizedTypeReference<List<String>>() {
        }).getBody();
    }

    public List<Inntektsinformasjon> hentEksisterendeInntekterPaaIdenter(List<String> identer) {
        var partitions = Lists.partition(identer, PAGE_SIZE);
        List<Inntektsinformasjon> inntekter = new ArrayList<>();

        for (var page : partitions) {
            var identerAsString = String.join(",", page);
            var getRequest = RequestEntity.get(hentEksisterendeInntekterUrl.expand(identerAsString)).build();

            try {
                var responseBody = restTemplate.exchange(getRequest, new ParameterizedTypeReference<List<Inntektsinformasjon>>() {
                }).getBody();

                if (responseBody != null) {
                    inntekter.addAll(responseBody);
                }
            } catch (HttpStatusCodeException e) {
                log.error("Kunne ikke hente inntekt fra inntektstub", e);
            }
        }

        return inntekter;
    }

    public List<Inntektsinformasjon> leggInntekterIInntektstub(Map<String, List<RsInntekt>> identerMedInntekter) {
        List<Inntektsinformasjon> inntektsinformasjonTilIdenter = new ArrayList<>();
        for (var identerMedInntekterEntry : identerMedInntekter.entrySet()) {
            for (var periodeVirksomhetInntekterEntry : buildInntektsliste(identerMedInntekterEntry.getValue()).entrySet()) {
                periodeVirksomhetInntekterEntry.getValue().forEach((key, value) -> inntektsinformasjonTilIdenter.add(
                        Inntektsinformasjon.builder()
                                .norskIdent(identerMedInntekterEntry.getKey())
                                .inntektsliste(value)
                                .aarMaaned(YearMonth.parse(periodeVirksomhetInntekterEntry.getKey()))
                                .virksomhet(key)
                                .build()));
            }
        }

        var postRequest = RequestEntity.post(leggTilInntektUrl.expand()).body(inntektsinformasjonTilIdenter);
        List<Inntektsinformasjon> response = new ArrayList<>();
        try {
            List<Inntektsinformasjon> responseBody = restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<Inntektsinformasjon>>() {
            }).getBody();
            if (responseBody != null) {
                response = responseBody;
            }
        } catch (HttpStatusCodeException e) {
            log.error("Kunne ikke legge inntekt i inntektstub", e);
        }
        return response;
    }

    private Map<String, Map<String, List<Inntekt>>> buildInntektsliste(List<RsInntekt> rsInntekter) {
        Map<String, Map<String, List<Inntekt>>> periodeVirksomhetInntekterMap = new HashMap<>();
        for (var rsInntekt : rsInntekter) {
            var aarMaaned = YearMonth.of(Integer.parseInt(rsInntekt.getAar()), Month.of(hentMaanedsnummerFraMaanedsnavn(rsInntekt.getMaaned()))).toString();
            var virksomhet = rsInntekt.getVirksomhet();
            if (periodeVirksomhetInntekterMap.containsKey(aarMaaned)) {
                var virksomhetInntekterMap = periodeVirksomhetInntekterMap.get(aarMaaned);
                if (virksomhetInntekterMap.containsKey(virksomhet)) {
                    var inntekter = virksomhetInntekterMap.computeIfAbsent(virksomhet, k -> new ArrayList<>());
                    inntekter.add(buildInntektFromSyntInntekt(rsInntekt));
                } else {
                    virksomhetInntekterMap.put(virksomhet, new ArrayList<>(Collections.singletonList(buildInntektFromSyntInntekt(rsInntekt))));
                }
            } else {
                Map<String, List<Inntekt>> virksomhetInntekterMap = new HashMap<>();
                virksomhetInntekterMap.put(virksomhet, new ArrayList<>(Collections.singletonList(buildInntektFromSyntInntekt(rsInntekt))));
                periodeVirksomhetInntekterMap.put(aarMaaned, virksomhetInntekterMap);
            }
        }
        return periodeVirksomhetInntekterMap;
    }

    private Inntekt buildInntektFromSyntInntekt(RsInntekt rsInntekt) {
        return Inntekt.builder()
                .inntektstype(mapInntektstypeFromSyntToInntekt(rsInntekt.getInntektstype()))
                .utloeserArbeidsgiveravgift(rsInntekt.getUtloeserArbeidsgiveravgift())
                .inngaarIGrunnlagForTrekk(rsInntekt.getInngaarIGrunnlagForTrekk())
                .beloep(rsInntekt.getBeloep())
                .build();
    }

    private Inntektstype mapInntektstypeFromSyntToInntekt(String inntektstype) {
        if ("PensjonEllerTrygd".equals(inntektstype)) {
            return PENSJON_ELLER_TRYGD;
        } else if ("Loennsinntekt".equals(inntektstype)) {
            return LOENNSINNTEKT;
        } else if ("YtelseFraOffentlige".equals(inntektstype)) {
            return YTELSE_FRA_OFFENTLIGE;
        } else if ("Naeringsinntekt".equals(inntektstype)) {
            return NAERINGSINNTEKT;
        }
        throw new IllegalArgumentException("Kunne ikke finne inntektstype " + inntektstype);
    }
}
