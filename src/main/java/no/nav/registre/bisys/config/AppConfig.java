package no.nav.registre.bisys.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.bisys.consumer.rs.BisysSyntetisererenConsumer;
import no.nav.registre.bisys.consumer.rs.request.BisysRequestAugments;
import no.nav.registre.bisys.consumer.ui.BisysUiSupport;
import no.nav.registre.bisys.consumer.ui.modules.BisysUiFatteVedtakConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Configuration
public class AppConfig {

    public static final String STANDARD_DATE_FORMAT_BISYS = "dd.MM.yyyy";
    public static final String STANDARD_DATE_FORMAT_TESTNORGEBISYS_REQUEST = "yyyy-MM-dd";

    // Will be set to true for BMs household (forskudd)
    private final static boolean barnRegistrertPaaAdresse = false;

    @Value("${ANDEL_FORSORGING}")
    String andelForsorging;

    @Value("${BISYS_URL}")
    String bisysUrl;

    @Value("${ENHET}")
    String enhet;

    @Value("${GEBYR_BESLAARSAK_KODE_FRITATT_IKKE_SOKT}")
    String gebyrBeslAarsakKodeFritattIkkeSokt;

    @Value("${testnorge-hodejegeren.rest-api.url}")
    String hodejegerenUrl;

    @Value("${INNTEKT_BM_EGNE_OPPLYSNINGER:0}")
    private int inntektBmEgneOpplysninger;

    @Value("${INNTEKT_BP_EGNE_OPPLYSNINGER:0}")
    private int inntektBpEgneOpplysninger;

    @Value("${KODE_UNNT_FORSK}")
    private String kodeUnntForsk;

    @Value("${SAKSBEHANDLER_ROLLE}")
    String rolleSaksbehandler;

    @Value("${SYNTBISYS_PASSWORD}")
    String saksbehandlerPwd;

    @Value("${SYNTBISYS_USERNAME}")
    String saksbehandlerUid;

    @Value("${SAMVARSKLASSE}")
    String samvarsklasse;

    @Value("${SIVILSTAND_BM}")
    private String sivilstandBm;

    @Value("${syntrest.rest.api.url}")
    String syntrestServerUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public BisysSyntetisererenConsumer bisysSyntetisererenConsumer() {
        return new BisysSyntetisererenConsumer(syntrestServerUrl);
    }

    @Bean
    public BisysUiSupport bisysUiNavigationSupport() {
        return new BisysUiSupport(saksbehandlerUid, saksbehandlerPwd, bisysUrl,
                rolleSaksbehandler, Integer.parseInt(enhet));
    }

    @Bean
    public BisysRequestAugments bisysRequestAugments() {

        String gebyrBeslAarsakKode = gebyrBeslAarsakKodeFritattIkkeSokt.equals("1")
                ? BisysUiFatteVedtakConsumer.KODE_BESL_AARSAK_FRITATT_IKKE_SOKT
                : BisysUiFatteVedtakConsumer.KODE_BESL_AARSAK_ILAGT_IKKE_SOKT;

        return BisysRequestAugments.builder()
                .barnRegistrertPaaAdresse(barnRegistrertPaaAdresse)
                .inntektBmEgneOpplysninger(inntektBmEgneOpplysninger)
                .inntektBpEgneOpplysninger(inntektBpEgneOpplysninger)
                .andelForsorging(andelForsorging)
                .gebyrBeslAarsakKode(gebyrBeslAarsakKode)
                .kodeUnntForsk(kodeUnntForsk)
                .samvarsklasse(samvarsklasse)
                .sivilstandBm(sivilstandBm)
                .build();
    }

    @Bean
    @DependsOn("restTemplate")
    public HodejegerenConsumer hodejegerenConsumer() {
        return new HodejegerenConsumer(hodejegerenUrl, restTemplate());
    }
}
