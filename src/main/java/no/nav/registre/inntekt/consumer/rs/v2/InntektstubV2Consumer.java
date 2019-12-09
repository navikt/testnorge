package no.nav.registre.inntekt.consumer.rs.v2;

import com.google.common.collect.Lists;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import no.nav.tjenester.stub.aordningen.inntektsinformasjon.v2.inntekter.Inntektsinformasjon;

@Component
public class InntektstubV2Consumer {

    private static final int PAGE_SIZE = 100;

    private final RestTemplate restTemplate;

    private final UriTemplate leggTilInntektUrl;
    private final UriTemplate hentEksisterendeIdenterUrl;
    private final UriTemplate hentEksisterendeInntekterUrl;

    public InntektstubV2Consumer(
            @Value("${inntektstub-u1.rest.api.url}") String inntektstubUrl
    ) {
        this.restTemplate = new RestTemplate(getHttpRequestFactory());
        this.leggTilInntektUrl = new UriTemplate(inntektstubUrl + "");
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
            var responseBody = restTemplate.exchange(getRequest, new ParameterizedTypeReference<List<Inntektsinformasjon>>() {
            }).getBody();
            if (responseBody != null) {
                inntekter.addAll(responseBody);
            }
        }

        return inntekter;
    }

    public List<Inntektsinformasjon> leggInntekterIInntektstub() {
        return null;
    }
}
